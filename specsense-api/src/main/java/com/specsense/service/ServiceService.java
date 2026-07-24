package com.specsense.service;

import com.specsense.model.entity.ServiceEntity;
import java.util.List;

public interface ServiceService {
    List<com.specsense.model.entity.ServiceEntity> getAll(String locale);
    com.specsense.model.entity.ServiceEntity getById(Long id);
    boolean save(com.specsense.model.entity.ServiceEntity service);
    boolean update(com.specsense.model.entity.ServiceEntity service);
    boolean deleteById(Long id);
}
