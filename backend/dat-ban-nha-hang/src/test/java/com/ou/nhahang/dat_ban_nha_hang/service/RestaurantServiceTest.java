package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.BookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.TableSearchRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.TableSearchResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
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

    @InjectMocks
    private RestaurantService restaurantService;

    // --- TEST THÀNH CÔNG ---

    @Test
    public void givenValidTableSearchRequest_whenSearchTablesExecute_thenReturnAvailableTables() {
        // 1. Arrange
        Long restaurantId = 1L;
        TableSearchRequestDTO request = new TableSearchRequestDTO("2026-03-20", "19:00", 2L);

        // Stub: Trả về hard-code data khi SUT yêu cầu dữ liệu.
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

        // Output assertion: Ở đây không cần gọi verify() đọc DB (như findById), kiểm
        // tra trạng thái là đủ.
    }

    @Test
    public void givenValidBookingRequest_whenBookingExecute_thenBookSuccessfully() {
        // 1. Arrange
        Long userId = 1L;
        Long restaurantId = 1L;
        LocalDateTime bookingTime = LocalDateTime.of(2026, 3, 20, 19, 0);
        BookingRequestDTO request = new BookingRequestDTO(bookingTime, 2L, List.of(1L));

        // Áp dụng Object Mother
        User user = TestDataMother.createUser(userId, "john_doe");
        Restaurant restaurant = TestDataMother.createRestaurant(restaurantId, "ResTest", null);
        RestaurantTable table = TestDataMother.createTable(1L, "T1", 4L, RestaurantTable.TableStatus.AVAILABLE, null);
        Booking mockBooking = TestDataMother.createBooking(1L, user, restaurant, bookingTime);

        // Stub: Cung cấp data đầu vào cố định.
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantTableRepository.findAllById(request.tableIds())).thenReturn(List.of(table));

        // Stub: Cho hành vi save -> trả lại Fake ID
        when(bookingRepository.save(any(Booking.class))).thenReturn(mockBooking);

        // 2. Act
        BookingResponseDTO response = restaurantService.bookingExecute(request, userId, restaurantId);

        // 3. Assert
        assertThat(response).isNotNull();
        assertThat(response.bookingId()).isEqualTo(mockBooking.getId());

        // Verify (Interaction Check qua MOCK)
        // Xác minh chắc chắn rằng hành động thay đổi dữ liệu đã được kích hoạt
        verify(restaurantRepository, times(1)).save(restaurant);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    // --- TEST NGOẠI LỆ (EXCEPTION) ---

    @Test
    public void givenNonExistentRestaurant_whenSearchTablesExecute_thenThrowResourceNotFoundException() {
        // 1. Arrange
        Long restaurantId = 999L;
        TableSearchRequestDTO request = new TableSearchRequestDTO("2026-03-20", "19:00", 2L);

        // Stub: giả lập luồng trả về false => kích hoạt lỗi do không tìm thấy nhà hàng
        when(restaurantRepository.existsById(restaurantId)).thenReturn(false);

        // 2. Act & 3. Assert
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

        // Stub: user không tồn tại trả ra giá trị rỗng (Empty Optional)
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
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

        // Stub: Trả về trạng thái bàn trống/không hợp lệ
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantTableRepository.findAllById(request.tableIds())).thenReturn(new ArrayList<>());

        // 2. Act & 3. Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantService.bookingExecute(request, userId, restaurantId);
        });
        assertThat(exception.getMessage()).isEqualTo("Bạn chưa chọn bàn nào hợp lệ, hoặc bàn không tồn tại.");
    }
}
