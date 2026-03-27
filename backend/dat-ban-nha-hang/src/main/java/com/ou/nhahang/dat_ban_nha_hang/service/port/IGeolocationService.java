package com.ou.nhahang.dat_ban_nha_hang.service.port;

import org.locationtech.jts.geom.Point;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoCoordinateResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoDirectionResponseDTO;

public interface IGeolocationService {
    public Point getPointFromAddress(String address);

    public GeoDirectionResponseDTO getDirection(Point start, Point end);

    public GeoCoordinateResponseDTO.Result.Geometry.Location getCoordinates(String address);
}
