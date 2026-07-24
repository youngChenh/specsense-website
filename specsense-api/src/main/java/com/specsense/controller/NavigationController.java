package com.specsense.controller;

import com.specsense.model.entity.NavigationMenu;
import com.specsense.model.vo.Result;
import com.specsense.service.NavigationMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NavigationController {

    @Autowired
    private NavigationMenuService navigationMenuService;

    @GetMapping("/navigation/{position}")
    public Result<List<NavigationMenu>> getNavigation(@PathVariable String position) {
        List<NavigationMenu> menus = navigationMenuService.getByPosition(position);
        return Result.success(menus);
    }

    @GetMapping("/navigation")
    public Result<List<NavigationMenu>> getAllNavigation() {
        List<NavigationMenu> menus = navigationMenuService.getAll();
        return Result.success(menus);
    }

    @GetMapping("/navigation/tree/{position}")
    public Result<List<NavigationMenu>> getNavigationTree(@PathVariable String position) {
        List<NavigationMenu> allMenus = navigationMenuService.getByPosition(position);
        // Build tree structure
        List<NavigationMenu> rootMenus = navigationMenuService.buildTree(allMenus);
        return Result.success(rootMenus);
    }
}
