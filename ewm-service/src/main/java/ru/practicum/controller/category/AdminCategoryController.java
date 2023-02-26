package ru.practicum.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.service.category.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto create(@Valid @RequestBody NewCategoryDto categoryDto) {
        log.debug("Get request for adding new category.");
        return categoryService.create(categoryDto);
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@PathVariable Long catId, @Valid @RequestBody CategoryDto categoryDto) {
        log.debug("Get request for updating new category.");
        return categoryService.update(catId, categoryDto);
    }

    @DeleteMapping("/{catId}")
    public void delete(@PathVariable Long catId) {
        log.debug("Get request for deleting category with id={}", catId);
        categoryService.delete(catId);
    }
}
