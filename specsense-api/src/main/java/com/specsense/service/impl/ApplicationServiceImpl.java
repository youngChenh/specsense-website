package com.specsense.service.impl;

import com.specsense.mapper.ApplicationMapper;
import com.specsense.model.entity.Application;
import com.specsense.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Override
    public List<Application> getAll(String locale) {
        List<Application> applications = applicationMapper.findAll();
        for (Application app : applications) {
            convertToLocale(app, locale);
        }
        return applications;
    }

    @Override
    public Application getById(Long id) {
        return applicationMapper.findById(id);
    }

    @Override
    public boolean save(Application application) {
        return applicationMapper.insert(application) > 0;
    }

    @Override
    public boolean update(Application application) {
        return applicationMapper.update(application) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return applicationMapper.deleteById(id) > 0;
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
