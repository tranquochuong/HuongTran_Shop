package com.codegym.huongtran_shop.service.customerAvatar;

import com.codegym.huongtran_shop.model.CustomerAvatar;
import com.codegym.huongtran_shop.repository.CustomerAvatarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerAvatarImpl implements ICustomerAvatarService{

    @Autowired
    private CustomerAvatarRepository customerAvatarRepository;

    @Override
    public List<CustomerAvatar> findAll() {
        return null;
    }

    @Override
    public CustomerAvatar getById(Long id) {
        return null;
    }

    @Override
    public Optional<CustomerAvatar> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public CustomerAvatar save(CustomerAvatar customerAvatar) {
        return customerAvatarRepository.save(customerAvatar);
    }

    @Override
    public void remove(Long id) {

    }

    @Override
    public void delete(String id) {
        customerAvatarRepository.deleteById(id);
    }
}
