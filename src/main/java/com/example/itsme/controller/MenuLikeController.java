package com.example.itsme.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.itsme.domain.Menu;
import com.example.itsme.domain.MenuLike;
import com.example.itsme.domain.MenuLikeId;
import com.example.itsme.domain.MenuRecommendationStat;
import com.example.itsme.domain.User;
import com.example.itsme.dto.MenuLikeRequest;
import com.example.itsme.dto.MenuLikeCountResponse;
import com.example.itsme.dto.PopularMenuResponse;
import com.example.itsme.dto.MenuLikeStatusResponse;
import com.example.itsme.exception.ResourceNotFoundException;
import com.example.itsme.repository.MenuLikeRepository.MenuLikeCountProjection;
import com.example.itsme.repository.MenuLikeRepository;
import com.example.itsme.repository.MenuRecommendationStatRepository;
import com.example.itsme.repository.MenuRepository;
import com.example.itsme.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menu-likes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Menu Likes & Recommendations", description = "Likes, counts, and recommended menus")
public class MenuLikeController {

	private final MenuLikeRepository menuLikeRepository;
	private final MenuRecommendationStatRepository menuRecommendationStatRepository;
	private final UserRepository userRepository;
	private final MenuRepository menuRepository;

	@GetMapping
	@Operation(summary = "좋아요 목록 조회", description = "조건에 따라 사용자별 또는 메뉴별 좋아요 기록을 조회합니다.")
	public List<MenuLike> getLikes(@RequestParam(required = false) Long userId,
			@RequestParam(required = false) Long menuId) {
		if (userId != null) {
			return menuLikeRepository.findByUserUserId(userId);
		}
		if (menuId != null) {
			return menuLikeRepository.findByMenuMenuId(menuId);
		}
		return menuLikeRepository.findAll();
	}

	@GetMapping("/counts")
	@Operation(summary = "메뉴별 좋아요 수 조회", description = "주어진 메뉴 목록 혹은 전체 메뉴의 좋아요 개수를 반환합니다.")
	public List<MenuLikeCountResponse> getLikeCounts(@RequestParam(required = false) List<Long> menuIds) {
		List<Long> normalizedMenuIds = normalizeMenuIds(menuIds);
		var projections = (normalizedMenuIds == null || normalizedMenuIds.isEmpty())
				? menuLikeRepository.countAllGrouped()
				: menuLikeRepository.countByMenuIds(normalizedMenuIds);
		return projections.stream()
				.filter(Objects::nonNull)
				.map(p -> new MenuLikeCountResponse(p.getMenuId(), p.getLikeCount()))
				.toList();
	}

	@GetMapping("/top")
	@Operation(summary = "인기 메뉴 상위 조회", description = "특정 날짜(기본 오늘) 기준 추천 수가 많은 메뉴를 상위 N개 조회합니다.")
	public List<PopularMenuResponse> getTopMenus(
			@RequestParam(required = false, defaultValue = "5") Integer limit,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		LocalDate targetDate = date != null ? date : LocalDate.now();
		int size = (limit == null || limit <= 0) ? 5 : Math.min(limit, 50);
		var stats = menuRecommendationStatRepository.findByStatDateOrderByTotalCountDesc(targetDate,
				PageRequest.of(0, size));
		return stats.stream()
				.map(stat -> new PopularMenuResponse(
						stat.getMenu().getMenuId(),
						stat.getMenu().getName(),
						stat.getMenu().getPrice(),
						stat.getMenu().getDescription(),
						stat.getMenu().getStore() != null ? stat.getMenu().getStore().getStoreId() : null,
						stat.getMenu().getStore() != null ? stat.getMenu().getStore().getName() : null,
						stat.getTotalCount()))
				.toList();
	}

	@GetMapping("/status")
	@Operation(summary = "좋아요 여부/총합 조회", description = "사용자가 특정 메뉴들을 좋아요 했는지와 메뉴별 총 좋아요 수를 반환합니다.")
	public List<MenuLikeStatusResponse> getStatuses(
			@RequestParam(required = false) Long userId,
			@RequestParam(required = false) String userEmail,
			@RequestParam(required = false) List<Long> menuIds) {
		User user = resolveUserByIdentifier(userId, userEmail);
		List<Long> normalizedMenuIds = normalizeMenuIds(menuIds);
		if (normalizedMenuIds == null || normalizedMenuIds.isEmpty()) {
			throw new IllegalArgumentException("menuIds is required");
		}

		var likeSet = menuLikeRepository.findByUserUserIdAndMenuMenuIdIn(user.getUserId(),
						Set.copyOf(normalizedMenuIds))
				.stream()
				.map(ml -> ml.getMenu().getMenuId())
				.collect(Collectors.toSet());

		var totalCounts = menuLikeRepository.countByMenuIds(normalizedMenuIds)
				.stream()
				.collect(Collectors.toMap(MenuLikeCountProjection::getMenuId, MenuLikeCountProjection::getLikeCount));

		return normalizedMenuIds.stream()
				.map(menuId -> new MenuLikeStatusResponse(
						menuId,
						likeSet.contains(menuId),
						totalCounts.getOrDefault(menuId, 0L)))
				.toList();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "좋아요 추가", description = "사용자가 메뉴를 좋아요하고 누적 카운트를 증가시킵니다.")
	public MenuLike likeMenu(@Valid @RequestBody MenuLikeRequest request) {
		User user = resolveUser(request);
		Menu menu = fetchMenu(request.menuId());
		MenuLikeId id = MenuLikeId.builder()
				.userId(user.getUserId())
				.menuId(menu.getMenuId())
				.build();
		MenuLike saved = menuLikeRepository.findById(id)
				.map(existing -> {
					existing.setLikeCount(existing.getLikeCount() + 1);
					return menuLikeRepository.save(existing);
				})
				.orElseGet(() -> {
					MenuLike menuLike = MenuLike.builder()
							.id(id)
							.user(user)
							.menu(menu)
							.likeCount(1)
							.build();
					return menuLikeRepository.save(menuLike);
				});
		incrementTodayStat(menu, 1);
		return saved;
	}

	@DeleteMapping("/{userId}/{menuId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "좋아요 취소", description = "사용자가 메뉴에 남긴 좋아요를 삭제합니다.")
	public void unlikeMenu(@PathVariable Long userId, @PathVariable Long menuId) {
		MenuLikeId id = MenuLikeId.builder()
				.userId(userId)
				.menuId(menuId)
				.build();
		if (!menuLikeRepository.existsById(id)) {
			throw new ResourceNotFoundException("Menu like not found for user %d and menu %d".formatted(userId, menuId));
		}
		// 좋아요 삭제 시 인기 통계도 함께 감소시킨다.
		menuRepository.findById(menuId).ifPresent(menu -> incrementTodayStat(menu, -1));
		menuLikeRepository.deleteById(id);
	}

	private User fetchUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
	}

	private User resolveUserByIdentifier(Long userId, String userEmail) {
		if (userId != null) {
			return fetchUser(userId);
		}
		if (userEmail != null && !userEmail.isBlank()) {
			return userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + userEmail));
		}
		throw new ResourceNotFoundException("User identifier is required (userId or userEmail)");
	}

	private User resolveUser(MenuLikeRequest request) {
		if (request.userId() != null) {
			return fetchUser(request.userId());
		}
		if (request.userEmail() != null && !request.userEmail().isBlank()) {
			return userRepository.findByEmail(request.userEmail())
					.orElseGet(() -> userRepository.save(User.builder()
							.email(request.userEmail())
							.username(request.userEmail())
							.password(UUID.randomUUID().toString())
							.name(request.userEmail())
							.build()));
		}
		throw new ResourceNotFoundException("User identifier is required (userId or userEmail)");
	}

	private Menu fetchMenu(Long id) {
		return menuRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Menu not found: " + id));
	}

	private void incrementTodayStat(Menu menu, int delta) {
		LocalDate today = LocalDate.now();
		menuRecommendationStatRepository.findByMenuMenuIdAndStatDate(menu.getMenuId(), today)
				.map(stat -> {
					int newCount = stat.getTotalCount() + delta;
					stat.setTotalCount(Math.max(newCount, 0)); // 음수 방지
					return menuRecommendationStatRepository.save(stat);
				})
				.orElseGet(() -> menuRecommendationStatRepository.save(MenuRecommendationStat.builder()
						.menu(menu)
						.statDate(today)
						.totalCount(delta)
						.build()));
	}

	private List<Long> normalizeMenuIds(List<Long> menuIds) {
		if (menuIds == null || menuIds.isEmpty()) {
			return menuIds;
		}
		if (menuIds.size() == 1) {
			String raw = String.valueOf(menuIds.getFirst());
			if (raw.contains(",")) {
				return List.of(raw.split(",")).stream()
						.map(String::trim)
						.filter(s -> !s.isBlank())
						.map(Long::parseLong)
						.toList();
			}
		}
		return menuIds;
	}
}
