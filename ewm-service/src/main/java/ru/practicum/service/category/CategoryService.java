package ru.practicum.service.category;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto get(Long catId);

    CategoryDto create(NewCategoryDto categoryDto);

    CategoryDto update(Long id, CategoryDto categoryDto);

    void delete(Long catId);
}
