package com.example.demo.controller;

import com.example.demo.model.Skill;
import com.example.demo.service.SkillService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService service;

    public SkillController(SkillService service) {
        this.service = service;
    }

    @PostMapping
    public Skill create(@RequestBody Skill skill) {
        return service.createSkill(skill);
    }

    @PutMapping("/{id}")
    public Skill update(@PathVariable Long id, @RequestBody Skill skill) {
        return service.updateSkill(id, skill);
    }

    @GetMapping("/{id}")
    public Skill get(@PathVariable Long id) {
        return service.getSkillById(id);
    }

    @GetMapping
    public List<Skill> getAll() {
        return service.getAllSkills();
    }

    @PutMapping("/{id}/deactivate")
    public void deactivate(@PathVariable Long id) {
        service.deactivateSkill(id);
    }
}
