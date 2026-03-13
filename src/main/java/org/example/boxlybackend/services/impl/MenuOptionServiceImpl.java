package org.example.boxlybackend.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.dto.MenuOptionRequest;
import org.example.boxlybackend.dto.MenuOptionResponse;
import org.example.boxlybackend.entites.MenuOption;
import org.example.boxlybackend.mapper.MenuOptionMapper;
import org.example.boxlybackend.repository.MenuOptionRepository;
import org.example.boxlybackend.services.MenuOptionService;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuOptionServiceImpl implements MenuOptionService {

    private final MenuOptionRepository repository;
    private final MenuOptionMapper mapper;

    @Override
    public MenuOptionResponse createMenuOption(MenuOptionRequest request) {
        MenuOption entity = mapper.toEntity(request);
        MenuOption saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public MenuOptionResponse getMenuOptionById(Long id) {
        MenuOption entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MenuOption not found with id: " + id));
        return mapper.toResponse(entity);
    }

    @Override
    public List<MenuOptionResponse> getAllMenuOptions() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MenuOptionResponse updateMenuOptions(Long id, MenuOptionRequest request) {
        MenuOption entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MenuOption not found with id: " + id));

        mapper.updateEntityFromRequest(request, entity);
        MenuOption updated = repository.save(entity);

        return mapper.toResponse(updated);
    }

    @Override
    public void deleteMenuOptions(Long id) {
        MenuOption entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MenuOption not found with id: " + id));

        repository.delete(entity);
    }
}