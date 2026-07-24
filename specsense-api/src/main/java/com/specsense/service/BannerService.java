package com.specsense.service;

import com.specsense.model.entity.Banner;
import java.util.List;

public interface BannerService {
    List<Banner> getEnabledBanners(String locale);
    List<Banner> getAllBanners();
    Banner getById(Long id);
    boolean save(Banner banner);
    boolean update(Banner banner);
    boolean deleteById(Long id);
}
