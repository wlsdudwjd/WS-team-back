package com.example.itsme.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Validated
@Tag(name = "Menus", description = "Create/read/update/delete menus")
public class MenuController {

	private final MenuRepository menuRepository;
	private final StoreRepository storeRepository;
	private final MenuCategoryRepository menuCategoryRepository;

	@GetMapping
	@Operation(summary = "메뉴 목록 조회", description = "매장/카테고리/서비스타입/이름 검색 + 페이지네이션/정렬을 지원합니다.")
	public Page<Menu> getMenus(@RequestParam(required = false) Long storeId,
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) Long serviceTypeId,
			@RequestParam(required = false) String q,
			Pageable pageable) {

		Specification<Menu> spec = Specification.where(null);

		if (storeId != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("store").get("storeId"), storeId));
		}
		if (categoryId != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("menuCategoryId"), categoryId));
		}
		if (serviceTypeId != null) {
			spec = spec.and((root, query, cb) -> cb.equal(root.get("store").get("serviceType").get("serviceTypeId"),
					serviceTypeId));
		}
		if (q != null && !q.isBlank()) {
			String like = "%" + q.trim().toLowerCase() + "%";
			spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), like));
		}

		return menuRepository.findAll(spec, pageable);
	}

	@GetMapping("/{id}")
	@Operation(summary = "메뉴 단건 조회", description = "menuId로 메뉴 상세를 조회합니다")
	public Menu getMenu(@PathVariable Long id) {
		return fetchMenu(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "메뉴 생성", description = "매장/카테고리와 연결된 새 메뉴를 등록합니다")
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
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "메뉴 존재 보장", description = "단일 매장/이름 메뉴가 없으면 생성, 있으면 기존 메뉴를 반환합니다")
	public Menu ensureMenu(@Valid @RequestBody MenuRequest request) {
		var existing = menuRepository.findByStoreStoreIdAndName(request.storeId(), request.name());
		if (existing.isPresent()) {
			return existing.get();
		}
		return createMenu(request);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "메뉴 수정", description = "메뉴명/가격/설명/매장/카테고리를 변경합니다.")
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
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "메뉴 삭제", description = "menuId로 메뉴를 삭제합니다")
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
