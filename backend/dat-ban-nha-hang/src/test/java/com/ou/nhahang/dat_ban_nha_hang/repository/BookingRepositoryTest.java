package com.ou.nhahang.dat_ban_nha_hang.repository;

import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
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
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager entityManager;

    private Restaurant restaurant;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = TestDataMother.createUser(null, "test_user_booking");
        entityManager.persist(testUser);

        restaurant = TestDataMother.createRestaurant(null, "Test Booking Restaurant", testUser);
        entityManager.persist(restaurant);
    }

    @Test
    void findCompletedBookings_ShouldReturnMostRecentFirst() {
        Booking b1 = TestDataMother.createBooking(null, testUser, restaurant, LocalDateTime.now().minusDays(2));
        b1.setStatus(Booking.BookingStatus.COMPLETED);
        entityManager.persist(b1.getBookingTime());
        entityManager.persist(b1);

        Booking b2 = TestDataMother.createBooking(null, testUser, restaurant, LocalDateTime.now().minusDays(1));
        b2.setStatus(Booking.BookingStatus.COMPLETED);
        entityManager.persist(b2.getBookingTime());
        entityManager.persist(b2);

        Booking b3 = TestDataMother.createBooking(null, testUser, restaurant, LocalDateTime.now());
        b3.setStatus(Booking.BookingStatus.CANCELLED);
        entityManager.persist(b3.getBookingTime());
        entityManager.persist(b3);

        entityManager.flush();

        List<Booking> completed = bookingRepository.findCompletedBookings(testUser.getId(), restaurant.getId(),
                PageRequest.of(0, 10));

        assertThat(completed).hasSize(2);
        assertThat(completed.get(0).getId()).isEqualTo(b2.getId());
        assertThat(completed.get(1).getId()).isEqualTo(b1.getId());
    }
}
