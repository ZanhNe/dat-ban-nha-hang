package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant.DepositType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class RegisterRestaurantRequestDTO {
    @NotBlank(message = "Tên nhà hàng không được để trống")
    private String name;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotNull(message = "Vĩ độ (latitude) không được để trống")
    private Double latitude;

    @NotNull(message = "Kinh độ (longitude) không được để trống")
    private Double longitude;

    @NotNull(message = "Mức cọc cơ bản không được để trống")
    private Long baseDepositValue;

    @NotNull(message = "Chính sách cọc không được để trống")
    private DepositType depositPolicy;

    @NotNull(message = "Danh mục ẩm thực không được để trống")
    private List<Long> cuisineIds;

    @NotNull(message = "Logo nhà hàng không được để trống")
    private MultipartFile logo;

    @NotNull(message = "Giấy tờ pháp lý không được để trống")
    private List<MultipartFile> legalDocs;
}
