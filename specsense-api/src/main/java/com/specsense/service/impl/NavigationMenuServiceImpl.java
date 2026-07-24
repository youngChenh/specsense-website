package com.specsense.service.impl;

import com.specsense.mapper.NavigationMenuMapper;
import com.specsense.model.entity.NavigationMenu;
import com.specsense.service.NavigationMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NavigationMenuServiceImpl implements NavigationMenuService {

    @Autowired
    private NavigationMenuMapper navigationMenuMapper;

    @Override
    public List<NavigationMenu> getByPosition(String position) {
        return navigationMenuMapper.findByPosition(position);
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
        return navigationMenuMapper.insert(menu) > 0;
    }

    @Override
    public boolean update(NavigationMenu menu) {
        return navigationMenuMapper.update(menu) > 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return navigationMenuMapper.deleteById(id) > 0;
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
