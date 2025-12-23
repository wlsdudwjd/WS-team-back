package com.example.itsme.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.itsme.domain.MenuRecommendationStat;

public interface MenuRecommendationStatRepository extends JpaRepository<MenuRecommendationStat, Long> {

	Optional<MenuRecommendationStat> findByMenuMenuIdAndStatDate(Long menuId, LocalDate statDate);

	List<MenuRecommendationStat> findByStatDateOrderByTotalCountDesc(LocalDate statDate, Pageable pageable);
}
