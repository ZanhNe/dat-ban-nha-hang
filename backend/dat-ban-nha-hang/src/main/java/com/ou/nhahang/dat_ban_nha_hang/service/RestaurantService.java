package com.ou.nhahang.dat_ban_nha_hang.service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ou.nhahang.dat_ban_nha_hang.dto.RestaurantTableDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.BookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTable;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.repository.BookingRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantTableRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;

@Service
public class RestaurantService implements IRestaurantService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public RestaurantService(RestaurantTableRepository restaurantTableRepository,
                             RestaurantRepository restaurantRepository,
                             UserRepository userRepository,
                             BookingRepository bookingRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Tương ứng với bước findTableExecute(time, quantity, resId)
     * trong sequence diagram.
     *
     * - Gọi xuống repository: findTablesValidByTimeAndRes(time, resId)
     * - Lọc thêm theo sức chứa (capacity >= quantity)
     * - Mapping sang DTO và trả về cho controller/UI.
     */
    @Override
    public List<RestaurantTableDTO> findTableExecute(LocalTime time, Long quantity, Long restaurantId) {
        List<RestaurantTable> tables = restaurantTableRepository.findTablesValidByTimeAndRes(time, restaurantId);

        return tables.stream()
                .filter(t -> t.getCapacity() != null && t.getCapacity() >= quantity)
                .map(RestaurantTableDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDTO bookingExecute(BookingRequestDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Restaurant restaurant = restaurantRepository.findById(requestDTO.restaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        List<RestaurantTable> tables = restaurantTableRepository.findAllById(requestDTO.tableIds());
        if (tables.isEmpty()) {
            throw new RuntimeException("No valid tables selected");
        }

        Booking booking = restaurant.makeBooking(
                user, 
                restaurant, 
                new java.util.HashSet<>(tables), 
                requestDTO.bookingTime(), 
                requestDTO.quantity(), 
                null
        );

        restaurantRepository.save(restaurant); 
        booking = bookingRepository.save(booking);

        return new BookingResponseDTO(
                booking.getId(),
                restaurant.getId(),
                "Restaurant Name", // placeholder as name doesn't exist
                requestDTO.bookingTime(),
                booking.getNumberOfPeople(),
                booking.getStatus().name(),
                booking.getNote()
        );
    }
}

