package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.BookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.TableSearchRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.TableSearchResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.GetRestaurantDetailRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoDirectionResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.entity.Cuisine;
import com.ou.nhahang.dat_ban_nha_hang.entity.OperationTime;
import com.ou.nhahang.dat_ban_nha_hang.entity.TableArea;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTable;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.BookingRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantTableRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import com.ou.nhahang.dat_ban_nha_hang.utils.TestDataMother;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantTableRepository restaurantTableRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private com.ou.nhahang.dat_ban_nha_hang.service.port.IGeolocationService geolocationService;

    @InjectMocks
    private RestaurantService restaurantService;

    @Test
    public void givenValidTableSearchRequest_whenSearchTablesExecute_thenReturnAvailableTables() {
        // 1. Arrange
        Long restaurantId = 1L;
        TableSearchRequestDTO request = new TableSearchRequestDTO("2026-03-20", "19:00", 2L);

        when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
        when(restaurantTableRepository.findByRestaurantIdWithArea(restaurantId)).thenReturn(new ArrayList<>());
        when(bookingRepository.findOverlappingBookings(eq(restaurantId), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        // 2. Act
        TableSearchResponseDTO response = restaurantService.searchTablesExecute(restaurantId, request);

        // 3. Assert
        assertThat(response).isNotNull();
        assertThat(response.restaurantId()).isEqualTo(restaurantId);

    }

    @Test
    public void givenValidBookingRequest_whenBookingExecute_thenBookSuccessfully() {
        // 1. Arrange
        Long userId = 1L;
        Long restaurantId = 1L;
        LocalDateTime bookingTime = LocalDateTime.of(2026, 3, 20, 19, 0);
        BookingRequestDTO request = new BookingRequestDTO(bookingTime, 2L, List.of(1L));

        User user = TestDataMother.createUser(userId, "john_doe");
        Restaurant restaurant = TestDataMother.createRestaurant(restaurantId, "ResTest", null);
        RestaurantTable table = TestDataMother.createTable(1L, "T1", 4L, RestaurantTable.TableStatus.AVAILABLE, null);
        Booking mockBooking = TestDataMother.createBooking(1L, user, restaurant, bookingTime);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantTableRepository.findAllById(request.tableIds())).thenReturn(List.of(table));

        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        // 2. Act
        BookingResponseDTO response = restaurantService.bookingExecute(request, userId, restaurantId);

        // 3. Assert
        assertThat(response).isNotNull();
        assertThat(response.bookingId()).isEqualTo(mockBooking.getId());

        verify(restaurantRepository, times(1)).save(restaurant);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    public void givenNonExistentRestaurant_whenSearchTablesExecute_thenThrowResourceNotFoundException() {
        // 1. Arrange
        Long restaurantId = 999L;
        TableSearchRequestDTO request = new TableSearchRequestDTO("2026-03-20", "19:00", 2L);

        when(restaurantRepository.existsById(restaurantId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            restaurantService.searchTablesExecute(restaurantId, request);
        });
        assertThat(exception.getMessage()).isEqualTo("Không tìm thấy nhà hàng với ID: " + restaurantId);
    }

    @Test
    public void givenNonExistentUser_whenBookingExecute_thenThrowResourceNotFoundException() {
        // 1. Arrange
        Long userId = 999L;
        Long restaurantId = 1L;
        BookingRequestDTO request = new BookingRequestDTO(LocalDateTime.now(), 2L, List.of(1L));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            restaurantService.bookingExecute(request, userId, restaurantId);
        });
        assertThat(exception.getMessage()).isEqualTo("Không tìm thấy người dùng với ID: " + userId);
    }

    @Test
    public void givenEmptyValidTables_whenBookingExecute_thenThrowBusinessException() {
        // 1. Arrange
        Long userId = 1L;
        Long restaurantId = 1L;
        BookingRequestDTO request = new BookingRequestDTO(LocalDateTime.now(), 2L, List.of(99L));

        User user = TestDataMother.createUser(userId, "john_doe");
        Restaurant restaurant = TestDataMother.createRestaurant(restaurantId, "ResTest", null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantTableRepository.findAllById(request.tableIds())).thenReturn(new ArrayList<>());
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantService.bookingExecute(request, userId, restaurantId);
        });
        assertThat(exception.getMessage()).isEqualTo("Bạn chưa chọn bàn nào hợp lệ, hoặc bàn không tồn tại.");
    }

    @Test
    public void givenValidRequest_whenGetRestaurantDetailExecute_thenReturnFullyMappedDTO() {
        // 1. Arrange
        Long restaurantId = 1L;
        GetRestaurantDetailRequestDTO request = new GetRestaurantDetailRequestDTO("10.0,106.0", restaurantId);

        Restaurant restaurant = TestDataMother.createRestaurant(restaurantId, "Full Restaurant", null);
        OperationTime ot = TestDataMother.createOperationTime(1L, 1L, LocalDateTime.of(2026, 1, 1, 8, 0),
                LocalDateTime.of(2026, 1, 1, 22, 0));
        restaurant.setOperationTimes(List.of(ot));

        TableArea area = TestDataMother.createTableArea(1L, "Tầng 1", restaurant);
        RestaurantTable table = TestDataMother.createTable(1L, "T1", 4L, RestaurantTable.TableStatus.AVAILABLE, area);
        area.setTables(List.of(table));
        restaurant.setTableAreas(List.of(area));

        Cuisine cuisine = TestDataMother.createCuisine(1L, "Món Việt");
        restaurant.setCuisines(new java.util.HashSet<>(List.of(cuisine)));

        GeoDirectionResponseDTO mockDirections = mock(GeoDirectionResponseDTO.class);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(geolocationService.getDirection(any(Point.class), any(Point.class))).thenReturn(mockDirections);
        GetRestaurantDetailResponseDTO response = restaurantService.getRestaurantDetailExecute(request);

        // 3. Assert
        // State-Based Testing
        assertThat(response).isNotNull();
        assertThat(response.restaurantId()).isEqualTo(restaurantId);
        assertThat(response.restaurantOperationTimes()).hasSize(1);
        assertThat(response.restaurantOperationTimes().get(0).day()).isEqualTo("MONDAY");
        assertThat(response.restaurantTableAreas()).hasSize(1);
        assertThat(response.restaurantTableAreas().get(0).availableTables()).isEqualTo(1);
        assertThat(response.restaurantCuisines()).contains("Món Việt");
        assertThat(response.restaurantDirections()).isEqualTo(mockDirections);
    }

    @Test
    public void givenRestaurantNotFound_whenGetRestaurantDetailExecute_thenThrowResourceNotFoundException() {
        // 1. Arrange
        Long restaurantId = 999L;
        GetRestaurantDetailRequestDTO request = new GetRestaurantDetailRequestDTO("10.0,106.0", restaurantId);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            restaurantService.getRestaurantDetailExecute(request);
        });
        assertThat(exception.getMessage()).isEqualTo("Không tìm thấy nhà hàng với ID: " + restaurantId);
    }

    @Test
    public void givenGeolocationServiceFails_whenGetRestaurantDetailExecute_thenReturnsDTOWithNullDirections() {
        // 1. Arrange
        Long restaurantId = 1L;
        GetRestaurantDetailRequestDTO request = new GetRestaurantDetailRequestDTO("10.0,106.0", restaurantId);

        Restaurant restaurant = TestDataMother.createRestaurant(restaurantId, "Res Fails Geolocation", null);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(geolocationService.getDirection(any(Point.class), any(Point.class)))
                .thenThrow(new RuntimeException("API Sập"));

        GetRestaurantDetailResponseDTO response = restaurantService.getRestaurantDetailExecute(request);

        assertThat(response).isNotNull();
        assertThat(response.restaurantDirections()).isNull();
    }
}
