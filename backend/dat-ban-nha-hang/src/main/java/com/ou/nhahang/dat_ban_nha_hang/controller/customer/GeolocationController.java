package com.ou.nhahang.dat_ban_nha_hang.controller.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoCoordinateResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.port.IGeolocationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/geolocation")
@RequiredArgsConstructor
public class GeolocationController {

    private final IGeolocationService geolocationService;

    @GetMapping("/get-coordinates")
    public ResponseEntity<GeoCoordinateResponseDTO.Result.Geometry.Location> getCoordinates(
            @RequestParam("address") String address) {
        return ResponseEntity.ok(geolocationService.getCoordinates(address));
    }
}
