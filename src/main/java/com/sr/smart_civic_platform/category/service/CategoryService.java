package com.sr.smart_civic_platform.category.service;

import com.sr.smart_civic_platform.category.dto.request.CreateCategoryRequest;
import com.sr.smart_civic_platform.category.dto.request.UpdateCategoryRequest;
import com.sr.smart_civic_platform.category.dto.request.UpdateCategoryStatusRequest;
import com.sr.smart_civic_platform.category.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CreateCategoryRequest request);

    List<CategoryResponse> listCategories(boolean includeInactive);

    CategoryResponse getCategoryById(String id);

    CategoryResponse updateCategory(String id, UpdateCategoryRequest request);

    CategoryResponse updateCategoryStatus(String id, UpdateCategoryStatusRequest request);
}