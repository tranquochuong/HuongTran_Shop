package com.codegym.huongtran_shop.controller.api;

import com.codegym.huongtran_shop.exception.DataInputException;
import com.codegym.huongtran_shop.exception.EmailExistsException;
import com.codegym.huongtran_shop.model.LocationRegion;
import com.codegym.huongtran_shop.model.Role;
import com.codegym.huongtran_shop.model.Staff;
import com.codegym.huongtran_shop.model.User;
import com.codegym.huongtran_shop.service.role.IRoleService;
import com.codegym.huongtran_shop.service.staff.IStaffService;
import com.codegym.huongtran_shop.service.user.IUserService;
import com.codegym.huongtran_shop.utils.AppUtils;
import com.codegym.huongtran_shop.model.dto.staff.StaffCreateDTO;
import com.codegym.huongtran_shop.model.dto.staff.StaffDTO;
import com.codegym.huongtran_shop.model.dto.staff.StaffUpdateDTO;
import com.codegym.huongtran_shop.model.dto.staff.StaffUpdateInformationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/staffs")
public class StaffAPI {
    private static final String DEFAULT_PASSWORD = "123456";
    @Autowired
    private IStaffService staffService;

    @Autowired
    private AppUtils appUtils;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRoleService roleService;

    @GetMapping
    public ResponseEntity<?> getAllByDeletedIsFalse() {
        List<StaffDTO> staffDTOS = staffService.getAllStaffDTO();
        if (staffDTOS.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(staffDTOS, HttpStatus.OK);
    }

    @GetMapping("/get-all-staffs-where-id-not/{staffId}")
    public ResponseEntity<?> getAllByDeletedIsFalseAndIdNot(@PathVariable String staffId) {
        Long sid = Long.parseLong(staffId);
        List<StaffDTO> staffDTOS = staffService.getAllStaffDTOWhereIdNot(sid);
        if (staffDTOS.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(staffDTOS, HttpStatus.OK);
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<?> getById(@PathVariable String staffId) {
        long sid;
        try {
            sid = Long.parseLong(staffId);
        } catch (NumberFormatException e) {
            throw new DataInputException("ID nh??n vi??n kh??ng h???p l???.");
        }

        Optional<Staff> staffOptional = staffService.findById(sid);

        if (!staffOptional.isPresent()) {
            throw new DataInputException("ID nh??n vi??n kh??ng h???p l???.");
        }

        return new ResponseEntity<>(staffOptional.get().toStaffDTO(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> create(@Validated StaffCreateDTO staffCreateDTO, BindingResult bindingResult) {

        MultipartFile imageFile = staffCreateDTO.getFile();

        if (imageFile == null) {
            throw new DataInputException("Vui l??ng ch???n h??nh ???nh.");
        }

        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        Optional<User> userOptional = userService.findByUserName(staffCreateDTO.getUsername());

        if (userOptional.isPresent()) {
            throw new EmailExistsException("Email ???? t???n t???i trong h??? th???ng.");
        }

        Optional<Staff> staffOptional = staffService.findByPhone(staffCreateDTO.getPhone());

        if (staffOptional.isPresent()) {
            throw new DataInputException("S??? ??i???n tho???i ???? t???n t???i trong h??? th???ng.");
        }

        LocationRegion locationRegion = staffCreateDTO.toLocationRegion();
        locationRegion.setId(null);

        Optional<Role> optRole = roleService.findById(Long.parseLong(staffCreateDTO.getRoleId()));

        if (!optRole.isPresent()) {
            throw new DataInputException("Role kh??ng h???p l???");
        }

        Role role = optRole.get();

            User user = staffCreateDTO.toUser(role);
        user.setId(null);
        user.setPassword(DEFAULT_PASSWORD);

        Staff newStaff = staffService.createStaffWithAvatar(staffCreateDTO, locationRegion, user);

        return new ResponseEntity<>(newStaff.toStaffDTO(), HttpStatus.CREATED);
    }

    @PatchMapping("/{staffId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long staffId, MultipartFile file, @Validated StaffUpdateDTO staffUpdateDTO, BindingResult bindingResult) {
        Optional<Staff> staffOptional = staffService.findById(staffId);
        if (!staffOptional.isPresent()) {
            throw new DataInputException("ID nh??n vi??n kh??ng t???n t???i.");
        }
        Staff staff = staffOptional.get();

        String phone = staffUpdateDTO.getPhone();

        if (staffService.existsByPhoneAndIdNot(phone, staffId)) {
            throw new DataInputException("S??? ??i???n tho???i ???? t???n t???i trong h??? th???ng.");
        }
        long roleId;
        try {
            roleId = Long.parseLong(staffUpdateDTO.getRoleId());
        } catch (Exception e) {
            throw new DataInputException("Role kh??ng h???p l???.");
        }
        Optional<Role> roleOptional = roleService.findById(roleId);

        if (!roleOptional.isPresent()) {
            throw new DataInputException("Role kh??ng t???n t???i.");
        }

        Role role = roleOptional.get();

        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        LocationRegion newLocationRegion = staffUpdateDTO.toLocationRegion();

        staff.setFullName(staffUpdateDTO.getFullName());
        staff.setPhone(phone);
        staff.getUser().setRole(role);
        staff.getLocationRegion()
                .setProvinceId(newLocationRegion.getProvinceId())
                .setProvinceName(newLocationRegion.getProvinceName())
                .setDistrictId(newLocationRegion.getDistrictId())
                .setDistrictName(newLocationRegion.getDistrictName())
                .setWardId(newLocationRegion.getWardId())
                .setWardName(newLocationRegion.getWardName())
                .setAddress(newLocationRegion.getAddress());
        staff = staffService.saveWithUserRoleAndLocationRegion(staff);
        if(file != null){
            staff = staffService.saveWithAvatar(staff, file);
        }
        return new ResponseEntity<>(staff.toStaffDTO(), HttpStatus.OK);
    }

    @PatchMapping("/update-information/{staffId}")
    public ResponseEntity<?> update(@PathVariable Long staffId, MultipartFile file, @Validated StaffUpdateInformationDTO staffUpdateInformationDTO, BindingResult bindingResult) {

        Optional<Staff> staffOptional = staffService.findById(staffId);
        if (!staffOptional.isPresent()) {
            throw new DataInputException("ID nh??n vi??n kh??ng t???n t???i.");
        }

        String phone = staffUpdateInformationDTO.getPhone();

        if (staffService.existsByPhoneAndIdNot(phone, staffId)) {
            throw new DataInputException("S??? ??i???n tho???i ???? t???n t???i trong h??? th???ng.");
        }

        Staff staff = staffOptional.get();

        LocationRegion locationRegion = staffUpdateInformationDTO.toLocationRegion();
        locationRegion.setId(staff.getLocationRegion().getId());

        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }

        staff.setFullName(staffUpdateInformationDTO.getFullName());
        staff.setPhone(phone);
        staff.setLocationRegion(locationRegion);
        staff = staffService.saveWithLocationRegion(staff);
        if (file != null) {
            staff = staffService.saveWithAvatar(staff, file);
        }

        return new ResponseEntity<>(staff.toStaffDTO(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{staffId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long staffId) {

        Optional<Staff> staffOptional = staffService.findById(staffId);

        if (!staffOptional.isPresent()) {
            throw new DataInputException("ID nh??n vi??n kh??ng h???p l???.");
        }

        Staff staff = staffOptional.get();

        if (staff.getUser().getRole().getCode().equals("ADMIN")) {
            throw new DataInputException("Kh??ng th??? x??a nh??n vi??n l?? ADMIN.");
        }

        try {
            staffService.softDelete(staffId);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            throw new DataInputException("Vui l??ng li??n h??? Administrator.");
        }
    }
}