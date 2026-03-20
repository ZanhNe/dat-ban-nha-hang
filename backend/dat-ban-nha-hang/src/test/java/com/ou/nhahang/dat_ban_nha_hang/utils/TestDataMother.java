package com.ou.nhahang.dat_ban_nha_hang.utils;

import java.time.LocalDateTime;
import java.util.HashSet;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.entity.BookingTime;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTable;
import com.ou.nhahang.dat_ban_nha_hang.entity.Review;
import com.ou.nhahang.dat_ban_nha_hang.entity.TableArea;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;

public class TestDataMother {

    private static final GeometryFactory geometryFactory = new GeometryFactory(
            new org.locationtech.jts.geom.PrecisionModel(), 4326);

    public static RestaurantTable createTable(Long id, String name, Long capacity, RestaurantTable.TableStatus status,
            TableArea area) {
        RestaurantTable table = new RestaurantTable();
        table.setId(id);
        table.setName(name);
        table.setCapacity(capacity);
        table.setStatus(status);
        table.setTableArea(area);
        return table;
    }

    public static User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("password123");
        user.setFullName("Test User " + username);
        user.setEmail(username + "@test.com");
        user.setPhone("01234567" + id);
        user.setAddress("Test Address");
        user.setStatus(User.UserStatus.ACTIVE);
        return user;
    }

    public static Restaurant createRestaurant(Long id, String name, User manager) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(id);
        restaurant.setName(name);
        restaurant.setLogo("logo.png");
        restaurant.setDescription("Test Description");
        restaurant.setStatus(Restaurant.RestaurantStatus.OPENING);
        restaurant.setAvgRating(0.0);
        restaurant.setDayOfWeek(127); // Every day
        restaurant.setBaseDepositValue(50000L);
        restaurant.setDepositPolicy(Restaurant.DepositType.FIXED);
        restaurant.setAddress("Test Restaurant Address");
        restaurant.setCommissionType(Restaurant.CommissionType.PERCENTAGE);
        restaurant.setBaseCommissionValue(10L);
        restaurant.setManager(manager);

        Point point = geometryFactory.createPoint(new Coordinate(106.68, 10.76)); // arbitrary coord
        restaurant.setLocation(point);

        return restaurant;
    }

    public static TableArea createTableArea(Long id, String name, Restaurant restaurant) {
        TableArea area = new TableArea();
        area.setId(id);
        area.setName(name);
        area.setStatus(TableArea.TableAreaStatus.ACTIVE);
        area.setRestaurant(restaurant);
        return area;
    }

    public static Booking createBooking(Long id, User user, Restaurant restaurant, LocalDateTime time) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setNumberOfPeople(2L);
        booking.setStatus(Booking.BookingStatus.AWAITING_CONFIRMATION);
        booking.setBookingUser(user);
        booking.setRestaurant(restaurant);

        BookingTime bookingTime = new BookingTime(time, restaurant);
        bookingTime.setId(id); // Using the same id for convenience in tests
        booking.setBookingTime(bookingTime);

        booking.setTables(new HashSet<>());
        return booking;
    }

    public static Review createReview(Long id, Restaurant restaurant, User user, int rating, String comment) {
        Review review = new Review();
        review.setId(id);
        review.setRestaurant(restaurant);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        return review;
    }
}
