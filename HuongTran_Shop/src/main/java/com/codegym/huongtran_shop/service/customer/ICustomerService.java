package com.codegym.huongtran_shop.service.customer;

import com.codegym.huongtran_shop.model.Customer;
import com.codegym.huongtran_shop.model.LocationRegion;
import com.codegym.huongtran_shop.model.User;
import com.codegym.huongtran_shop.model.dto.customer.CustomerCreateDTO;
import com.codegym.huongtran_shop.model.dto.customer.CustomerDTO;
import com.codegym.huongtran_shop.service.IGeneralService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ICustomerService extends IGeneralService<Customer> {
    Customer saveWithLocationRegion(Customer customer);

    List<CustomerDTO> getAllCustomerDTO();

    Optional<CustomerDTO> getByEmailDTO(String email);

    Optional<Customer> findByPhone(String phone);

    void softDelete(Long customerId);

    Boolean existsByPhoneAndIdNot(String phone, Long id);

    Customer createCustomerWithAvatar(CustomerCreateDTO customerCreateDTO,
                                      LocationRegion locationRegion,
                                      User user);

    Customer saveWithAvatar(Customer customer, MultipartFile file);
}
