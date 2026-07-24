package com.specsense.service.impl;

import com.specsense.mapper.BannerMapper;
import com.specsense.model.entity.Banner;
import com.specsense.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    @Override
    public List<Banner> getEnabledBanners(String locale) {
        List<Banner> banners = bannerMapper.findEnabledByLocale(locale);
        // 如果没有对应语言的数据，回退到中文
        if (banners.isEmpty() && !"zh".equals(locale)) {
            banners = bannerMapper.findEnabledByLocale("zh");
        }
        for (Banner banner : banners) {
            if ("zh".equals(locale)) {
                banner.setTitle(banner.getTitleZh());
                banner.setSubtitle(banner.getSubtitleZh());
            } else {
                // 优先使用英文，没有则回退到中文
                banner.setTitle(banner.getTitleEn() != null && !banner.getTitleEn().isEmpty() ? banner.getTitleEn() : banner.getTitleZh());
                banner.setSubtitle(banner.getSubtitleEn() != null && !banner.getSubtitleEn().isEmpty() ? banner.getSubtitleEn() : banner.getSubtitleZh());
            }
        }
        return banners;
    }

    @Override
    public List<Banner> getAllBanners() {
        return bannerMapper.findAll();
    }

    @Override
    public Banner getById(Long id) {
        return bannerMapper.findById(id);
    }

    @Override
    public boolean save(Banner banner) {
        return bannerMapper.insert(banner) > 0;
    }

    @Override
    public boolean update(Banner banner) {
        return bannerMapper.update(banner) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return bannerMapper.deleteById(id) > 0;
    }
}
