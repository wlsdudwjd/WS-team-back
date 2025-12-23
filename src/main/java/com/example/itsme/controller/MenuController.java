package com.example.itsme.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.itsme.domain.Menu;
import com.example.itsme.domain.MenuCategory;
import com.example.itsme.domain.Store;
import com.example.itsme.dto.MenuRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.MenuCategoryRepository;
import com.example.itsme.repository.MenuRepository;
import com.example.itsme.repository.StoreRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Validated
public class MenuController {

	private final MenuRepository menuRepository;
	private final StoreRepository storeRepository;
	private final MenuCategoryRepository menuCategoryRepository;

	@GetMapping
	public List<Menu> getMenus(@RequestParam(required = false) Long storeId,
			@RequestParam(required = false) Long categoryId) {
		if (storeId != null && categoryId != null) {
			return menuRepository.findByStoreStoreIdAndCategoryMenuCategoryId(storeId, categoryId);
		}
		if (storeId != null) {
			return menuRepository.findByStoreStoreId(storeId);
		}
		if (categoryId != null) {
			return menuRepository.findByCategoryMenuCategoryId(categoryId);
		}
		return menuRepository.findAll();
	}

	@GetMapping("/{id}")
	public Menu getMenu(@PathVariable Long id) {
		return fetchMenu(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Menu createMenu(@Valid @RequestBody MenuRequest request) {
		Menu menu = Menu.builder()
				.name(request.name())
				.price(request.price())
				.description(request.description())
				.store(fetchStore(request.storeId()))
				.category(fetchCategory(request.categoryId()))
				.build();
		return menuRepository.save(menu);
	}

	@PostMapping("/ensure")
	@ResponseStatus(HttpStatus.CREATED)
	public Menu ensureMenu(@Valid @RequestBody MenuRequest request) {
		var existing = menuRepository.findByStoreStoreIdAndName(request.storeId(), request.name());
		if (existing.isPresent()) {
			return existing.get();
		}
		return createMenu(request);
	}

	@PutMapping("/{id}")
	public Menu updateMenu(@PathVariable Long id, @Valid @RequestBody MenuRequest request) {
		Menu menu = fetchMenu(id);
		menu.setName(request.name());
		menu.setPrice(request.price());
		menu.setDescription(request.description());
		menu.setStore(fetchStore(request.storeId()));
		menu.setCategory(fetchCategory(request.categoryId()));
		return menuRepository.save(menu);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteMenu(@PathVariable Long id) {
		Menu menu = fetchMenu(id);
		menuRepository.delete(menu);
	}

	private Menu fetchMenu(Long id) {
		return menuRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Menu not found: " + id));
	}

	private Store fetchStore(Long id) {
		return storeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Store not found: " + id));
	}

	private MenuCategory fetchCategory(Long id) {
		return menuCategoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Menu category not found: " + id));
	}
}
