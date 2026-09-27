package com.sr.smart_civic_platform.category.repository;

import com.sr.smart_civic_platform.category.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    boolean existsByName(String name);

    // Case-insensitive-ish would need custom query; exact match is enough for now.
    Optional<Category> findByName(String name);

    List<Category> findByIsActiveTrue();
}