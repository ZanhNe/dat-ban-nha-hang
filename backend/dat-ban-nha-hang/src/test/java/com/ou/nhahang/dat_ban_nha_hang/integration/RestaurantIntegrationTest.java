package com.ou.nhahang.dat_ban_nha_hang.integration;

import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTable;
import com.ou.nhahang.dat_ban_nha_hang.entity.TableArea;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.repository.BookingRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantTableRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import com.ou.nhahang.dat_ban_nha_hang.utils.TestDataMother;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class RestaurantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantTableRepository restaurantTableRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager entityManager;

    private Restaurant savedRestaurant;
    private RestaurantTable savedTable;

    @BeforeEach
    void setUp() {
        User customer = TestDataMother.createUser(null, "customer1");
        userRepository.save(customer);

        User manager = TestDataMother.createUser(null, "manager1");
        userRepository.save(manager);

        Restaurant restaurant = TestDataMother.createRestaurant(null, "Nhà Hàng E2E", manager);
        savedRestaurant = restaurantRepository.save(restaurant);

        TableArea area = TestDataMother.createTableArea(null, "Khu VIP", savedRestaurant);
        entityManager.persist(area);

        RestaurantTable table = TestDataMother.createTable(null, "VIP-1", 4L, RestaurantTable.TableStatus.AVAILABLE,
                area);
        savedTable = restaurantTableRepository.save(table);
    }

    @Test
    void givenValidBookingData_whenPostBookingApi_thenDatabaseMustBeUpdatedAndReturn201() throws Exception {
        String requestJson = String.format("""
                    {
                        "tableIds": [%d],
                        "bookingTime": "2026-12-31T19:00:00",
                        "quantity": 4
                    }
                """, savedTable.getId());

        // Act & Assert
        mockMvc.perform(post("/api/v1/restaurants/{id}/bookings", savedRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Đặt bàn thành công"))
                .andExpect(jsonPath("$.data.guestCount").value(4));

        long resultBookingsCount = bookingRepository.count();
        assertThat(resultBookingsCount).isEqualTo(1L);
    }

    @Test
    void givenHcmAddress_whenGetSearchApi_thenRealGoongApiIsCalledAndReturns200OK() throws Exception {

        String addressQuery = "Hồ Chí Minh";
        Long radiusInMeters = 500000L;

        // Act & Assert
        mockMvc.perform(get("/api/v1/restaurants")
                .param("address", addressQuery)
                .param("cuisine", "Tất cả")
                .param("radius", String.valueOf(radiusInMeters))
                .param("page", "1")
                .param("limit", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Lấy danh sách nhà hàng thành công"));
    }
}
