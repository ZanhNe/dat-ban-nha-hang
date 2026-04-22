package com.ou.nhahang.dat_ban_nha_hang.controller.manager;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.*;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.*;
import com.ou.nhahang.dat_ban_nha_hang.service.IManagerMenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/manager")
@PreAuthorize("hasAnyAuthority('ROLE_MANAGER')")
@RequiredArgsConstructor
public class ManagerMenuController {

        private final IManagerMenuService managerMenuService;

        // Menu chính

        @GetMapping("/menus")
        public ResponseEntity<ApiResponse<List<ManagerMenuResponseDTO>>> getMenus(Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                List<ManagerMenuResponseDTO> data = managerMenuService.getMenus(managerId);

                ApiResponse<List<ManagerMenuResponseDTO>> response = ApiResponse.<List<ManagerMenuResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách menu thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @PostMapping("/menus")
        public ResponseEntity<ApiResponse<ManagerMenuResponseDTO>> createMenu(
                        @Valid @RequestBody ManagerMenuRequestDTO.CreateOrUpdateMenu requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerMenuResponseDTO data = managerMenuService.createMenu(managerId, requestDTO);

                ApiResponse<ManagerMenuResponseDTO> response = ApiResponse.<ManagerMenuResponseDTO>builder()
                                .status(201)
                                .message("Tạo menu thành công")
                                .data(data)
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/menus/{menuId}")
        public ResponseEntity<ApiResponse<ManagerMenuResponseDTO>> updateMenu(
                        @PathVariable Long menuId,
                        @Valid @RequestBody ManagerMenuRequestDTO.CreateOrUpdateMenu requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerMenuResponseDTO data = managerMenuService.updateMenu(menuId, managerId, requestDTO);

                ApiResponse<ManagerMenuResponseDTO> response = ApiResponse.<ManagerMenuResponseDTO>builder()
                                .status(200)
                                .message("Cập nhật menu thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/menus/{menuId}")
        public ResponseEntity<ApiResponse<Void>> deleteMenu(
                        @PathVariable Long menuId,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                managerMenuService.deleteMenu(menuId, managerId);

                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .status(200)
                                .message("Xóa menu thành công")
                                .data(null)
                                .build();
                return ResponseEntity.ok(response);
        }

        // Food Group

        @GetMapping("/menus/{menuId}/food-groups")
        public ResponseEntity<ApiResponse<List<ManagerFoodGroupResponseDTO>>> getFoodGroups(
                        @PathVariable Long menuId,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                List<ManagerFoodGroupResponseDTO> data = managerMenuService.getFoodGroups(menuId, managerId);

                ApiResponse<List<ManagerFoodGroupResponseDTO>> response = ApiResponse
                                .<List<ManagerFoodGroupResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách nhóm món ăn thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @PostMapping("/menus/{menuId}/food-groups")
        public ResponseEntity<ApiResponse<ManagerFoodGroupResponseDTO>> createFoodGroup(
                        @PathVariable Long menuId,
                        @Valid @RequestBody ManagerFoodGroupRequestDTO.CreateOrUpdateFoodGroup requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerFoodGroupResponseDTO data = managerMenuService.createFoodGroup(menuId, managerId, requestDTO);

                ApiResponse<ManagerFoodGroupResponseDTO> response = ApiResponse.<ManagerFoodGroupResponseDTO>builder()
                                .status(201)
                                .message("Tạo nhóm món ăn thành công")
                                .data(data)
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/food-groups/{foodGroupId}")
        public ResponseEntity<ApiResponse<ManagerFoodGroupResponseDTO>> updateFoodGroup(
                        @PathVariable Long foodGroupId,
                        @Valid @RequestBody ManagerFoodGroupRequestDTO.CreateOrUpdateFoodGroup requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerFoodGroupResponseDTO data = managerMenuService.updateFoodGroup(foodGroupId, managerId,
                                requestDTO);

                ApiResponse<ManagerFoodGroupResponseDTO> response = ApiResponse.<ManagerFoodGroupResponseDTO>builder()
                                .status(200)
                                .message("Cập nhật nhóm món ăn thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/food-groups/{foodGroupId}")
        public ResponseEntity<ApiResponse<Void>> deleteFoodGroup(
                        @PathVariable Long foodGroupId,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                managerMenuService.deleteFoodGroup(foodGroupId, managerId);

                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .status(200)
                                .message("Xóa nhóm thành công")
                                .data(null)
                                .build();
                return ResponseEntity.ok(response);
        }

        // Food Item

        @GetMapping("/food-groups/{foodGroupId}/foods")
        public ResponseEntity<ApiResponse<List<ManagerFoodResponseDTO>>> getFoods(
                        @PathVariable Long foodGroupId,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                List<ManagerFoodResponseDTO> data = managerMenuService.getFoods(foodGroupId, managerId);

                ApiResponse<List<ManagerFoodResponseDTO>> response = ApiResponse.<List<ManagerFoodResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách món ăn thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @PostMapping("/food-groups/{foodGroupId}/foods")
        public ResponseEntity<ApiResponse<ManagerFoodResponseDTO>> createFood(
                        @PathVariable Long foodGroupId,
                        @Valid @RequestBody ManagerFoodRequestDTO.CreateOrUpdateFood requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerFoodResponseDTO data = managerMenuService.createFood(foodGroupId, managerId, requestDTO);

                ApiResponse<ManagerFoodResponseDTO> response = ApiResponse.<ManagerFoodResponseDTO>builder()
                                .status(201)
                                .message("Thêm món ăn thành công")
                                .data(data)
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/foods/{foodId}")
        public ResponseEntity<ApiResponse<ManagerFoodResponseDTO>> updateFood(
                        @PathVariable Long foodId,
                        @Valid @RequestBody ManagerFoodRequestDTO.CreateOrUpdateFood requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerFoodResponseDTO data = managerMenuService.updateFood(foodId, managerId, requestDTO);

                ApiResponse<ManagerFoodResponseDTO> response = ApiResponse.<ManagerFoodResponseDTO>builder()
                                .status(200)
                                .message("Cập nhật món ăn thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/foods/{foodId}")
        public ResponseEntity<ApiResponse<Void>> deleteFood(
                        @PathVariable Long foodId,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                managerMenuService.deleteFood(foodId, managerId);

                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .status(200)
                                .message("Xóa món ăn thành công")
                                .data(null)
                                .build();
                return ResponseEntity.ok(response);
        }

        // Option Group

        @GetMapping("/restaurants/{restaurantId}/option-groups")
        public ResponseEntity<ApiResponse<List<ManagerOptionGroupResponseDTO>>> getOptionGroups(
                        @PathVariable Long restaurantId,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                List<ManagerOptionGroupResponseDTO> data = managerMenuService.getOptionGroups(restaurantId, managerId);

                ApiResponse<List<ManagerOptionGroupResponseDTO>> response = ApiResponse
                                .<List<ManagerOptionGroupResponseDTO>>builder()
                                .status(200)
                                .message("Lấy danh sách nhóm tùy chọn thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @PostMapping("/restaurants/{restaurantId}/option-groups")
        public ResponseEntity<ApiResponse<ManagerOptionGroupResponseDTO>> createOptionGroup(
                        @PathVariable Long restaurantId,
                        @Valid @RequestBody ManagerOptionGroupRequestDTO.CreateOrUpdateOptionGroup requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerOptionGroupResponseDTO data = managerMenuService.createOptionGroup(restaurantId, managerId,
                                requestDTO);

                ApiResponse<ManagerOptionGroupResponseDTO> response = ApiResponse
                                .<ManagerOptionGroupResponseDTO>builder()
                                .status(201)
                                .message("Tạo nhóm tùy chọn thành công")
                                .data(data)
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/option-groups/{optionGroupId}")
        public ResponseEntity<ApiResponse<ManagerOptionGroupResponseDTO>> updateOptionGroup(
                        @PathVariable Long optionGroupId,
                        @Valid @RequestBody ManagerOptionGroupRequestDTO.CreateOrUpdateOptionGroup requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerOptionGroupResponseDTO data = managerMenuService.updateOptionGroup(optionGroupId, managerId,
                                requestDTO);

                ApiResponse<ManagerOptionGroupResponseDTO> response = ApiResponse
                                .<ManagerOptionGroupResponseDTO>builder()
                                .status(200)
                                .message("Cập nhật thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/option-groups/{optionGroupId}")
        public ResponseEntity<ApiResponse<Void>> deleteOptionGroup(
                        @PathVariable Long optionGroupId,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                managerMenuService.deleteOptionGroup(optionGroupId, managerId);

                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .status(200)
                                .message("Xóa thành công")
                                .data(null)
                                .build();
                return ResponseEntity.ok(response);
        }

        // Option

        @GetMapping("/option-groups/{optionGroupId}/options")
        public ResponseEntity<ApiResponse<List<ManagerOptionResponseDTO>>> getOptions(
                        @PathVariable Long optionGroupId,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                List<ManagerOptionResponseDTO> data = managerMenuService.getOptions(optionGroupId, managerId);

                ApiResponse<List<ManagerOptionResponseDTO>> response = ApiResponse
                                .<List<ManagerOptionResponseDTO>>builder()
                                .status(200)
                                .message("Thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @PostMapping("/option-groups/{optionGroupId}/options")
        public ResponseEntity<ApiResponse<ManagerOptionResponseDTO>> createOption(
                        @PathVariable Long optionGroupId,
                        @Valid @RequestBody ManagerOptionRequestDTO.CreateOrUpdateOption requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerOptionResponseDTO data = managerMenuService.createOption(optionGroupId, managerId, requestDTO);

                ApiResponse<ManagerOptionResponseDTO> response = ApiResponse.<ManagerOptionResponseDTO>builder()
                                .status(201)
                                .message("Thêm tùy chọn thành công")
                                .data(data)
                                .build();
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @PutMapping("/options/{optionId}")
        public ResponseEntity<ApiResponse<ManagerOptionResponseDTO>> updateOption(
                        @PathVariable Long optionId,
                        @Valid @RequestBody ManagerOptionRequestDTO.CreateOrUpdateOption requestDTO,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                ManagerOptionResponseDTO data = managerMenuService.updateOption(optionId, managerId, requestDTO);

                ApiResponse<ManagerOptionResponseDTO> response = ApiResponse.<ManagerOptionResponseDTO>builder()
                                .status(200)
                                .message("Cập nhật thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/options/{optionId}")
        public ResponseEntity<ApiResponse<Void>> deleteOption(
                        @PathVariable Long optionId,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                managerMenuService.deleteOption(optionId, managerId);

                ApiResponse<Void> response = ApiResponse.<Void>builder()
                                .status(200)
                                .message("Xóa thành công")
                                .data(null)
                                .build();
                return ResponseEntity.ok(response);
        }
}
