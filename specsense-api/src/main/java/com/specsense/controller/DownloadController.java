
package com.specsense.controller;

import com.specsense.model.vo.Result;
import com.specsense.model.entity.Download;
import com.specsense.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/downloads")
public class DownloadController {

    @Autowired
    private DownloadService downloadService;

    @GetMapping
    public Result<List<Download>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "en") String locale) {
        return Result.success(downloadService.getAll(category, locale));
    }
}
