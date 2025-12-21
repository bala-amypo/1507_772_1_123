package com.example.demo.service.impl;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.SkillCategory;
import com.example.demo.repository.SkillCategoryRepository;
import com.example.demo.service.SkillCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillCategoryServiceImpl implements SkillCategoryService {

    private final SkillCategoryRepository repository;

    public SkillCategoryServiceImpl(SkillCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public SkillCategory createCategory(SkillCategory category) {
        repository.findByCategoryName(category.getCategoryName())
                .ifPresent(c -> {
                    throw new IllegalArgumentException("Category already exists");
                });
        return repository.save(category);
    }

    @Override
    public SkillCategory updateCategory(Long id, SkillCategory category) {
        SkillCategory existing = getCategoryById(id);
        existing.setDescription(category.getDescription());
        return repository.save(existing);
    }

    @Override
    public SkillCategory getCategoryById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SkillCategory not found"));
    }

    @Override
    public List<SkillCategory> getAllCategories() {
        return repository.findAll();
    }

    @Override
    public void deactivateCategory(Long id) {
        SkillCategory category = getCategoryById(id);
        category.setActive(false);
        repository.save(category);
    }
}
