package com.codegym.huongtran_shop.service.staff;

import com.codegym.huongtran_shop.model.LocationRegion;
import com.codegym.huongtran_shop.model.Staff;
import com.codegym.huongtran_shop.model.User;
import com.codegym.huongtran_shop.model.dto.staff.StaffCreateDTO;
import com.codegym.huongtran_shop.model.dto.staff.StaffDTO;
import com.codegym.huongtran_shop.service.IGeneralService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface IStaffService  extends IGeneralService<Staff> {
    List<StaffDTO> getAllStaffDTO();
    List<Staff> findAllByIdNot(long id);

    Staff saveWithUserRoleAndLocationRegion(Staff staff);

    Staff saveWithLocationRegion(Staff staff);

    Optional<StaffDTO> getByEmailDTO(String email);
    Optional<Staff> findByPhone(String phone);

    Boolean existsByPhoneAndIdNot(String phone, Long id);

    void softDelete(Long staffId);

    Staff createStaffWithAvatar(StaffCreateDTO staffCreateDTO,
                                LocationRegion locationRegion,
                                User user);

    Staff saveWithAvatar(Staff staff, MultipartFile file);

    List<StaffDTO> getAllStaffDTOWhereIdNot(Long staffId);
}
