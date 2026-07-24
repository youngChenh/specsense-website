package com.specsense.service.impl;

import com.specsense.mapper.ApplicationMapper;
import com.specsense.model.entity.Application;
import com.specsense.service.ApplicationService;
import com.specsense.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    public List<Application> getAll(String locale) {
        String key = CacheService.keyApplications(locale);
        @SuppressWarnings("unchecked")
        List<Application> cached = cacheService.get(key, (Class<List<Application>>) (Class<?>) ArrayList.class);
        if (cached != null) {
            return cached;
        }

        List<Application> applications = applicationMapper.findAll();
        for (Application app : applications) {
            convertToLocale(app, locale);
        }
        cacheService.set(key, applications);
        return applications;
    }

    @Override
    public Application getById(Long id) {
        return applicationMapper.findById(id);
    }

    @Override
    public boolean save(Application application) {
        boolean result = applicationMapper.insert(application) > 0;
        if (result) {
            cacheService.deleteByPattern("applications:*");
        }
        return result;
    }

    @Override
    public boolean update(Application application) {
        boolean result = applicationMapper.update(application) > 0;
        if (result) {
            cacheService.deleteByPattern("applications:*");
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean result = applicationMapper.deleteById(id) > 0;
        if (result) {
            cacheService.deleteByPattern("applications:*");
        }
        return result;
    }

    private void convertToLocale(Application app, String locale) {
        if ("zh".equals(locale)) {
            app.setTitle(app.getTitleZh());
            app.setDescription(app.getDescriptionZh());
        } else {
            app.setTitle(app.getTitleEn());
            app.setDescription(app.getDescriptionEn());
        }
    }
}
