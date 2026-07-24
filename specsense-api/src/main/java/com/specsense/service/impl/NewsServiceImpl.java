package com.specsense.service.impl;

import com.specsense.mapper.NewsMapper;
import com.specsense.model.entity.News;
import com.specsense.model.vo.PageResult;
import com.specsense.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Override
    public PageResult<List<News>> getList(String category, int page, int pageSize, String locale) {
        int offset = (page - 1) * pageSize;
        long total = newsMapper.count(category);
        List<News> newsList = newsMapper.findList(category, offset, pageSize);

        for (News news : newsList) {
            convertToLocale(news, locale);
        }

        return new PageResult<>(total, page, pageSize, newsList);
    }

    @Override
    public News getBySlug(String slug, String locale) {
        News news = newsMapper.findBySlug(slug);
        if (news != null) {
            convertToLocale(news, locale);
        }
        return news;
    }

    @Override
    public News getById(Long id) {
        return newsMapper.findById(id);
    }

    @Override
    public List<News> getLatest(int limit, String locale) {
        List<News> newsList = newsMapper.findLatest(limit);
        for (News news : newsList) {
            convertToLocale(news, locale);
        }
        return newsList;
    }

    @Override
    public boolean save(News news) {
        return newsMapper.insert(news) > 0;
    }

    @Override
    public boolean update(News news) {
        return newsMapper.update(news) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return newsMapper.deleteById(id) > 0;
    }

    private void convertToLocale(News news, String locale) {
        if ("zh".equals(locale)) {
            news.setTitle(news.getTitleZh());
            news.setExcerpt(news.getExcerptZh());
            news.setContent(news.getContentZh());
        } else {
            news.setTitle(news.getTitleEn());
            news.setExcerpt(news.getExcerptEn());
            news.setContent(news.getContentEn());
        }
    }
}
