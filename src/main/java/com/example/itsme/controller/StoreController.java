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

import com.example.itsme.domain.ServiceType;
import com.example.itsme.domain.Store;
import com.example.itsme.dto.StoreRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.ServiceTypeRepository;
import com.example.itsme.repository.StoreRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Validated
public class StoreController {

	private final StoreRepository storeRepository;
	private final ServiceTypeRepository serviceTypeRepository;

	@GetMapping
	public List<Store> getStores(@RequestParam(required = false) Long serviceTypeId) {
		if (serviceTypeId == null) {
			return storeRepository.findAll();
		}
		return storeRepository.findByServiceTypeServiceTypeId(serviceTypeId);
	}

	@GetMapping("/{id}")
	public Store getStore(@PathVariable Long id) {
		return fetchStore(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Store createStore(@Valid @RequestBody StoreRequest request) {
		ServiceType serviceType = fetchServiceType(request.serviceTypeId());
		Store store = Store.builder()
				.name(request.name())
				.serviceType(serviceType)
				.build();
		return storeRepository.save(store);
	}

	@PutMapping("/{id}")
	public Store updateStore(@PathVariable Long id, @Valid @RequestBody StoreRequest request) {
		Store store = fetchStore(id);
		ServiceType serviceType = fetchServiceType(request.serviceTypeId());
		store.setName(request.name());
		store.setServiceType(serviceType);
		return storeRepository.save(store);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
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
