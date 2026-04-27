package com.ou.nhahang.dat_ban_nha_hang.integrations;

import org.locationtech.jts.geom.Point;

import java.util.Map;
import java.util.HashMap;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;

import com.ou.nhahang.dat_ban_nha_hang.config.GoongConfig;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoCoordinateResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoDirectionResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.service.port.IGeolocationService;
import com.ou.nhahang.dat_ban_nha_hang.utils.ExternalApiUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoongGeolocationService implements IGeolocationService {

    private final ExternalApiUtil externalApiUtil;
    private final GoongConfig goongConfig;

    @Override
    public Point getPointFromAddress(String address) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("address", address);
            params.put("api_key", goongConfig.getApiKey());
            GeoCoordinateResponseDTO response = externalApiUtil.sendGetRequest(goongConfig.getBaseUrl() + "geocode",
                    params,
                    GeoCoordinateResponseDTO.class);
            if (response.status().equals("OK")) {
                GeoCoordinateResponseDTO.Result result = response.results()[0];
                GeoCoordinateResponseDTO.Result.Geometry geometry = result.geometry();
                GeoCoordinateResponseDTO.Result.Geometry.Location location = geometry.location();
                return new GeometryFactory().createPoint(new Coordinate(
                        location.longitude(),
                        location.latitude()));
            }
            throw new BusinessException("Không tìm thấy tọa độ cho địa chỉ: " + address);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi lấy tọa độ cho địa chỉ: " + address);
        }
    }

    @Override
    public GeoDirectionResponseDTO getDirection(Point start, Point end) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("origin", start.getY() + "," + start.getX());
            params.put("destination", end.getY() + "," + end.getX());
            params.put("api_key", goongConfig.getApiKey());

            String finalUrl = goongConfig.getBaseUrl() + "direction";
            GeoDirectionResponseDTO response = externalApiUtil.sendGetRequest(finalUrl, params,
                    GeoDirectionResponseDTO.class);
            if (response.routes().length > 0) {
                return response;
            }
            throw new BusinessException("Không tìm thấy đường đi từ " + start + " đến " + end);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi lấy đường đi từ " + start + " đến " + end);
        }
    }

    @Override
    public GeoCoordinateResponseDTO.Result.Geometry.Location getCoordinates(String address) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("address", address);
            params.put("api_key", goongConfig.getApiKey());
            GeoCoordinateResponseDTO response = externalApiUtil.sendGetRequest(goongConfig.getBaseUrl() + "geocode",
                    params,
                    GeoCoordinateResponseDTO.class);
            if (response.status().equals("OK")) {
                GeoCoordinateResponseDTO.Result result = response.results()[0];
                GeoCoordinateResponseDTO.Result.Geometry geometry = result.geometry();
                GeoCoordinateResponseDTO.Result.Geometry.Location location = geometry.location();
                return location;
            }
            throw new BusinessException("Không tìm thấy tọa độ cho địa chỉ: " + address);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Lỗi khi lấy tọa độ cho địa chỉ: " + address);
        }
    }

}
