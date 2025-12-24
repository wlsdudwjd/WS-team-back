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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menu-categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Menu Categories", description = "Manage categories for each service type")
public class MenuCategoryController {

	private final MenuCategoryRepository menuCategoryRepository;
	private final ServiceTypeRepository serviceTypeRepository;

	@GetMapping
	@Operation(summary = "카테고리 목록 조회", description = "서비스 타입별 또는 전체 메뉴 카테고리를 조회합니다.")
	public List<MenuCategory> getCategories(@RequestParam(required = false) Long serviceTypeId) {
		if (serviceTypeId == null) {
			return menuCategoryRepository.findAll();
		}
		return menuCategoryRepository.findByServiceTypeServiceTypeId(serviceTypeId);
	}

	@GetMapping("/{id}")
	@Operation(summary = "카테고리 단건 조회", description = "menuCategoryId로 카테고리 상세를 조회합니다.")
	public MenuCategory getCategory(@PathVariable Long id) {
		return fetchCategory(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "카테고리 생성", description = "서비스 타입에 속한 새 카테고리를 등록합니다.")
	public MenuCategory createCategory(@Valid @RequestBody MenuCategoryRequest request) {
		ServiceType serviceType = fetchServiceType(request.serviceTypeId());
		MenuCategory category = MenuCategory.builder()
				.name(request.name())
				.serviceType(serviceType)
				.build();
		return menuCategoryRepository.save(category);
	}

	@PutMapping("/{id}")
	@Operation(summary = "카테고리 수정", description = "카테고리 이름과 서비스 타입을 변경합니다.")
	public MenuCategory updateCategory(@PathVariable Long id, @Valid @RequestBody MenuCategoryRequest request) {
		MenuCategory category = fetchCategory(id);
		ServiceType serviceType = fetchServiceType(request.serviceTypeId());
		category.setName(request.name());
		category.setServiceType(serviceType);
		return menuCategoryRepository.save(category);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "카테고리 삭제", description = "menuCategoryId로 카테고리를 삭제합니다.")
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
