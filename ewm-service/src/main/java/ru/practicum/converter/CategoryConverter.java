package ru.practicum.converter;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.model.Category;

@UtilityClass
public class CategoryConverter {

    public static CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public Category fromDto(NewCategoryDto newCategoryDto) {
        return new Category(null, newCategoryDto.getName());
    }
}
