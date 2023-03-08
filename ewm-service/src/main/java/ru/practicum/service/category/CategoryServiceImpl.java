package ru.practicum.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.converter.CategoryConverter;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.CategoryIsNotEmptyException;
import ru.practicum.exception.InvalidIdException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        log.info("Getting all categories from repository");

        return categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(CategoryConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto get(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    throw new InvalidIdException("Category", catId, LocalDateTime.now());
                });
        log.info("Getting category with id={} from repository.", category.getId());

        return CategoryConverter.toDto(category);
    }

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto categoryDto) {
        Category category = CategoryConverter.fromDto(categoryDto);

        category.setName(categoryDto.getName());

        Category savedCategory = categoryRepository.save(category);
        log.info("New category with id={} added successfully.", savedCategory.getId());

        return CategoryConverter.toDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    throw new BadRequestException("Category", id, LocalDateTime.now());
                });

        category.setName(categoryDto.getName());
        log.info("Category with id={} updated successfully.", category.getId());

        return CategoryConverter.toDto(category);
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        if (!eventRepository.findAllByCategory_Id(catId).isEmpty()) {
            throw new CategoryIsNotEmptyException(LocalDateTime.now());
        }
        categoryRepository.deleteById(catId);
        log.info("Category with if={} deleted successfully.", catId);
    }
}
