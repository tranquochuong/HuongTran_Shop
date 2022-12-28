package com.codegym.huongtran_shop.service.customerAvatar;

import com.codegym.huongtran_shop.model.CustomerAvatar;
import com.codegym.huongtran_shop.service.IGeneralService;

public interface ICustomerAvatarService extends IGeneralService<CustomerAvatar> {
    void delete(String id);
}
