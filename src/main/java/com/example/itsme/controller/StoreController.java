package com.example.itsme.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.example.itsme.domain.ServiceType;
import com.example.itsme.domain.Store;
import com.example.itsme.dto.StoreRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.ServiceTypeRepository;
import com.example.itsme.repository.StoreRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Validated
@Tag(name = "Stores", description = "Stores (cafes/cafeterias) per service type")
public class StoreController {

	private final StoreRepository storeRepository;
	private final ServiceTypeRepository serviceTypeRepository;

	@GetMapping
	@Operation(summary = "매장 목록 조회", description = "서비스타입별 매장 목록을 페이지네이션 조회")
	public Page<Store> getStores(@RequestParam(required = false) Long serviceTypeId, Pageable pageable) {
		if (serviceTypeId == null) {
			return storeRepository.findAll(pageable);
		}
		return storeRepository.findByServiceTypeServiceTypeId(serviceTypeId, pageable);
	}

	@GetMapping("/{id}")
	@Operation(summary = "매장 단건 조회", description = "storeId로 매장을 조회합니다")
	public Store getStore(@PathVariable Long id) {
		return fetchStore(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "매장 생성", description = "서비스타입에 매장을 등록합니다")
	public Store createStore(@Valid @RequestBody StoreRequest request) {
		ServiceType serviceType = fetchServiceType(request.serviceTypeId());
		Store store = Store.builder()
				.name(request.name())
				.serviceType(serviceType)
				.build();
		return storeRepository.save(store);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "매장 수정", description = "storeId로 매장 이름/서비스타입을 수정합니다")
	public Store updateStore(@PathVariable Long id, @Valid @RequestBody StoreRequest request) {
		Store store = fetchStore(id);
		ServiceType serviceType = fetchServiceType(request.serviceTypeId());
		store.setName(request.name());
		store.setServiceType(serviceType);
		return storeRepository.save(store);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "매장 삭제", description = "storeId로 매장을 삭제합니다")
	public void deleteStore(@PathVariable Long id) {
		Store store = fetchStore(id);
		storeRepository.delete(store);
	}

	private Store fetchStore(Long id) {
		return storeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Store not found: " + id));
	}

	private ServiceType fetchServiceType(Long id) {
		return serviceTypeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Service type not found: " + id));
	}
}
