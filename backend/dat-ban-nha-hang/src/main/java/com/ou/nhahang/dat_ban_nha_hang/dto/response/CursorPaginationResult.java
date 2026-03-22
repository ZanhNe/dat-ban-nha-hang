package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.util.List;

public record CursorPaginationResult<T>(
    List<T> data,
    CursorPaginationMeta meta
) {
    public record CursorPaginationMeta(
        Long nextCursor,
        Boolean hasMore,
        Long totalReviews
    ) {}
}
