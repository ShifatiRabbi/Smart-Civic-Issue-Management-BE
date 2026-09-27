package com.sr.smart_civic_platform.category.service.impl;

import com.sr.smart_civic_platform.category.dto.request.CreateCategoryRequest;
import com.sr.smart_civic_platform.category.dto.request.UpdateCategoryRequest;
import com.sr.smart_civic_platform.category.dto.request.UpdateCategoryStatusRequest;
import com.sr.smart_civic_platform.category.dto.response.CategoryResponse;
import com.sr.smart_civic_platform.category.entity.Category;
import com.sr.smart_civic_platform.category.repository.CategoryRepository;
import com.sr.smart_civic_platform.category.service.CategoryService;
import com.sr.smart_civic_platform.common.exception.BusinessException;
import com.sr.smart_civic_platform.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BusinessException("Category with this name already exists", HttpStatus.CONFLICT);
        }

        Category category = new Category(request.getName(), request.getDescription(), true);
        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    /*
     * Purpose:
     * List categories. Default: active only (what citizens need for
     * submitting a complaint). includeInactive=true is meant for admin
     * management screens - caller (controller) is responsible for only
     * honoring this flag when the requester is an admin.
     */
    @Override
    public List<CategoryResponse> listCategories(boolean includeInactive) {
        List<Category> categories = includeInactive
                ? categoryRepository.findAll()
                : categoryRepository.findByIsActiveTrue();

        return categories.stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return CategoryResponse.fromEntity(category);
    }

    @Override
    public CategoryResponse updateCategory(String id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            // Renaming - must re-check uniqueness against the new name.
            if (categoryRepository.existsByName(request.getName())) {
                throw new BusinessException("Category with this name already exists", HttpStatus.CONFLICT);
            }
            category.setName(request.getName());
        }

        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }

        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse updateCategoryStatus(String id, UpdateCategoryStatusRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setActive(request.getIsActive());
        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }
}