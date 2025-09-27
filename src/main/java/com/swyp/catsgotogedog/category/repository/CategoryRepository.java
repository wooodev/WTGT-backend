package com.swyp.catsgotogedog.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.catsgotogedog.category.domain.entity.CategoryCode;

public interface CategoryRepository extends JpaRepository<CategoryCode, String> {
}
