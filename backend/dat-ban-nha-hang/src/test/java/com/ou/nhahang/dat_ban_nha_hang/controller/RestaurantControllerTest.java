package com.ou.nhahang.dat_ban_nha_hang.controller;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.TableSearchRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.TableSearchResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.RestaurantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestaurantController.class)
public class RestaurantControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private RestaurantService restaurantService;

        // --- TEST THÀNH CÔNG ---

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

        // --- TEST NGOẠI LỆ (EXCEPTION / VALIDATION) ---

        @Test
        public void givenInvalidDatePattern_whenSearchTables_thenReturn400BadRequest() throws Exception {
                // 1. Arrange
                Long restaurantId = 1L;
                // Ngày sai định dạng regex YYYY-MM-DD
                String invalidDate = "2026/03/20";

                // Data Dummy/Không cần cấu hình Stub vì Service layer không bao giờ được chạm
                // tới
                // Lỗi sẽ bị chặn ngay tại controller do logic @Valid

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
                // Số lượng khách không hợp lệ (< 1)
                String invalidGuests = "0";

                // 2. Act & 3. Assert
                mockMvc.perform(get("/api/v1/restaurants/{id}/tables", restaurantId)
                                .param("date", "2026-03-20")
                                .param("time", "19:00")
                                .param("guests", invalidGuests))
                                .andExpect(status().isBadRequest());
        }
}
