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

import com.example.itsme.domain.MenuCategory;
import com.example.itsme.domain.ServiceType;
import com.example.itsme.dto.MenuCategoryRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.MenuCategoryRepository;
import com.example.itsme.repository.ServiceTypeRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menu-categories")
@RequiredArgsConstructor
@Validated
public class MenuCategoryController {

	private final MenuCategoryRepository menuCategoryRepository;
	private final ServiceTypeRepository serviceTypeRepository;

	@GetMapping
	public List<MenuCategory> getCategories(@RequestParam(required = false) Long serviceTypeId) {
		if (serviceTypeId == null) {
			return menuCategoryRepository.findAll();
		}
		return menuCategoryRepository.findByServiceTypeServiceTypeId(serviceTypeId);
	}

	@GetMapping("/{id}")
	public MenuCategory getCategory(@PathVariable Long id) {
		return fetchCategory(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MenuCategory createCategory(@Valid @RequestBody MenuCategoryRequest request) {
		ServiceType serviceType = fetchServiceType(request.serviceTypeId());
		MenuCategory category = MenuCategory.builder()
				.name(request.name())
				.serviceType(serviceType)
				.build();
		return menuCategoryRepository.save(category);
	}

	@PutMapping("/{id}")
	public MenuCategory updateCategory(@PathVariable Long id, @Valid @RequestBody MenuCategoryRequest request) {
		MenuCategory category = fetchCategory(id);
		ServiceType serviceType = fetchServiceType(request.serviceTypeId());
		category.setName(request.name());
		category.setServiceType(serviceType);
		return menuCategoryRepository.save(category);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCategory(@PathVariable Long id) {
		MenuCategory category = fetchCategory(id);
		menuCategoryRepository.delete(category);
	}

	private MenuCategory fetchCategory(Long id) {
		return menuCategoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Menu category not found: " + id));
	}

	private ServiceType fetchServiceType(Long id) {
		return serviceTypeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Service type not found: " + id));
	}
}
