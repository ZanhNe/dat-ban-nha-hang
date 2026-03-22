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
import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void givenValidPointAndRadius_whenFindNearByRestaurant_thenReturnPagedRestaurants() {
        // 1. Arrange
        User manager = TestDataMother.createUser(null, "manager1");
        userRepository.save(manager);

        Restaurant restaurant = TestDataMother.createRestaurant(null, "Test Res MySQL", manager);
        restaurantRepository.save(restaurant);

        // 2. Act
        String pointWkt = "POINT(10.76 106.68)";
        Page<Restaurant> result = restaurantRepository.findNearByRestaurant(pointWkt, 5000, PageRequest.of(0, 10));

        // 3. Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    public void givenSavedRestaurant_whenFindById_thenReturnRestaurant() {
        // 1. Arrange
        User manager = TestDataMother.createUser(null, "manager_find_by_id");
        userRepository.save(manager);

        Restaurant restaurant = TestDataMother.createRestaurant(null, "Test Fetch By Id", manager);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        // 2. Act
        java.util.Optional<Restaurant> result = restaurantRepository.findById(savedRestaurant.getId());

        // 3. Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Fetch By Id");
        assertThat(result.get().getManager().getUsername()).isEqualTo("manager_find_by_id");
    }

    @Test
    public void givenRestaurantWithMenu_whenFindById_thenReturnCompleteStructure() {
        // 1. Arrange
        User manager = TestDataMother.createUser(null, "manager_menu");
        entityManager.persist(manager);

        Restaurant restaurant = TestDataMother.createRestaurant(null, "Test Menu Mapping", manager);
        entityManager.persist(restaurant);

        com.ou.nhahang.dat_ban_nha_hang.entity.Menu menu1 = TestDataMother.createMenu(restaurant, "Menu 1", null);
        entityManager.persist(menu1);

        restaurant.getMenus().add(menu1);
        entityManager.persist(restaurant);
        entityManager.flush();
        entityManager.clear();

        // 2. Act
        java.util.Optional<Restaurant> result = restaurantRepository.findById(restaurant.getId());

        // 3. Assert
        assertThat(result).isPresent();
        assertThat(result.get().getMenus()).hasSize(1);
        assertThat(result.get().getMenus().iterator().next().getName()).isEqualTo("Menu 1");
    }
}
