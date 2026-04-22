package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record CursorPaginationResult<T>(
        List<T> data,
        CursorPaginationMeta meta) {
    @Builder
    public record CursorPaginationMeta(
            Long nextCursor,
            Boolean hasMore,
            Long totalElements) {
    }
}
