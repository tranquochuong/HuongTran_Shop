package com.codegym.huongtran_shop.service.staffAvatar;

import com.codegym.huongtran_shop.model.StaffAvatar;
import com.codegym.huongtran_shop.service.IGeneralService;

public interface IStaffAvatarService extends IGeneralService<StaffAvatar> {
    void delete(String id);
}
