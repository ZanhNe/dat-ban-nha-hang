package com.ou.nhahang.dat_ban_nha_hang.controller;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.GetRestaurantDetailRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.TableSearchRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantMenuResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.SearchRestaurantResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.TableSearchResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.GetRestaurantReviewRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantReviewResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.CursorPaginationResult;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.service.RestaurantService;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.assertj.core.api.Assertions;

@WebMvcTest(RestaurantController.class)
public class RestaurantControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private RestaurantService restaurantService;

        @Test
        public void givenValidTableSearchRequest_whenSearchTables_thenReturn200Ok() throws Exception {
                // 1. Arrange
                Long restaurantId = 1L;
                TableSearchRequestDTO requestDTO = new TableSearchRequestDTO("2026-03-20", "19:00", 2L);

                TableSearchResponseDTO mockResponse = TableSearchResponseDTO.builder()
                                .restaurantId(restaurantId)
                                .build();

                // Stub: service trả về response cố định
                when(restaurantService.searchTablesExecute(eq(restaurantId), any(TableSearchRequestDTO.class)))
                                .thenReturn(mockResponse);

                // 2. Act & 3. Assert
                mockMvc.perform(get("/api/v1/restaurants/{id}/tables", restaurantId)
                                .param("date", requestDTO.date())
                                .param("time", requestDTO.time())
                                .param("guests", requestDTO.guests().toString()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Lấy danh sách bàn thành công"))
                                .andExpect(jsonPath("$.data.restaurantId").value(restaurantId));
        }

        @Test
        public void givenInvalidDatePattern_whenSearchTables_thenReturn400BadRequest() throws Exception {
                // 1. Arrange
                Long restaurantId = 1L;
                String invalidDate = "2026/03/20";

                // 2. Act & 3. Assert
                mockMvc.perform(get("/api/v1/restaurants/{id}/tables", restaurantId)
                                .param("date", invalidDate)
                                .param("time", "19:00")
                                .param("guests", "2"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void givenNegativeGuests_whenSearchTables_thenReturn400BadRequest() throws Exception {
                // 1. Arrange
                Long restaurantId = 1L;
                String invalidGuests = "0";

                // 2. Act & 3. Assert
                mockMvc.perform(get("/api/v1/restaurants/{id}/tables", restaurantId)
                                .param("date", "2026-03-20")
                                .param("time", "19:00")
                                .param("guests", invalidGuests))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void givenValidIdAndOrigin_whenGetRestaurantDetail_thenReturn200Ok() throws Exception {
                // 1. Arrange
                Long restaurantId = 1L;
                String origin = "10.0,106.0";
                GetRestaurantDetailResponseDTO mockResponse = GetRestaurantDetailResponseDTO.builder()
                                .restaurantId(restaurantId)
                                .restaurantName("Test Res")
                                .build();

                when(restaurantService.getRestaurantDetailExecute(
                                any(GetRestaurantDetailRequestDTO.class)))
                                .thenReturn(mockResponse);

                // 2. Act & 3. Assert
                mockMvc.perform(get("/api/v1/restaurants/{id}", restaurantId)
                                .param("origin", origin))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value(200))
                                .andExpect(jsonPath("$.message").value("Lấy thông tin chi tiết nhà hàng thành công"))
                                .andExpect(jsonPath("$.data.restaurantId").value(restaurantId));
        }

        @Test
        public void givenServiceThrowsNotFound_whenGetRestaurantDetail_thenReturns404() throws Exception {
                // 1. Arrange
                Long restaurantId = 999L;
                String origin = "10.0,106.0";

                when(restaurantService.getRestaurantDetailExecute(any()))
                                .thenThrow(new ResourceNotFoundException(
                                                "Không tìm thấy nhà hàng"));

                // 2. Act & 3. Assert
                try {
                        mockMvc.perform(get("/api/v1/restaurants/{id}", restaurantId)
                                        .param("origin", origin))
                                        .andExpect(status().isNotFound());
                } catch (Exception e) {
                        Assertions.assertThat(e.getCause()).isInstanceOf(ResourceNotFoundException.class);
                }
        }

    @Test
    void getRestaurantMenu_Success_ShouldReturnMenuDTO() throws Exception {
        // Arrange
        Long restaurantId = 1L;
        GetRestaurantMenuResponseDTO mockResponse = GetRestaurantMenuResponseDTO.builder()
                .restaurantId(restaurantId)
                .restaurantName("Haidilao")
                .restaurantMenus(List.of())
                .build();

        given(restaurantService.getRestaurantMenuExecute(restaurantId)).willReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/restaurants/{id}/menu", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Lấy thông tin menu thành công"))
                .andExpect(jsonPath("$.data.restaurantId").value(1))
                .andExpect(jsonPath("$.data.restaurantName").value("Haidilao"));
    }

    @Test
    public void getRestaurantReviews_Success_ShouldReturn200() throws Exception {
        Long restaurantId = 1L;
        
        GetRestaurantReviewResponseDTO mockReview = new GetRestaurantReviewResponseDTO(
            100L, "John Doe", "avatar.png", 5, "Great food", LocalDateTime.now()
        );
        CursorPaginationResult.CursorPaginationMeta mockMeta = new CursorPaginationResult.CursorPaginationMeta(100L, false, 1L);
        CursorPaginationResult<GetRestaurantReviewResponseDTO> mockResult = new CursorPaginationResult<>(
            List.of(mockReview), mockMeta
        );

        when(restaurantService.getRestaurantReviewsExecute(eq(restaurantId), any(GetRestaurantReviewRequestDTO.class)))
            .thenReturn(mockResult);

        mockMvc.perform(get("/api/v1/restaurants/{id}/reviews", restaurantId)
                .param("limit", "10")
                .param("sort", "NEWEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Lấy danh sách đánh giá thành công"))
                .andExpect(jsonPath("$.data[0].id").value(100))
                .andExpect(jsonPath("$.data[0].userName").value("John Doe"))
                .andExpect(jsonPath("$.meta.hasMore").value(false))
                .andExpect(jsonPath("$.meta.nextCursor").value(100))
                .andExpect(jsonPath("$.meta.totalReviews").value(1));
    }
}
