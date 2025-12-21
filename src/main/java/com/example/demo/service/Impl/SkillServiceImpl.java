package com.example.demo.service.impl;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Skill;
import com.example.demo.repository.SkillRepository;
import com.example.demo.service.SkillService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public Skill createSkill(Skill skill) {
        skillRepository.findByName(skill.getName())
                .ifPresent(s -> {
                    throw new IllegalArgumentException("Skill already exists");
                });
        skill.setActive(true);
        return skillRepository.save(skill);
    }

    @Override
    public Skill updateSkill(Long id, Skill skill) {
        Skill existing = getSkillById(id);
        existing.setCategory(skill.getCategory());
        existing.setDescription(skill.getDescription());
        return skillRepository.save(existing);
    }

    @Override
    public Skill getSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));
    }

    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findByActiveTrue();
    }

    @Override
    public void deactivateSkill(Long id) {
        Skill skill = getSkillById(id);
        skill.setActive(false);
        skillRepository.save(skill);
    }
}
