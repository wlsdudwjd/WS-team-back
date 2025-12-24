package com.example.itsme.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.itsme.domain.MenuLike;
import com.example.itsme.domain.MenuLikeId;

public interface MenuLikeRepository extends JpaRepository<MenuLike, MenuLikeId> {
	List<MenuLike> findByUserUserId(Long userId);

	List<MenuLike> findByMenuMenuId(Long menuId);

	List<MenuLike> findByUserUserIdAndMenuMenuIdIn(Long userId, Set<Long> menuIds);

	interface MenuLikeCountProjection {
		Long getMenuId();

		Long getLikeCount();
	}

	@Query("""
			select ml.menu.menuId as menuId, sum(ml.likeCount) as likeCount
			from MenuLike ml
			group by ml.menu.menuId
			""")
	List<MenuLikeCountProjection> countAllGrouped();

	@Query("""
			select ml.menu.menuId as menuId, sum(ml.likeCount) as likeCount
			from MenuLike ml
			where ml.menu.menuId in :menuIds
			group by ml.menu.menuId
			""")
	List<MenuLikeCountProjection> countByMenuIds(List<Long> menuIds);
}
