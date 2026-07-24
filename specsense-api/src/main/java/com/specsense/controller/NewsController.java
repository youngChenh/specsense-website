package com.specsense.controller;

import com.specsense.model.vo.Result;
import com.specsense.model.vo.PageResult;
import com.specsense.model.entity.News;
import com.specsense.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    public Result<PageResult<List<News>>> getList(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "en") String locale) {
        return Result.success(newsService.getList(category, page, pageSize, locale));
    }

    @GetMapping("/{slug}")
    public Result<News> getBySlug(@PathVariable String slug,
                                  @RequestParam(defaultValue = "en") String locale) {
        News news = newsService.getBySlug(slug, locale);
        if (news == null) {
            return Result.error(404, "News not found");
        }
        return Result.success(news);
    }

    @GetMapping("/latest")
    public Result<List<News>> getLatest(@RequestParam(defaultValue = "3") int limit,
                                        @RequestParam(defaultValue = "en") String locale) {
        return Result.success(newsService.getLatest(limit, locale));
    }
}
