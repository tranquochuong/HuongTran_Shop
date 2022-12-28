package com.codegym.huongtran_shop.repository;

import com.codegym.huongtran_shop.model.CustomerAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerAvatarRepository extends JpaRepository<CustomerAvatar, String> {
}
