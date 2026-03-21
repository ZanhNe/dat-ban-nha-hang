package com.ou.nhahang.dat_ban_nha_hang.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.BookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.GetRestaurantDetailRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.SearchRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.TableSearchRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.SearchRestaurantResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.TableSearchResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.RestaurantService;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SearchRestaurantResponseDTO>>> searchRestaurants(
            @ModelAttribute @Valid SearchRestaurantRequestDTO requestDTO) {
        List<SearchRestaurantResponseDTO> data = restaurantService.searchRestaurantsExecute(requestDTO);
        ApiResponse<List<SearchRestaurantResponseDTO>> response = ApiResponse
                .<List<SearchRestaurantResponseDTO>>builder()
                .status(200)
                .message("Lấy danh sách nhà hàng thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/tables")
    public ResponseEntity<ApiResponse<TableSearchResponseDTO>> searchTables(
            @PathVariable("id") Long id,
            @ModelAttribute @Valid TableSearchRequestDTO requestDTO) {

        TableSearchResponseDTO data = restaurantService.searchTablesExecute(id, requestDTO);
        ApiResponse<TableSearchResponseDTO> response = ApiResponse.<TableSearchResponseDTO>builder()
                .status(200)
                .message("Lấy danh sách bàn thành công")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{id}/bookings")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @PathVariable("id") Long id,
            @RequestBody @Valid BookingRequestDTO requestDTO) {
        Long mockUserId = 1L;
        BookingResponseDTO data = restaurantService.bookingExecute(requestDTO, mockUserId, id);
        ApiResponse<BookingResponseDTO> apiResponse = ApiResponse.<BookingResponseDTO>builder()
                .status(201)
                .message("Đặt bàn thành công")
                .data(data)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetRestaurantDetailResponseDTO>> getRestaurantDetail(
            @PathVariable("id") Long id,
            @ModelAttribute GetRestaurantDetailRequestDTO requestDTO) {
        
        GetRestaurantDetailRequestDTO finalRequestDTO;
        if (requestDTO == null || requestDTO.restaurantId() == null) {
            String origin = requestDTO != null ? requestDTO.origin() : null;
            finalRequestDTO = new GetRestaurantDetailRequestDTO(origin, id);
        } else {
            finalRequestDTO = requestDTO;
        }

        GetRestaurantDetailResponseDTO data = restaurantService.getRestaurantDetailExecute(finalRequestDTO);
        ApiResponse<GetRestaurantDetailResponseDTO> response = ApiResponse.<GetRestaurantDetailResponseDTO>builder()
                .status(200)
                .message("Lấy thông tin chi tiết nhà hàng thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }
}
