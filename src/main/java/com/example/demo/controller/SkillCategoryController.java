package com.example.demo.controller;

import com.example.demo.model.SkillCategory;
import com.example.demo.service.SkillCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skill-categories")
public class SkillCategoryController {

    private final SkillCategoryService skillCategoryService;

    public SkillCategoryController(SkillCategoryService skillCategoryService) {
        this.skillCategoryService = skillCategoryService;
    }

    @PostMapping
    public SkillCategory create(@RequestBody SkillCategory category) {
        return skillCategoryService.createCategory(category);
    }

    @PutMapping("/{id}")
    public SkillCategory update(@PathVariable Long id, @RequestBody SkillCategory category) {
        return skillCategoryService.updateCategory(id, category);
    }

    @GetMapping("/{id}")
    public SkillCategory getById(@PathVariable Long id) {
        return skillCategoryService.getCategoryById(id);
    }

    @GetMapping
    public List<SkillCategory> getAll() {
        return skillCategoryService.getAllCategories();
    }

    @PutMapping("/{id}/deactivate")
    public void deactivate(@PathVariable Long id) {
        skillCategoryService.deactivateCategory(id);
    }
}
