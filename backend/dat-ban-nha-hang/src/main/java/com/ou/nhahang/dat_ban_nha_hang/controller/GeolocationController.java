package com.ou.nhahang.dat_ban_nha_hang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoCoordinateResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.port.IGeolocationService;

@RestController
@RequestMapping("/api/v1/geolocation")
public class GeolocationController {

    @Autowired
    private IGeolocationService geolocationService;

    @GetMapping("/get-coordinates")
    public ResponseEntity<GeoCoordinateResponseDTO.Result.Geometry.Location> getCoordinates(
            @RequestParam("address") String address) {
        return ResponseEntity.ok(geolocationService.getCoordinates(address));
    }
}
