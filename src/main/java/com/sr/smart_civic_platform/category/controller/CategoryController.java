package com.sr.smart_civic_platform.category.controller;

import com.sr.smart_civic_platform.category.dto.request.CreateCategoryRequest;
import com.sr.smart_civic_platform.category.dto.request.UpdateCategoryRequest;
import com.sr.smart_civic_platform.category.dto.request.UpdateCategoryStatusRequest;
import com.sr.smart_civic_platform.category.dto.response.CategoryResponse;
import com.sr.smart_civic_platform.category.service.CategoryService;
import com.sr.smart_civic_platform.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {

        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", response));
    }

    /*
     * Purpose:
     * Any authenticated user can list categories (citizens need this to
     * submit a complaint). includeInactive is only honored for admins -
     * non-admin requests silently get active-only results regardless of
     * what they pass, rather than an error (no need to reveal the flag
     * exists to non-admins).
     */
    @GetMapping
    public ApiResponse<List<CategoryResponse>> listCategories(
            @RequestParam(defaultValue = "false") boolean includeInactive,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        boolean effectiveIncludeInactive = includeInactive && isAdmin;

        return ApiResponse.success("Categories fetched successfully",
                categoryService.listCategories(effectiveIncludeInactive));
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable String id) {
        return ApiResponse.success("Category fetched successfully",
                categoryService.getCategoryById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody UpdateCategoryRequest request) {

        return ApiResponse.success("Category updated successfully",
                categoryService.updateCategory(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ApiResponse<CategoryResponse> updateCategoryStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateCategoryStatusRequest request) {

        return ApiResponse.success("Category status updated successfully",
                categoryService.updateCategoryStatus(id, request));
    }
}