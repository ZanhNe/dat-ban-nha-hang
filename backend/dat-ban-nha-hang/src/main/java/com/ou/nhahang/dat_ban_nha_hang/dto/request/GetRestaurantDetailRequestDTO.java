package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record GetRestaurantDetailRequestDTO(
        @NotBlank(message = "Tọa độ không được để trống") @Pattern(regexp = "^-?\\d+(\\.\\d+)?,-?\\d+(\\.\\d+)?$", message = "Tọa độ không hợp lệ, định dạng chuẩn: latitude,longitude") String origin,
        @NotNull(message = "ID nhà hàng không được để trống") Long restaurantId) {

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
