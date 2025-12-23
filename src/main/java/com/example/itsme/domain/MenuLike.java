package com.example.itsme.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
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
@Table(name = "menu_likes", indexes = {
		@Index(name = "idx_menu_likes_user_id", columnList = "user_id"),
		@Index(name = "idx_menu_likes_menu_id", columnList = "menu_id")
})
public class MenuLike {

	@EmbeddedId
	private MenuLikeId id;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("menuId")
	@JoinColumn(name = "menu_id")
	private Menu menu;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@NotNull
	@Builder.Default
	@Column(name = "like_count", nullable = false)
	private Integer likeCount = 1;
}
