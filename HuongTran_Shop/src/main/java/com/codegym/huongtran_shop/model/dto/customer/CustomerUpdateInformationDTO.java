package com.codegym.huongtran_shop.model.dto.customer;

import com.codegym.huongtran_shop.model.Customer;
import com.codegym.huongtran_shop.model.CustomerAvatar;
import com.codegym.huongtran_shop.model.LocationRegion;
import com.codegym.huongtran_shop.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerUpdateInformationDTO {
    private Long id;
    @NotEmpty(message = "Vui lòng nhập họ tên.")
    @Size(min = 5, max = 100, message = "Họ tên có độ dài nằm trong khoảng 5 - 100 ký tự.")
    private String fullName;
    @NotEmpty(message = "Vui lòng nhập số điện thoại.")
    private String phone;


    private String fileType;
    @Pattern(regexp = "^\\d+$", message = "ID Province phải là số.")
    @NotEmpty(message = "ID Province không được trống.")
    private String provinceId;
    @NotEmpty(message = "Tên Province không được trống.")
    private String provinceName;
    @Pattern(regexp = "^\\d+$", message = "ID District phải là số.")
    @NotEmpty(message = "ID District xã không được trống.")
    private String districtId;
    @NotEmpty(message = "Tên District xã không được trống.")
    private String districtName;
    @Pattern(regexp = "^\\d+$", message = "ID Ward phải là số.")
    @NotEmpty(message = "Ward không được trống.")
    private String wardId;
    @NotEmpty(message = "Ward không được trống.")
    private String wardName;

    @NotEmpty(message = "Vui lòng nhập địa chỉ")
    @Size(min = 5, max = 100, message = "Address có độ dài nằm trong khoảng 5 - 100 ký tự.")
    private String address;

    public LocationRegion toLocationRegion(){
        return new LocationRegion()
                .setId(id)
                .setProvinceId(provinceId)
                .setProvinceName(provinceName)
                .setDistrictId(districtId)
                .setDistrictName(districtName)
                .setWardId(wardId)
                .setWardName(wardName)
                .setAddress(address);
    }

    public Customer toCustomer(User user, LocationRegion locationRegion, CustomerAvatar customerAvatar){
        return new Customer()
                .setId(id)
                .setFullName(fullName)
                .setPhone(phone)
                .setLocationRegion(locationRegion)
                .setUser(user)
                .setCustomerAvatar(customerAvatar);
    }
}
