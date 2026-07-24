package com.specsense.service;

import com.specsense.model.entity.News;
import com.specsense.model.vo.PageResult;
import java.util.List;

public interface NewsService {
    PageResult<List<News>> getList(String category, int page, int pageSize, String locale);
    News getBySlug(String slug, String locale);
    News getById(Long id);
    List<News> getLatest(int limit, String locale);
    boolean save(News news);
    boolean update(News news);
    boolean deleteById(Long id);
}
