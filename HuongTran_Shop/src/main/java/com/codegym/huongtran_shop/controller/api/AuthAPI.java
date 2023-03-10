package com.codegym.huongtran_shop.controller.api;

import com.codegym.huongtran_shop.exception.DataInputException;
import com.codegym.huongtran_shop.exception.EmailExistsException;
import com.codegym.huongtran_shop.model.*;
import com.codegym.huongtran_shop.service.customer.ICustomerService;
import com.codegym.huongtran_shop.service.jwt.JwtService;
import com.codegym.huongtran_shop.service.role.IRoleService;
import com.codegym.huongtran_shop.service.user.IUserService;
import com.codegym.huongtran_shop.utils.AppUtils;
import com.codegym.huongtran_shop.model.dto.customer.CustomerCreateDTO;
import com.codegym.huongtran_shop.model.dto.user.UserLoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthAPI {
    private final static Long CUSTOMER_ROLE_ID = 3L;
    @Autowired
    private AppUtils appUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICustomerService customerService;

    @Autowired
    private IRoleService roleService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return appUtils.mapErrorToResponse(bindingResult);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtService.generateTokenLogin(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User currentUser = userService.getByUsername(userLoginDTO.getUsername());

            JwtResponse jwtResponse = new JwtResponse(
                    jwt,
                    currentUser.getId(),
                    userDetails.getUsername(),
                    currentUser.getUsername(),
                    userDetails.getAuthorities()
            );

            ResponseCookie springCookie = ResponseCookie.from("JWT", jwt)
                    .httpOnly(false)
                    .secure(false)
                    .path("/")
                    .maxAge(60 * 1000)
                    .domain("localhost")
                    .build();

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                    .body(jwtResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated CustomerCreateDTO customerCreateDTO, BindingResult bindingResult) {

        MultipartFile imageFile = customerCreateDTO.getFile();
        if (imageFile == null) {
            throw new DataInputException("Vui l??ng ch???n h??nh ???nh.");
        }

        if (bindingResult.hasFieldErrors()) {
            return appUtils.mapErrorToResponse(bindingResult);
        }
        Optional<User> userOptional = userService.findByUserName(customerCreateDTO.getUsername());

        if (userOptional.isPresent()) {
            throw new EmailExistsException("Email ???? t???n t???i trong h??? th???ng.");
        }
        Optional<Customer> customerOptional = customerService.findByPhone(customerCreateDTO.getPhone());

        if (customerOptional.isPresent()) {
            throw new DataInputException("S??? ??i???n tho???i ???? t???n t???i trong h??? th???ng.");
        }

        LocationRegion locationRegion = customerCreateDTO.toLocationRegion();
        locationRegion.setId(null);

        Optional<Role> roleOptional = roleService.findById(CUSTOMER_ROLE_ID);
        if (!roleOptional.isPresent()) {
            throw new DataInputException("Role kh??ng h???p l???");
        }
        Role role = roleOptional.get();

        User user = customerCreateDTO.toUser(role);
        user.setId(null);
        user.setPassword(customerCreateDTO.getPassword());

        Customer customer = customerService.createCustomerWithAvatar(customerCreateDTO, locationRegion, user);

        return new ResponseEntity<>(customer.toCustomerDTO(), HttpStatus.CREATED);
    }
}
