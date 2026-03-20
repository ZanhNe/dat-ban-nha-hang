package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.utils.TestDataMother;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenValidPointAndRadius_whenFindNearByRestaurant_thenReturnPagedRestaurants() {
        // 1. Arrange
        // Tạo User (Manager) trước vì manager_id trên Restaurant không được phép null
        // (Ràng buộc toàn vẹn)
        User manager = TestDataMother.createUser(null, "manager1");
        userRepository.save(manager);

        // Tạo Restaurant và gán manager vừa tạo
        Restaurant restaurant = TestDataMother.createRestaurant(null, "Test Res MySQL", manager);
        restaurantRepository.save(restaurant);

        // 2. Act
        // MySQL SRID 4326 expects Latitude first, then Longitude. (Lat, Lon)
        String pointWkt = "POINT(10.76 106.68)";
        Page<Restaurant> result = restaurantRepository.findNearByRestaurant(pointWkt, 5000, PageRequest.of(0, 10));

        // 3. Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }
}
