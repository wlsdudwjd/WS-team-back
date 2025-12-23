package com.example.itsme.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menu_recommendation_stat", uniqueConstraints = {
		@UniqueConstraint(name = "uk_menu_stat_date", columnNames = {"menu_id", "stat_date"})
}, indexes = {
		@Index(name = "idx_menu_recommendation_stat_date", columnList = "stat_date"),
		@Index(name = "idx_menu_recommendation_stat_menu_id", columnList = "menu_id")
})
public class MenuRecommendationStat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_recommendation_stat_id")
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "menu_id", nullable = false)
	private Menu menu;

	@NotNull
	@Column(name = "stat_date", nullable = false)
	private LocalDate statDate;

	@NotNull
	@Builder.Default
	@Column(name = "total_count", nullable = false)
	private Integer totalCount = 0;
}
