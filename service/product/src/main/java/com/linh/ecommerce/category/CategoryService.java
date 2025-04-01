package com.linh.ecommerce.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public CategoryResponse create(CategoryRequest request) {
        Category category = mapper.toCategory(request);
        Category savedCategory = repository.save(category);
        return mapper.toCategoryResponse(savedCategory);
    }

    public CategoryResponse getById(UUID id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return mapper.toCategoryResponse(category);
    }

    public List<CategoryResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toCategoryResponse)
                .toList();
    }

    public CategoryResponse update(UUID id, CategoryRequest request) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        category.setName(request.name());
        category.setDescription(request.description());
        
        Category updatedCategory = repository.save(category);
        return mapper.toCategoryResponse(updatedCategory);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
} 