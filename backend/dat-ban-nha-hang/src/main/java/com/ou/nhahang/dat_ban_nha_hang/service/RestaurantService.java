package com.ou.nhahang.dat_ban_nha_hang.service;

import java.time.LocalTime;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.BookingRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.GetRestaurantDetailRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.SearchRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.TableSearchRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoCoordinateResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoDirectionResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.GetRestaurantDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.SearchRestaurantResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.TableSearchResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.entity.Cuisine;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.RestaurantTable;
import com.ou.nhahang.dat_ban_nha_hang.entity.TableArea;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.BookingRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantTableRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.port.IGeolocationService;

@Service
@Transactional(readOnly = true)
public class RestaurantService implements IRestaurantService {

        private final RestaurantTableRepository restaurantTableRepository;
        private final RestaurantRepository restaurantRepository;
        private final UserRepository userRepository;
        private final BookingRepository bookingRepository;
        private final IGeolocationService geolocationService;

        public RestaurantService(RestaurantTableRepository restaurantTableRepository,
                        RestaurantRepository restaurantRepository,
                        UserRepository userRepository,
                        BookingRepository bookingRepository, IGeolocationService geolocationService) {
                this.restaurantTableRepository = restaurantTableRepository;
                this.restaurantRepository = restaurantRepository;
                this.userRepository = userRepository;
                this.bookingRepository = bookingRepository;
                this.geolocationService = geolocationService;
        }

<<<<<<< HEAD
    
    public List<RestaurantTableDTO> findTableExecute(LocalTime time, Long quantity, Long restaurantId) {
        List<RestaurantTable> tables =
            restaurantTableRepository.findTablesValidByTimeAndRes(time, restaurantId);
=======
        @Override
        public List<SearchRestaurantResponseDTO> searchRestaurantsExecute(SearchRestaurantRequestDTO requestDTO) {
                Point userLocation = geolocationService.getPointFromAddress(requestDTO.address());
>>>>>>> b35b2f3f82ce015a0b4f2322b569a3429786348f

                String pointWkt = String.format("POINT(%f %f)", userLocation.getY(), userLocation.getX());

                Pageable pageable = PageRequest.of(requestDTO.page(), requestDTO.limit());

                Page<Restaurant> restaurants = restaurantRepository.findNearByRestaurant(pointWkt, requestDTO.radius(),
                                pageable);

                List<SearchRestaurantResponseDTO> restaurantDTOs = restaurants.getContent().stream().map(restaurant -> {
                        return SearchRestaurantResponseDTO.builder()
                                        .restaurantId(restaurant.getId())
                                        .restaurantName(restaurant.getName())
                                        .restaurantLogo(restaurant.getLogo())
                                        .restaurantCuisines(
                                                        restaurant.getCuisines() != null
                                                                        ? restaurant.getCuisines().stream()
                                                                                        .map(Cuisine::getName)
                                                                                        .collect(Collectors.toList())
                                                                        : null)
                                        .restaurantAvgRating(restaurant.getAvgRating())
                                        .restaurantTotalReviews(restaurant.getTotalReviews())
                                        .restaurantAddress(restaurant.getAddress())
                                        .restaurantDistance(restaurant.calculateRestaurantDistance(userLocation))
                                        .restaurantDepositType(restaurant.getDepositPolicy() != null
                                                        ? restaurant.getDepositPolicy().name()
                                                        : null)
                                        .restaurantBaseDeposit(restaurant.getBaseDepositValue() != null
                                                        ? restaurant.getBaseDepositValue().doubleValue()
                                                        : 0.0)
                                        .restaurantIsOpen(restaurant.getStatus() == Restaurant.RestaurantStatus.OPENING)
                                        .restaurantLocation(restaurant.getLocation() != null
                                                        ? new GeoCoordinateResponseDTO.Result.Geometry.Location(
                                                                        restaurant.getLocation().getY(),
                                                                        restaurant.getLocation().getX())
                                                        : null)
                                        .build();
                }).collect(Collectors.toList());

                return restaurantDTOs;
        }

        @Override
        public TableSearchResponseDTO searchTablesExecute(Long restaurantId, TableSearchRequestDTO requestDTO) {
                if (!restaurantRepository.existsById(restaurantId)) {
                        throw new ResourceNotFoundException("Không tìm thấy nhà hàng với ID: " + restaurantId);
                }

                LocalDate parsedDate = LocalDate.parse(requestDTO.date());
                LocalTime parsedTime = LocalTime.parse(requestDTO.time());
                LocalDateTime requestedStartTime = LocalDateTime.of(parsedDate, parsedTime);
                LocalDateTime requestedEndTime = requestedStartTime.plusHours(2);

                List<RestaurantTable> allTables = restaurantTableRepository.findByRestaurantIdWithArea(restaurantId);

                Map<TableArea, List<RestaurantTable>> tablesByArea = allTables.stream()
                                .collect(Collectors.groupingBy(RestaurantTable::getTableArea));

                List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                                restaurantId, requestedStartTime, requestedEndTime);

                Set<Long> bookedTableIds = overlappingBookings.stream()
                                .map(Booking::getTables)
                                .<RestaurantTable>flatMap(tables -> tables != null ? tables.stream()
                                                : java.util.stream.Stream.empty())
                                .map(RestaurantTable::getId)
                                .collect(Collectors.toSet());

                List<TableSearchResponseDTO.AreaDTO> areaDTOs = new ArrayList<>();

                for (Map.Entry<TableArea, List<RestaurantTable>> entry : tablesByArea.entrySet()) {
                        TableArea area = entry.getKey();
                        List<RestaurantTable> tablesInArea = entry.getValue();

                        List<TableSearchResponseDTO.TableSearchItemDTO> tableDTOs = new ArrayList<>();
                        for (RestaurantTable table : tablesInArea) {
                                boolean isAvailable = true;
                                String reason = null;

                                if (table.getStatus() != RestaurantTable.TableStatus.AVAILABLE) {
                                        isAvailable = false;
                                        reason = table.getStatus().name();
                                } else if (table.getCapacity() < requestDTO.guests()) {
                                        isAvailable = false;
                                        reason = "CAPACITY_NOT_MET";
                                } else if (bookedTableIds.contains(table.getId())) {
                                        isAvailable = false;
                                        reason = "ALREADY_BOOKED";
                                }

                                tableDTOs.add(TableSearchResponseDTO.TableSearchItemDTO.builder()
                                                .tableId(table.getId())
                                                .label(table.getName())
                                                .capacity(table.getCapacity())
                                                .isAvailable(isAvailable)
                                                .reason(reason)
                                                .build());
                        }

                        areaDTOs.add(TableSearchResponseDTO.AreaDTO.builder()
                                        .areaName(area.getName())
                                        .tables(tableDTOs)
                                        .build());
                }

                return TableSearchResponseDTO.builder()
                                .restaurantId(restaurantId)
                                .requestContext(TableSearchResponseDTO.RequestContextDTO.builder()
                                                .dateTime(requestedStartTime)
                                                .guests(requestDTO.guests())
                                                .build())
                                .areas(areaDTOs)
                                .build();
        }

        @Override
        @Transactional
        public BookingResponseDTO bookingExecute(BookingRequestDTO requestDTO, Long userId, Long restaurantId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy người dùng với ID: " + userId));

                Restaurant restaurant = restaurantRepository.findById(restaurantId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy nhà hàng với ID: " + restaurantId));

                List<RestaurantTable> tables = restaurantTableRepository.findAllById(requestDTO.tableIds());
                if (tables.isEmpty()) {
                        throw new BusinessException("Bạn chưa chọn bàn nào hợp lệ, hoặc bàn không tồn tại.");
                }

                Booking booking = restaurant.makeBooking(
                                user,
                                restaurant,
                                new java.util.HashSet<>(tables),
                                requestDTO.bookingTime(),
                                requestDTO.quantity(),
                                null);

                restaurantRepository.save(restaurant);
                booking = bookingRepository.save(booking);

                return new BookingResponseDTO(
                                booking.getId(),
                                restaurant.getId(),
                                "Restaurant Name",
                                requestDTO.bookingTime(),
                                booking.getNumberOfPeople(),
                                booking.getStatus().name(),
                                booking.getNote());
        }

        @Override
        public GetRestaurantDetailResponseDTO getRestaurantDetailExecute(
                        GetRestaurantDetailRequestDTO requestDto) {
                Restaurant restaurant = restaurantRepository.findById(requestDto.restaurantId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy nhà hàng với ID: " + requestDto.restaurantId()));

                Point userLocation = requestDto.extractLocation();
                Point resLocation = restaurant.getLocation();

                GeoDirectionResponseDTO directions = null;
                if (userLocation != null && resLocation != null) {
                        try {
                                directions = geolocationService.getDirection(userLocation, resLocation);
                        } catch (Exception e) {
                        }
                }

                List<String> cuisines = restaurant.getCuisines() != null
                                ? restaurant.getCuisines().stream().map(Cuisine::getName).collect(Collectors.toList())
                                : new ArrayList<>();

                List<GetRestaurantDetailResponseDTO.OperationTimeDTO> operationTimes = restaurant
                                .getOperationTimes() != null ? restaurant.getOperationTimes().stream()
                                                .map(t -> GetRestaurantDetailResponseDTO.OperationTimeDTO.builder()
                                                                .day(DayOfWeek.of(t.getDay().intValue()).name())
                                                                .open(t.getStartTime() != null ? t
                                                                                .getStartTime()
                                                                                .toLocalTime()
                                                                                .toString() : "")
                                                                .close(t.getEndTime() != null ? t
                                                                                .getEndTime()
                                                                                .toLocalTime()
                                                                                .toString() : "")
                                                                .build())
                                                .collect(Collectors.toList())
                                                : new ArrayList<>();

                List<GetRestaurantDetailResponseDTO.TableAreaDTO> tableAreas = restaurant.getTableAreas() != null
                                ? restaurant.getTableAreas().stream()
                                                .map(a -> {
                                                        int availableTables = 0;
                                                        int maxCapacity = 0;
                                                        if (a.getTables() != null) {
                                                                for (RestaurantTable t : a.getTables()) {
                                                                        if (t.getStatus() == RestaurantTable.TableStatus.AVAILABLE) {
                                                                                availableTables++;
                                                                        }
                                                                        if (t.getCapacity() != null) {
                                                                                maxCapacity += t.getCapacity()
                                                                                                .intValue();
                                                                        }
                                                                }
                                                        }
                                                        return GetRestaurantDetailResponseDTO.TableAreaDTO.builder()
                                                                        .areaName(a.getName())
                                                                        .availableTables(availableTables)
                                                                        .maxCapacity(maxCapacity)
                                                                        .build();
                                                })
                                                .collect(Collectors.toList())
                                : new ArrayList<>();

                return GetRestaurantDetailResponseDTO.builder()
                                .restaurantId(restaurant.getId())
                                .restaurantName(restaurant.getName())
                                .restaurantImage(restaurant.getLogo())
                                .restaurantLogo(restaurant.getLogo())
                                .restaurantDescription(restaurant.getDescription())
                                .restaurantAddress(restaurant.getAddress())
                                .restaurantLocation(resLocation != null
                                                ? new GeoCoordinateResponseDTO.Result.Geometry.Location(
                                                                resLocation.getY(), resLocation.getX())
                                                : null)
                                .restaurantCuisines(cuisines)
                                .restaurantAvgRating(restaurant.getAvgRating())
                                .restaurantTotalReviews(restaurant.getTotalReviews())
                                .restaurantDepositPolicy(restaurant.getDepositPolicy() != null
                                                ? restaurant.getDepositPolicy().name()
                                                : null)
                                .restaurantBaseDeposit(restaurant.getBaseDepositValue())
                                .restaurantOperationTimes(operationTimes)
                                .restaurantTableAreas(tableAreas)
                                .restaurantDirections(directions)
                                .build();
        }
}
