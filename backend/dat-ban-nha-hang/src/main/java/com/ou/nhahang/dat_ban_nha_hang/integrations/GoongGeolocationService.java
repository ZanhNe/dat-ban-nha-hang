package com.ou.nhahang.dat_ban_nha_hang.integrations;

import org.locationtech.jts.geom.Point;

import java.util.Map;
import java.util.HashMap;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoCoordinateResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoDirectionResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.service.port.IGeolocationService;
import com.ou.nhahang.dat_ban_nha_hang.utils.ExternalApiUtil;

import org.springframework.beans.factory.annotation.Value;

@Service
public class GoongGeolocationService implements IGeolocationService {

    private ExternalApiUtil externalApiUtil;

    public GoongGeolocationService(ExternalApiUtil externalApiUtil) {
        this.externalApiUtil = externalApiUtil;
    }

    @Value("${goong.api-key}")
    private String apiKey;

    @Value("${goong.url}")
    private String baseUrl;

    @Override
    public Point getPointFromAddress(String address) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("address", address);
            params.put("api_key", apiKey);
            GeoCoordinateResponseDTO response = externalApiUtil.sendGetRequest(baseUrl + "geocode", params,
                    GeoCoordinateResponseDTO.class);
            if (response.status().equals("OK")) {
                GeoCoordinateResponseDTO.Result result = response.results()[0];
                GeoCoordinateResponseDTO.Result.Geometry geometry = result.geometry();
                GeoCoordinateResponseDTO.Result.Geometry.Location location = geometry.location();
                return new GeometryFactory().createPoint(new Coordinate(
                        location.lng(),
                        location.lat()));
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
            params.put("api_key", apiKey);
            GeoDirectionResponseDTO response = externalApiUtil.sendGetRequest(baseUrl + "directions", params,
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

}
