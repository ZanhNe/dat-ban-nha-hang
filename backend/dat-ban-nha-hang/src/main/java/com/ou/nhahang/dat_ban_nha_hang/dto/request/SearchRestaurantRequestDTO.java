package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SearchRestaurantRequestDTO(
        @NotBlank(message = "Tọa độ không được để trống") @Pattern(regexp = "^-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?$", message = "Tọa độ không hợp lệ, định dạng chuẩn: latitude,longitude") String origin,
        String cuisine,
        @NotNull(message = "Bán kính tìm kiếm không được để trống") @Min(value = 1, message = "Bán kính tìm kiếm phải lớn hơn 0") Integer radius,
        @Min(value = 0, message = "Số trang phải lớn hơn hoặc bằng 0") Integer page,
        @Min(value = 10, message = "Số lượng trên mỗi trang phải lớn hơn 0") @Max(value = 100, message = "Số lượng trên mỗi trang phải nhỏ hơn hoặc bằng 100") Integer limit) {

    public SearchRestaurantRequestDTO {
        if (page == null)
            page = 1;
        if (limit == null)
            limit = 10;
    }

    public Point extractLocation() {
        if (origin == null || origin.isBlank()) {
            return null;
        }
        String[] parts = origin.split(",");
        if (parts.length != 2) {
            return null;
        }
        try {
            double lat = Double.parseDouble(parts[0].trim());
            double lng = Double.parseDouble(parts[1].trim());
            GeometryFactory geometryFactory = new GeometryFactory();
            Point point = geometryFactory.createPoint(new Coordinate(lng, lat));
            point.setSRID(4326);
            return point;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
