package com.ou.nhahang.dat_ban_nha_hang.controller;

import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import com.ou.nhahang.dat_ban_nha_hang.dto.RestaurantTableDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.BookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.RestaurantService;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;


    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * Tương ứng với bước KhachHang → UI → RestaurantController.findTable(time, quantity, resId)
     * trong sequence diagram.
     *
     * Ví dụ gọi:
     * GET /api/restaurants/1/tables/available?time=18:30&quantity=4
     */
    @GetMapping("/{restaurantId}/tables/available")
    public List<RestaurantTableDTO> findTable(
            @PathVariable("restaurantId") Long restaurantId,
            @RequestParam("quantity") Long quantity,
            @RequestParam("time")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {

        return restaurantService.findTableExecute(time, quantity, restaurantId);
    }


    @PostMapping("/bookings")
    public ApiResponse<BookingResponseDTO> createBooking(@RequestBody @Valid BookingRequestDTO requestDTO) {
        // Tương ứng với UI --> RestaurantController trong sequence diagram
        // Mock userId 1L cho User đang đăng nhập 
        Long mockUserId = 1L;
        BookingResponseDTO data = restaurantService.bookingExecute(requestDTO, mockUserId);
        
        return ApiResponse.<BookingResponseDTO>builder()
                .status(200)
                .message("Đặt bàn thành công")
                .data(data)
                .build();
    }
}

