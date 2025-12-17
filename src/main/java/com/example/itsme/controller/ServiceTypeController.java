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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.itsme.domain.ServiceType;
import com.example.itsme.dto.ServiceTypeRequest;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.ServiceTypeRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/service-types")
@RequiredArgsConstructor
@Validated
public class ServiceTypeController {

	private final ServiceTypeRepository serviceTypeRepository;

	@GetMapping
	public List<ServiceType> getServiceTypes() {
		return serviceTypeRepository.findAll();
	}

	@GetMapping("/{id}")
	public ServiceType getServiceType(@PathVariable Long id) {
		return fetchServiceType(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ServiceType createServiceType(@Valid @RequestBody ServiceTypeRequest request) {
		ServiceType type = ServiceType.builder()
				.name(request.name())
				.build();
		return serviceTypeRepository.save(type);
	}

	@PutMapping("/{id}")
	public ServiceType updateServiceType(@PathVariable Long id, @Valid @RequestBody ServiceTypeRequest request) {
		ServiceType type = fetchServiceType(id);
		type.setName(request.name());
		return serviceTypeRepository.save(type);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteServiceType(@PathVariable Long id) {
		ServiceType type = fetchServiceType(id);
		serviceTypeRepository.delete(type);
	}

	private ServiceType fetchServiceType(Long id) {
		return serviceTypeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Service type not found: " + id));
	}
}
