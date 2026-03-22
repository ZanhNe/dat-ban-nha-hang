package com.ou.nhahang.dat_ban_nha_hang.controller;

import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ou.nhahang.dat_ban_nha_hang.dto.RestaurantTableDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.RestaurantService;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/{restaurantId}/tables/available")
    public List<RestaurantTableDTO> findTable(
            @PathVariable("restaurantId") Long restaurantId,
            @RequestParam("quantity") Long quantity,
            @RequestParam("time")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {

        return restaurantService.findTableExecute(time, quantity, restaurantId);
    }
}

