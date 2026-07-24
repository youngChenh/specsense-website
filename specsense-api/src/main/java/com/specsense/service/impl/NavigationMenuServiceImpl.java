package com.specsense.service.impl;

import com.specsense.mapper.NavigationMenuMapper;
import com.specsense.model.entity.NavigationMenu;
import com.specsense.service.NavigationMenuService;
import com.specsense.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NavigationMenuServiceImpl implements NavigationMenuService {

    @Autowired
    private NavigationMenuMapper navigationMenuMapper;

    @Autowired
    private CacheService cacheService;

    @Override
    public List<NavigationMenu> getByPosition(String position) {
        String key = CacheService.keyNavigation(position);
        @SuppressWarnings("unchecked")
        List<NavigationMenu> cached = cacheService.get(key, (Class<List<NavigationMenu>>) (Class<?>) ArrayList.class);
        if (cached != null) {
            return cached;
        }
        List<NavigationMenu> menus = navigationMenuMapper.findByPosition(position);
        cacheService.set(key, menus);
        return menus;
    }

    @Override
    public List<NavigationMenu> getAll() {
        return navigationMenuMapper.findAll();
    }

    @Override
    public NavigationMenu getById(Long id) {
        return navigationMenuMapper.findById(id);
    }

    @Override
    public boolean save(NavigationMenu menu) {
        boolean result = navigationMenuMapper.insert(menu) > 0;
        if (result) {
            cacheService.deleteByPattern("navigation:*");
        }
        return result;
    }

    @Override
    public boolean update(NavigationMenu menu) {
        boolean result = navigationMenuMapper.update(menu) > 0;
        if (result) {
            cacheService.deleteByPattern("navigation:*");
        }
        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        boolean result = navigationMenuMapper.deleteById(id) > 0;
        if (result) {
            cacheService.deleteByPattern("navigation:*");
        }
        return result;
    }

    @Override
    public List<NavigationMenu> buildTree(List<NavigationMenu> flatMenus) {
        List<NavigationMenu> rootMenus = new ArrayList<>();

        for (NavigationMenu menu : flatMenus) {
            if (menu.getParentId() == null) {
                // This is a root menu
                List<NavigationMenu> children = new ArrayList<>();
                for (NavigationMenu child : flatMenus) {
                    if (child.getParentId() != null && child.getParentId().equals(menu.getId())) {
                        children.add(child);
                    }
                }
                menu.setChildren(children);
                rootMenus.add(menu);
            }
        }

        return rootMenus;
    }
}
