package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.Review;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.utils.TestDataMother;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EntityManager entityManager;

    private Restaurant restaurant;
    private User testUser;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testUser = TestDataMother.createUser(null, "test_user_review");
        entityManager.persist(testUser);

        restaurant = TestDataMother.createRestaurant(null, "Test Restaurant", testUser);
        entityManager.persist(restaurant);

        testBooking = TestDataMother.createBooking(null, testUser, restaurant, LocalDateTime.now());
        entityManager.persist(testBooking.getBookingTime());
        entityManager.persist(testBooking);
    }

    @Test
    void findReviewsByCursor_NEWEST_ShouldReturnCorrectOrder() {
        Review r1 = new Review();
        r1.setRestaurant(restaurant);
        r1.setUser(testUser);
        r1.setBooking(testBooking);
        r1.setRating(5);
        r1.setComment("Good");
        entityManager.persist(r1);

        Booking b2 = TestDataMother.createBooking(null, testUser, restaurant, LocalDateTime.now().plusDays(1));
        entityManager.persist(b2.getBookingTime());
        entityManager.persist(b2);

        Review r2 = new Review();
        r2.setRestaurant(restaurant);
        r2.setUser(testUser);
        r2.setBooking(b2);
        r2.setRating(4);
        r2.setComment("Okay");
        entityManager.persist(r2);
        entityManager.flush();

        List<Review> reviews = reviewRepository.findReviewsByCursor(
                restaurant.getId(), null, null, "NEWEST", PageRequest.of(0, 5));

        assertThat(reviews).hasSize(2);
        assertThat(reviews.get(0).getId()).isEqualTo(r2.getId());
        assertThat(reviews.get(1).getId()).isEqualTo(r1.getId());
    }

    @Test
    void existsByRestaurantIdAndUserId_ShouldWorkProperly() {
        Review r1 = new Review();
        r1.setRestaurant(restaurant);
        r1.setUser(testUser);
        r1.setBooking(testBooking);
        r1.setRating(5);
        r1.setComment("Good");
        entityManager.persist(r1);
        entityManager.flush();

        boolean exists = reviewRepository.existsByRestaurantIdAndUserId(restaurant.getId(), testUser.getId());
        assertThat(exists).isTrue();

        boolean notExists = reviewRepository.existsByRestaurantIdAndUserId(restaurant.getId(), 999L);
        assertThat(notExists).isFalse();
    }
}
