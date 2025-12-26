package com.example.demo.service.impl;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.SkillCategory;
import com.example.demo.repository.SkillCategoryRepository;
import com.example.demo.service.SkillCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service   // ✅ REQUIRED — FIXES BEAN ERROR
public class SkillCategoryServiceImpl implements SkillCategoryService {

    private final SkillCategoryRepository skillCategoryRepository;

    public SkillCategoryServiceImpl(SkillCategoryRepository skillCategoryRepository) {
        this.skillCategoryRepository = skillCategoryRepository;
    }

    @Override
    public SkillCategory createCategory(SkillCategory category) {
        category.setActive(true);
        return skillCategoryRepository.save(category);
    }

    @Override
    public SkillCategory updateCategory(Long id, SkillCategory category) {
        SkillCategory existing = skillCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SkillCategory not found"));

        existing.setCategoryName(category.getCategoryName());
        existing.setDescription(category.getDescription());

        return skillCategoryRepository.save(existing);
    }

    @Override
    public SkillCategory getCategoryById(Long id) {
        return skillCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SkillCategory not found"));
    }

    @Override
    public List<SkillCategory> getAllCategories() {
        return skillCategoryRepository.findAll();
    }

    @Override
    public void deactivateCategory(Long id) {
        SkillCategory category = skillCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SkillCategory not found"));

        category.setActive(false);
        skillCategoryRepository.save(category);
    }
}
