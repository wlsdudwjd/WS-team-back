package com.example.itsme.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Popular menu (today)")
public record PopularMenuResponse(
		@Schema(description = "Menu id", example = "1")
		Long menuId,
		@Schema(description = "Menu name", example = "아메리카노")
		String name,
		@Schema(description = "Menu price", example = "3800")
		Integer price,
		@Schema(description = "Menu description")
		String description,
		@Schema(description = "Store id", example = "1")
		Long storeId,
		@Schema(description = "Store name", example = "캠퍼스 카페 후생관")
		String storeName,
		@Schema(description = "Recommendation count for the given date", example = "12")
		Integer likeCount
) {
}
