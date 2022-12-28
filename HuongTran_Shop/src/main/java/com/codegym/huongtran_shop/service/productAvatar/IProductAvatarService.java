package com.codegym.huongtran_shop.service.productAvatar;

import com.codegym.huongtran_shop.model.ProductAvatar;
import com.codegym.huongtran_shop.service.IGeneralService;

public interface IProductAvatarService extends IGeneralService<ProductAvatar> {
    void delete(String id);
}
