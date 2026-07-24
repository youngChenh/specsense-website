package com.specsense.service;

import com.specsense.model.entity.Application;
import java.util.List;

public interface ApplicationService {
    List<Application> getAll(String locale);
    Application getById(Long id);
    boolean save(Application application);
    boolean update(Application application);
    boolean deleteById(Long id);
}
