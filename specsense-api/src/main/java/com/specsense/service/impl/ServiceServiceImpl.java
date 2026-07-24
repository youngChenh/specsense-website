package com.specsense.service.impl;

import com.specsense.mapper.ServiceMapper;
import com.specsense.model.entity.ServiceEntity;
import com.specsense.service.ServiceService;
import com.specsense.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    public List<ServiceEntity> getAll(String locale) {
        String key = CacheService.keyServices(locale);
        @SuppressWarnings("unchecked")
        List<ServiceEntity> cached = cacheService.get(key, (Class<List<ServiceEntity>>) (Class<?>) ArrayList.class);
        if (cached != null) {
            return cached;
        }
        List<ServiceEntity> services = serviceMapper.findAll();
        for (ServiceEntity service : services) {
            convertToLocale(service, locale);
        }
        cacheService.set(key, services);
        return services;
    }

    @Override
    public ServiceEntity getById(Long id) {
        return serviceMapper.findById(id);
    }

    @Override
    public boolean save(ServiceEntity service) {
        boolean result = serviceMapper.insert(service) > 0;
        if (result) {
            cacheService.deleteByPattern("services:*");
        }
        return result;
    }

    @Override
    public boolean update(ServiceEntity service) {
        boolean result = serviceMapper.update(service) > 0;
        if (result) {
            cacheService.deleteByPattern("services:*");
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean result = serviceMapper.deleteById(id) > 0;
        if (result) {
            cacheService.deleteByPattern("services:*");
        }
        return result;
    }

    private void convertToLocale(ServiceEntity service, String locale) {
        if ("zh".equals(locale)) {
            service.setTitle(service.getTitleZh());
            service.setDescription(service.getDescriptionZh());
        } else {
            service.setTitle(service.getTitleEn());
            service.setDescription(service.getDescriptionEn());
        }
    }
}
