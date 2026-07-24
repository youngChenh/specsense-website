package com.specsense.service;

import com.specsense.model.entity.NavigationMenu;
import java.util.List;

public interface NavigationMenuService {
    List<NavigationMenu> getByPosition(String position);
    List<NavigationMenu> getAll();
    NavigationMenu getById(Long id);
    boolean save(NavigationMenu menu);
    boolean update(NavigationMenu menu);
    boolean deleteById(Long id);
    List<NavigationMenu> buildTree(List<NavigationMenu> flatMenus);
}
