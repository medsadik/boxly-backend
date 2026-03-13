package org.example.boxlybackend.services;

import org.example.boxlybackend.dto.MenuOptionRequest;
import org.example.boxlybackend.dto.MenuOptionResponse;

import java.util.List;

public interface MenuOptionService {

    MenuOptionResponse createMenuOption(MenuOptionRequest request);

    MenuOptionResponse getMenuOptionById(Long id);

    List<MenuOptionResponse> getAllMenuOptions();

    MenuOptionResponse updateMenuOptions(Long id, MenuOptionRequest request);

    void deleteMenuOptions(Long id);
}