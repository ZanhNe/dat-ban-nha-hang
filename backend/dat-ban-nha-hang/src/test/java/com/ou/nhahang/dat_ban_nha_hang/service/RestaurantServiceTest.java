package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.BookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.TableSearchRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.TableSearchResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.GetRestaurantDetailRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.SearchRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoDirectionResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantMenuResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.SearchRestaurantResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.entity.Cuisine;
import com.ou.nhahang.dat_ban_nha_hang.entity.FoodDescription;
import com.ou.nhahang.dat_ban_nha_hang.entity.FoodGroup;
import com.ou.nhahang.dat_ban_nha_hang.entity.Menu;
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
import com.ou.nhahang.dat_ban_nha_hang.repository.ReviewRepository;
import com.ou.nhahang.dat_ban_nha_hang.utils.TestDataMother;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.GetRestaurantReviewRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantReviewResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.CursorPaginationResult;
import com.ou.nhahang.dat_ban_nha_hang.entity.Review;

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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Mock
    private ReviewRepository reviewRepository;

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

    @Test
    void getRestaurantMenuExecute_Success_ShouldMapNestedDTOsCorrectly() {
        // Arrange
        Long restaurantId = 1L;
        Restaurant restaurant = TestDataMother.createRestaurant(restaurantId, "Haidilao", null);

        Menu menu1 = TestDataMother.createMenu(restaurant, "Menu Khai Vị", 1L);
        FoodGroup group1 = TestDataMother.createFoodGroup(menu1, "Khai Vị Lạnh", 1L);
        FoodDescription fd1 = TestDataMother.createFoodDescription(group1, "Gỏi sứa", 50000L, 1L);
        FoodDescription fd2 = TestDataMother.createFoodDescription(group1, "Salad rong biển", 40000L, 2L);

        group1.getFoodDescriptions().add(fd1);
        group1.getFoodDescriptions().add(fd2);
        menu1.getFoodGroups().add(group1);
        restaurant.getMenus().add(menu1);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // Act
        GetRestaurantMenuResponseDTO response = restaurantService.getRestaurantMenuExecute(restaurantId);

        // Assert
        assertThat(response.restaurantId()).isEqualTo(1L);
        assertThat(response.restaurantName()).isEqualTo("Haidilao");
        assertThat(response.restaurantMenus()).hasSize(1);

        var menuDto = response.restaurantMenus().get(0);
        assertThat(menuDto.menuName()).isEqualTo("Menu Khai Vị");
        assertThat(menuDto.restaurantMenu()).hasSize(1);

        var groupDto = menuDto.restaurantMenu().get(0);
        assertThat(groupDto.groupName()).isEqualTo("Khai Vị Lạnh");
        assertThat(groupDto.items()).hasSize(2)
                .extracting("itemName")
                .containsExactly("Gỏi sứa", "Salad rong biển");
        assertThat(groupDto.items().get(0).itemPrice()).isEqualTo(50000L);

        /*
         * 💡 Phân tích Kỹ thuật:
         * Đây là bài Test cho cấu trúc phân tầng cực sâu: Restaurant -> Menu ->
         * FoodGroup -> FoodDescription
         * Chúng ta đã tận dụng TestDataMother để giả lập State phức tạp.
         * Phương pháp Test: State-Based Testing để khẳng định việc duyệt 3 vòng lặp
         * Stream không bị trật field (field-mapping assertion).
         */
    }

    @Test
    void getRestaurantMenuExecute_NotFound_ShouldThrowException() {
        // Arrange
        Long invalidId = 999L;
        when(restaurantRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> restaurantService.getRestaurantMenuExecute(invalidId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Không tìm thấy nhà hàng");
    }

    @Test
    void getRestaurantReviewsExecute_Success_ShouldReturnCursorPaginationResult() {
        // Arrange
        Long restaurantId = 1L;
        GetRestaurantReviewRequestDTO request = new GetRestaurantReviewRequestDTO(null, "NEWEST", 2, null);
        
        when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
        when(reviewRepository.countByRestaurantId(restaurantId)).thenReturn(10L);

        User user = TestDataMother.createUser(1L, "john_doe");
        Restaurant restaurant = TestDataMother.createRestaurant(restaurantId, "ResTest", null);

        Review r1 = new Review();
        r1.setId(3L);
        r1.setUser(user);
        r1.setRestaurant(restaurant);
        r1.setRating(5);
        r1.setComment("Great");
        r1.setCreatedAt(LocalDateTime.now());

        Review r2 = new Review();
        r2.setId(2L);
        r2.setUser(user);
        r2.setRestaurant(restaurant);
        r2.setRating(4);
        r2.setComment("Good");
        r2.setCreatedAt(LocalDateTime.now());

        Review r3 = new Review(); // Fake 3rd item to check hasMore
        r3.setId(1L);
        r3.setUser(user);
        r3.setRestaurant(restaurant);
        r3.setRating(3);
        r3.setComment("Okay");
        r3.setCreatedAt(LocalDateTime.now());

        List<Review> rawReviews = new ArrayList<>(List.of(r1, r2, r3));

        when(reviewRepository.findReviewsByCursor(eq(restaurantId), isNull(), isNull(), eq("NEWEST"), any()))
            .thenReturn(rawReviews);

        // Act
        CursorPaginationResult<GetRestaurantReviewResponseDTO> result = restaurantService.getRestaurantReviewsExecute(restaurantId, request);

        // Assert
        assertThat(result.data()).hasSize(2); // Should remove the 3rd item
        assertThat(result.data().get(0).comment()).isEqualTo("Great");
        assertThat(result.meta().hasMore()).isTrue();
        assertThat(result.meta().nextCursor()).isEqualTo(2L); // ID of the last item in the 2 items list
        assertThat(result.meta().totalReviews()).isEqualTo(10L);
    }

    @Test
    void getRestaurantReviewsExecute_NotFound_ShouldThrowException() {
        Long invalidId = 999L;
        GetRestaurantReviewRequestDTO request = new GetRestaurantReviewRequestDTO(null, "NEWEST", 10, null);
        when(restaurantRepository.existsById(invalidId)).thenReturn(false);

        assertThatThrownBy(() -> restaurantService.getRestaurantReviewsExecute(invalidId, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Không tìm thấy nhà hàng");
    }

    @Test
    void createReviewExecute_Success_ShouldSaveReviewAndReturnDTO() {
        // Arrange
        Long userId = 1L;
        Long restaurantId = 1L;
        com.ou.nhahang.dat_ban_nha_hang.dto.request.CreateRestaurantReviewRequestDTO request = new com.ou.nhahang.dat_ban_nha_hang.dto.request.CreateRestaurantReviewRequestDTO(5, "Tuyệt vời");

        User user = TestDataMother.createUser(userId, "john_doe");
        Restaurant restaurant = TestDataMother.createRestaurant(restaurantId, "ResTest", null);
        Booking booking = TestDataMother.createBooking(1L, user, restaurant, LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.COMPLETED);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reviewRepository.existsByRestaurantIdAndUserId(restaurantId, userId)).thenReturn(false);
        when(bookingRepository.findCompletedBookings(eq(userId), eq(restaurantId), any())).thenReturn(List.of(booking));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> {
            Review r = i.getArgument(0);
            r.setId(99L);
            return r;
        });

        // Act
        GetRestaurantReviewResponseDTO response = restaurantService.createReviewExecute(restaurantId, userId, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.rating()).isEqualTo(5);
        assertThat(response.comment()).isEqualTo("Tuyệt vời");

        verify(restaurantRepository).save(restaurant); 
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReviewExecute_AlreadyReviewed_ShouldThrowBusinessException() {
        Long userId = 1L;
        Long restaurantId = 1L;
        com.ou.nhahang.dat_ban_nha_hang.dto.request.CreateRestaurantReviewRequestDTO request = new com.ou.nhahang.dat_ban_nha_hang.dto.request.CreateRestaurantReviewRequestDTO(5, "Tuyệt vời");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(new Restaurant()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(reviewRepository.existsByRestaurantIdAndUserId(restaurantId, userId)).thenReturn(true);

        assertThatThrownBy(() -> restaurantService.createReviewExecute(restaurantId, userId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Bạn đã đánh giá nhà hàng này rồi");
    }

    @Test
    void createReviewExecute_NoCompletedBooking_ShouldThrowBusinessException() {
        Long userId = 1L;
        Long restaurantId = 1L;
        com.ou.nhahang.dat_ban_nha_hang.dto.request.CreateRestaurantReviewRequestDTO request = new com.ou.nhahang.dat_ban_nha_hang.dto.request.CreateRestaurantReviewRequestDTO(5, "Tuyệt vời");

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(new Restaurant()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(reviewRepository.existsByRestaurantIdAndUserId(restaurantId, userId)).thenReturn(false);
        when(bookingRepository.findCompletedBookings(eq(userId), eq(restaurantId), any())).thenReturn(new ArrayList<>());

        assertThatThrownBy(() -> restaurantService.createReviewExecute(restaurantId, userId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Bạn chưa từng dùng bữa tại nhà hàng này");
    }
}
