package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.*;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.*;
import com.ou.nhahang.dat_ban_nha_hang.entity.*;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.*;
import com.ou.nhahang.dat_ban_nha_hang.service.IManagerMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerMenuService implements IManagerMenuService {

        private final RestaurantRepository restaurantRepository;
        private final MenuRepository menuRepository;
        private final FoodGroupRepository foodGroupRepository;
        private final FoodDescriptionRepository foodDescriptionRepository;
        private final FoodOptionGroupRepository foodOptionGroupRepository;
        private final FoodOptionRepository foodOptionRepository;

        public ManagerMenuService(RestaurantRepository restaurantRepository,
                        MenuRepository menuRepository,
                        FoodGroupRepository foodGroupRepository,
                        FoodDescriptionRepository foodDescriptionRepository,
                        FoodOptionGroupRepository foodOptionGroupRepository,
                        FoodOptionRepository foodOptionRepository) {
                this.restaurantRepository = restaurantRepository;
                this.menuRepository = menuRepository;
                this.foodGroupRepository = foodGroupRepository;
                this.foodDescriptionRepository = foodDescriptionRepository;
                this.foodOptionGroupRepository = foodOptionGroupRepository;
                this.foodOptionRepository = foodOptionRepository;
        }

        private Restaurant getMenuRestaurant(Long managerId) {
                return restaurantRepository.findAll().stream()
                                .filter(r -> r.getManager() != null && r.getManager().getId().equals(managerId))
                                .findFirst()
                                .orElseThrow(
                                                () -> new ResourceNotFoundException("Bạn không quản lý nhà hàng nào."));
        }

        // Menu chính

        @Override
        public List<ManagerMenuResponseDTO> getMenus(Long managerId) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                return menuRepository.findByRestaurantId(restaurant.getId()).stream()
                                .map(m -> ManagerMenuResponseDTO.builder()
                                                .menuId(m.getId())
                                                .name(m.getName())
                                                .description(m.getDescription())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public ManagerMenuResponseDTO createMenu(Long managerId, ManagerMenuRequestDTO.CreateOrUpdateMenu requestDTO) {
                Restaurant restaurant = getMenuRestaurant(managerId);

                Menu menu = Menu.builder()
                                .name(requestDTO.name())
                                .description(requestDTO.description())
                                .restaurant(restaurant)
                                .build();

                Menu savedMenu = menuRepository.save(menu);
                return ManagerMenuResponseDTO.builder()
                                .menuId(savedMenu.getId())
                                .name(savedMenu.getName())
                                .description(savedMenu.getDescription())
                                .build();
        }

        @Override
        @Transactional
        public ManagerMenuResponseDTO updateMenu(Long menuId, Long managerId,
                        ManagerMenuRequestDTO.CreateOrUpdateMenu requestDTO) {
                Restaurant restaurant = getMenuRestaurant(managerId);

                Menu menu = menuRepository.findByIdAndRestaurantId(menuId, restaurant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Menu"));

                menu.setName(requestDTO.name());
                menu.setDescription(requestDTO.description());

                Menu updatedMenu = menuRepository.save(menu);
                return ManagerMenuResponseDTO.builder()
                                .menuId(updatedMenu.getId())
                                .name(updatedMenu.getName())
                                .description(updatedMenu.getDescription())
                                .build();
        }

        @Override
        @Transactional
        public void deleteMenu(Long menuId, Long managerId) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                Menu menu = menuRepository.findByIdAndRestaurantId(menuId, restaurant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Menu"));

                // Thường thì sẽ là soft delete, nhưng ở đây tạm sẽ hard delete :))
                menuRepository.delete(menu);
        }

        // Food Group

        @Override
        public List<ManagerFoodGroupResponseDTO> getFoodGroups(Long menuId, Long managerId) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                menuRepository.findByIdAndRestaurantId(menuId, restaurant.getId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Menu không thuộc nhà hàng của bạn."));

                return foodGroupRepository.findByMenuId(menuId).stream()
                                .map(fg -> ManagerFoodGroupResponseDTO.builder()
                                                .groupId(fg.getId())
                                                .name(fg.getName())
                                                .description(fg.getDescription())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public ManagerFoodGroupResponseDTO createFoodGroup(Long menuId, Long managerId,
                        ManagerFoodGroupRequestDTO.CreateOrUpdateFoodGroup requestDTO) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                Menu menu = menuRepository.findByIdAndRestaurantId(menuId, restaurant.getId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Menu không thuộc nhà hàng của bạn."));

                FoodGroup fg = new FoodGroup();
                fg.setName(requestDTO.name());
                fg.setDescription(requestDTO.description());
                fg.setMenu(menu);

                FoodGroup savedGroup = foodGroupRepository.save(fg);
                return ManagerFoodGroupResponseDTO.builder()
                                .groupId(savedGroup.getId())
                                .name(savedGroup.getName())
                                .description(savedGroup.getDescription())
                                .build();
        }

        @Override
        @Transactional
        public ManagerFoodGroupResponseDTO updateFoodGroup(Long foodGroupId, Long managerId,
                        ManagerFoodGroupRequestDTO.CreateOrUpdateFoodGroup requestDTO) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                FoodGroup fg = foodGroupRepository.findByIdAndMenuRestaurantId(foodGroupId, restaurant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm món ăn."));

                fg.setName(requestDTO.name());
                fg.setDescription(requestDTO.description());

                FoodGroup updated = foodGroupRepository.save(fg);
                return ManagerFoodGroupResponseDTO.builder()
                                .groupId(updated.getId())
                                .name(updated.getName())
                                .description(updated.getDescription())
                                .build();
        }

        @Override
        @Transactional
        public void deleteFoodGroup(Long foodGroupId, Long managerId) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                FoodGroup fg = foodGroupRepository.findByIdAndMenuRestaurantId(foodGroupId, restaurant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm món ăn."));

                foodGroupRepository.delete(fg);
        }

        // Food Description

        @Override
        public List<ManagerFoodResponseDTO> getFoods(Long foodGroupId, Long managerId) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                foodGroupRepository.findByIdAndMenuRestaurantId(foodGroupId, restaurant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Ngành món ăn không thuộc nhà hàng của bạn."));

                return foodDescriptionRepository.findByFoodGroupId(foodGroupId).stream()
                                .map(this::mapToFoodResponseDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public ManagerFoodResponseDTO createFood(Long foodGroupId, Long managerId,
                        ManagerFoodRequestDTO.CreateOrUpdateFood requestDTO) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                FoodGroup fg = foodGroupRepository.findByIdAndMenuRestaurantId(foodGroupId, restaurant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Ngành món ăn không thuộc nhà hàng của bạn."));

                FoodDescription food = new FoodDescription();
                food.setName(requestDTO.name());
                food.setDescription(requestDTO.description());
                food.setImage(requestDTO.image() != null ? requestDTO.image() : "");
                food.setPrice(requestDTO.price());
                food.setStatus(DescriptionStatus.valueOf(requestDTO.status().toUpperCase()));
                food.setFoodGroup(fg);

                if (requestDTO.optionGroupIds() != null && !requestDTO.optionGroupIds().isEmpty()) {
                        List<FoodOptionGroup> optionGroups = foodOptionGroupRepository
                                        .findAllById(requestDTO.optionGroupIds());
                        food.setOptionGroups(optionGroups);
                }

                FoodDescription savedFood = foodDescriptionRepository.save(food);
                return mapToFoodResponseDTO(savedFood);
        }

        @Override
        @Transactional
        public ManagerFoodResponseDTO updateFood(Long foodId, Long managerId,
                        ManagerFoodRequestDTO.CreateOrUpdateFood requestDTO) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                FoodDescription food = foodDescriptionRepository
                                .findByIdAndFoodGroupMenuRestaurantId(foodId, restaurant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn."));

                food.setName(requestDTO.name());
                food.setDescription(requestDTO.description());
                if (requestDTO.image() != null) {
                        food.setImage(requestDTO.image());
                }
                food.setPrice(requestDTO.price());
                food.setStatus(DescriptionStatus.valueOf(requestDTO.status().toUpperCase()));

                if (requestDTO.optionGroupIds() != null) {
                        List<FoodOptionGroup> optionGroups = foodOptionGroupRepository
                                        .findAllById(requestDTO.optionGroupIds());
                        food.setOptionGroups(optionGroups);
                } else {
                        food.getOptionGroups().clear();
                }

                FoodDescription updatedFood = foodDescriptionRepository.save(food);
                return mapToFoodResponseDTO(updatedFood);
        }

        @Override
        @Transactional
        public void deleteFood(Long foodId, Long managerId) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                FoodDescription food = foodDescriptionRepository
                                .findByIdAndFoodGroupMenuRestaurantId(foodId, restaurant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn."));

                foodDescriptionRepository.delete(food);
        }

        private ManagerFoodResponseDTO mapToFoodResponseDTO(FoodDescription f) {
                List<ManagerFoodResponseDTO.OptionGroupResponse> ogResponses = f.getOptionGroups().stream()
                                .map(og -> ManagerFoodResponseDTO.OptionGroupResponse.builder()
                                                .optionGroupId(og.getId())
                                                .name(og.getName())
                                                .status(og.getStatus().name())
                                                .build())
                                .collect(Collectors.toList());

                return ManagerFoodResponseDTO.builder()
                                .foodId(f.getId())
                                .name(f.getName())
                                .description(f.getDescription())
                                .image(f.getImage())
                                .price(f.getPrice())
                                .status(f.getStatus().name())
                                .optionGroups(ogResponses)
                                .build();
        }

        // Option Group

        @Override
        public List<ManagerOptionGroupResponseDTO> getOptionGroups(Long restaurantId, Long managerId) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                if (!restaurant.getId().equals(restaurantId)) {
                        throw new ResourceNotFoundException("Không có quyền quản lý nhóm tùy chọn này.");
                }

                return foodOptionGroupRepository.findByRestaurantId(restaurantId).stream()
                                .map(og -> ManagerOptionGroupResponseDTO.builder()
                                                .optionGroupId(og.getId())
                                                .name(og.getName())
                                                .description(og.getDescription())
                                                .status(og.getStatus().name())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public ManagerOptionGroupResponseDTO createOptionGroup(Long restaurantId, Long managerId,
                        ManagerOptionGroupRequestDTO.CreateOrUpdateOptionGroup requestDTO) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                if (!restaurant.getId().equals(restaurantId)) {
                        throw new ResourceNotFoundException("Không có quyền quản lý.");
                }

                FoodOptionGroup og = new FoodOptionGroup();
                og.setName(requestDTO.name());
                og.setDescription(requestDTO.description());
                og.setStatus(DescriptionStatus.valueOf(requestDTO.status().toUpperCase()));
                og.setRestaurant(restaurant);

                FoodOptionGroup saved = foodOptionGroupRepository.save(og);
                return ManagerOptionGroupResponseDTO.builder()
                                .optionGroupId(saved.getId())
                                .name(saved.getName())
                                .description(saved.getDescription())
                                .status(saved.getStatus().name())
                                .build();
        }

        @Override
        @Transactional
        public ManagerOptionGroupResponseDTO updateOptionGroup(Long optionGroupId, Long managerId,
                        ManagerOptionGroupRequestDTO.CreateOrUpdateOptionGroup requestDTO) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                FoodOptionGroup og = foodOptionGroupRepository
                                .findByIdAndRestaurantId(optionGroupId, restaurant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm tùy chọn."));

                og.setName(requestDTO.name());
                og.setDescription(requestDTO.description());
                og.setStatus(DescriptionStatus.valueOf(requestDTO.status().toUpperCase()));

                FoodOptionGroup saved = foodOptionGroupRepository.save(og);
                return ManagerOptionGroupResponseDTO.builder()
                                .optionGroupId(saved.getId())
                                .name(saved.getName())
                                .description(saved.getDescription())
                                .status(saved.getStatus().name())
                                .build();
        }

        @Override
        @Transactional
        public void deleteOptionGroup(Long optionGroupId, Long managerId) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                FoodOptionGroup og = foodOptionGroupRepository
                                .findByIdAndRestaurantId(optionGroupId, restaurant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhóm tùy chọn."));

                foodOptionGroupRepository.delete(og);
        }

        // Option

        @Override
        public List<ManagerOptionResponseDTO> getOptions(Long optionGroupId, Long managerId) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                foodOptionGroupRepository.findByIdAndRestaurantId(optionGroupId, restaurant.getId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Nhóm tùy chọn không thuộc về bạn."));

                return foodOptionRepository.findByOptionGroupId(optionGroupId).stream()
                                .map(o -> ManagerOptionResponseDTO.builder()
                                                .optionId(o.getId())
                                                .name(o.getName())
                                                .description(o.getDescription())
                                                .price(o.getPrice())
                                                .status(o.getStatus().name())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public ManagerOptionResponseDTO createOption(Long optionGroupId, Long managerId,
                        ManagerOptionRequestDTO.CreateOrUpdateOption requestDTO) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                FoodOptionGroup og = foodOptionGroupRepository
                                .findByIdAndRestaurantId(optionGroupId, restaurant.getId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "Nhóm tùy chọn không thuộc về bạn."));

                FoodOption option = new FoodOption();
                option.setName(requestDTO.name());
                option.setDescription(requestDTO.description());
                option.setPrice(requestDTO.price());
                option.setStatus(DescriptionStatus.valueOf(requestDTO.status().toUpperCase()));
                option.setOptionGroup(og);

                FoodOption saved = foodOptionRepository.save(option);
                return ManagerOptionResponseDTO.builder()
                                .optionId(saved.getId())
                                .name(saved.getName())
                                .description(saved.getDescription())
                                .price(saved.getPrice())
                                .status(saved.getStatus().name())
                                .build();
        }

        @Override
        @Transactional
        public ManagerOptionResponseDTO updateOption(Long optionId, Long managerId,
                        ManagerOptionRequestDTO.CreateOrUpdateOption requestDTO) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                FoodOption option = foodOptionRepository
                                .findByIdAndOptionGroupRestaurantId(optionId, restaurant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tùy chọn."));

                option.setName(requestDTO.name());
                option.setDescription(requestDTO.description());
                option.setPrice(requestDTO.price());
                option.setStatus(DescriptionStatus.valueOf(requestDTO.status().toUpperCase()));

                FoodOption saved = foodOptionRepository.save(option);
                return ManagerOptionResponseDTO.builder()
                                .optionId(saved.getId())
                                .name(saved.getName())
                                .description(saved.getDescription())
                                .price(saved.getPrice())
                                .status(saved.getStatus().name())
                                .build();
        }

        @Override
        @Transactional
        public void deleteOption(Long optionId, Long managerId) {
                Restaurant restaurant = getMenuRestaurant(managerId);
                FoodOption option = foodOptionRepository
                                .findByIdAndOptionGroupRestaurantId(optionId, restaurant.getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tùy chọn."));

                foodOptionRepository.delete(option);
        }
}
