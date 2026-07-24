package com.specsense.service;

import com.specsense.model.entity.Download;
import java.util.List;

public interface DownloadService {
    List<Download> getAll(String category, String locale);
    Download getById(Long id);
    boolean save(Download download);
    boolean update(Download download);
    boolean deleteById(Long id);
}
