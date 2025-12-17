package com.example.itsme.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.itsme.domain.Menu;
import com.example.itsme.domain.MenuLike;
import com.example.itsme.domain.MenuLikeId;
import com.example.itsme.domain.User;
import com.example.itsme.dto.MenuLikeRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.MenuLikeRepository;
import com.example.itsme.repository.MenuRepository;
import com.example.itsme.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menu-likes")
@RequiredArgsConstructor
@Validated
public class MenuLikeController {

	private final MenuLikeRepository menuLikeRepository;
	private final UserRepository userRepository;
	private final MenuRepository menuRepository;

	@GetMapping
	public List<MenuLike> getLikes() {
		return menuLikeRepository.findAll();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MenuLike likeMenu(@Valid @RequestBody MenuLikeRequest request) {
		User user = fetchUser(request.userId());
		Menu menu = fetchMenu(request.menuId());
		MenuLikeId id = MenuLikeId.builder()
				.userId(user.getUserId())
				.menuId(menu.getMenuId())
				.build();
		if (menuLikeRepository.existsById(id)) {
			return menuLikeRepository.findById(id).orElseThrow();
		}
		MenuLike menuLike = MenuLike.builder()
				.id(id)
				.user(user)
				.menu(menu)
				.build();
		return menuLikeRepository.save(menuLike);
	}

	@DeleteMapping("/{userId}/{menuId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void unlikeMenu(@PathVariable Long userId, @PathVariable Long menuId) {
		MenuLikeId id = MenuLikeId.builder()
				.userId(userId)
				.menuId(menuId)
				.build();
		if (!menuLikeRepository.existsById(id)) {
			throw new ResourceNotFoundException("Menu like not found for user %d and menu %d".formatted(userId, menuId));
		}
		menuLikeRepository.deleteById(id);
	}

	private User fetchUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
	}

	private Menu fetchMenu(Long id) {
		return menuRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Menu not found: " + id));
	}
}
