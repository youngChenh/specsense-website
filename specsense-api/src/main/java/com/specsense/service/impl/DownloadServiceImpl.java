package com.specsense.service.impl;

import com.specsense.mapper.DownloadMapper;
import com.specsense.model.entity.Download;
import com.specsense.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    private DownloadMapper downloadMapper;

    @Override
    public List<Download> getAll(String category, String locale) {
        List<Download> downloads = downloadMapper.findAll(category);
        for (Download download : downloads) {
            convertToLocale(download, locale);
        }
        return downloads;
    }

    @Override
    public Download getById(Long id) {
        return downloadMapper.findById(id);
    }

    @Override
    public boolean save(Download download) {
        return downloadMapper.insert(download) > 0;
    }

    @Override
    public boolean update(Download download) {
        return downloadMapper.update(download) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return downloadMapper.deleteById(id) > 0;
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
