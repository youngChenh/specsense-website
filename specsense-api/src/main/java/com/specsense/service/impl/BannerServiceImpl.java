package com.specsense.service.impl;

import com.specsense.mapper.BannerMapper;
import com.specsense.model.entity.Banner;
import com.specsense.service.BannerService;
import com.specsense.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    public List<Banner> getEnabledBanners(String locale) {
        String key = CacheService.keyBanners(locale);
        @SuppressWarnings("unchecked")
        List<Banner> cached = cacheService.get(key, (Class<List<Banner>>) (Class<?>) ArrayList.class);
        if (cached != null) {
            return cached;
        }

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
        cacheService.set(key, banners);
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
        boolean result = bannerMapper.insert(banner) > 0;
        if (result) {
            cacheService.deleteByPattern("banners:*");
        }
        return result;
    }

    @Override
    public boolean update(Banner banner) {
        boolean result = bannerMapper.update(banner) > 0;
        if (result) {
            cacheService.deleteByPattern("banners:*");
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean result = bannerMapper.deleteById(id) > 0;
        if (result) {
            cacheService.deleteByPattern("banners:*");
        }
        return result;
    }
}
