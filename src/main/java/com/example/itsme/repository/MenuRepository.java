package com.example.itsme.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.itsme.domain.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {
	List<Menu> findByStoreStoreId(Long storeId);

	List<Menu> findByCategoryMenuCategoryId(Long menuCategoryId);

	List<Menu> findByStoreStoreIdAndCategoryMenuCategoryId(Long storeId, Long menuCategoryId);

	List<Menu> findByStoreServiceTypeServiceTypeId(Long serviceTypeId);

	List<Menu> findByCategoryMenuCategoryIdAndStoreServiceTypeServiceTypeId(Long menuCategoryId, Long serviceTypeId);

	Optional<Menu> findByStoreStoreIdAndName(Long storeId, String name);
}
