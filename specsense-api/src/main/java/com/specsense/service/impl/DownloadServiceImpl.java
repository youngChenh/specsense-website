package com.specsense.service.impl;

import com.specsense.mapper.DownloadMapper;
import com.specsense.model.entity.Download;
import com.specsense.service.CacheService;
import com.specsense.service.DownloadService;
import com.specsense.service.DownloadFileMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    private DownloadMapper downloadMapper;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private DownloadFileMetadata fileMetadata;

    @Override
    public List<Download> getAll(String category, String locale) {
        // Bypass older cache entries that did not include file metadata.
        String key = CacheService.keyDownloads("metadata-v1:" + locale + ":" + category);
        @SuppressWarnings("unchecked")
        List<Download> cached = cacheService.get(key, (Class<List<Download>>) (Class<?>) ArrayList.class);
        if (cached != null) {
            return cached;
        }

        List<Download> downloads = downloadMapper.findAll(category);
        for (Download download : downloads) {
            fileMetadata.fillMissingSize(download);
            convertToLocale(download, locale);
        }
        cacheService.set(key, downloads);
        return downloads;
    }

    @Override
    public Download getById(Long id) {
        Download download = downloadMapper.findById(id);
        fileMetadata.fillMissingSize(download);
        return download;
    }

    @Override
    public boolean save(Download download) {
        fileMetadata.fillMissingSize(download);
        boolean result = downloadMapper.insert(download) > 0;
        if (result) {
            cacheService.deleteByPattern("downloads:*");
        }
        return result;
    }

    @Override
    public boolean update(Download download) {
        fileMetadata.fillMissingSize(download);
        boolean result = downloadMapper.update(download) > 0;
        if (result) {
            cacheService.deleteByPattern("downloads:*");
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean result = downloadMapper.deleteById(id) > 0;
        if (result) {
            cacheService.deleteByPattern("downloads:*");
        }
        return result;
    }

    private void convertToLocale(Download download, String locale) {
        if ("zh".equals(locale)) {
            download.setName(download.getNameZh());
            download.setDescription(download.getDescriptionZh());
        } else {
            download.setName(download.getNameEn());
            download.setDescription(download.getDescriptionEn());
        }
    }
}
