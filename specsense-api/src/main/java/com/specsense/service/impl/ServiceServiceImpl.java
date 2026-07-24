package com.specsense.service.impl;

import com.specsense.mapper.ServiceMapper;
import com.specsense.model.entity.ServiceEntity;
import com.specsense.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceMapper serviceMapper;

    @Override
    public List<ServiceEntity> getAll(String locale) {
        List<ServiceEntity> services = serviceMapper.findAll();
        for (ServiceEntity service : services) {
            convertToLocale(service, locale);
        }
        return services;
    }

    @Override
    public ServiceEntity getById(Long id) {
        return serviceMapper.findById(id);
    }

    @Override
    public boolean save(ServiceEntity service) {
        return serviceMapper.insert(service) > 0;
    }

    @Override
    public boolean update(ServiceEntity service) {
        return serviceMapper.update(service) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return serviceMapper.deleteById(id) > 0;
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
