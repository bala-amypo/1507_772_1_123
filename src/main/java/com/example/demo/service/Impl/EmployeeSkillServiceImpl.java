package com.example.demo.service.impl;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeSkill;
import com.example.demo.model.Skill;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.EmployeeSkillRepository;
import com.example.demo.repository.SkillRepository;
import com.example.demo.service.EmployeeSkillService;
import com.example.demo.util.ProficiencyValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeSkillServiceImpl implements EmployeeSkillService {

    private final EmployeeSkillRepository employeeSkillRepository;
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;

    public EmployeeSkillServiceImpl(EmployeeSkillRepository employeeSkillRepository,
                                    EmployeeRepository employeeRepository,
                                    SkillRepository skillRepository) {
        this.employeeSkillRepository = employeeSkillRepository;
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public EmployeeSkill createEmployeeSkill(EmployeeSkill mapping) {

        if (mapping.getYearsOfExperience() < 0) {
            throw new IllegalArgumentException("Experience years");
        }

        if (!ProficiencyValidator.isValid(mapping.getProficiencyLevel())) {
            throw new IllegalArgumentException("Invalid proficiency");
        }

        Employee emp = employeeRepository.findById(mapping.getEmployee().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Skill skill = skillRepository.findById(mapping.getSkill().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        if (!emp.getActive()) throw new IllegalArgumentException("inactive employee");
        if (!skill.getActive()) throw new IllegalArgumentException("inactive skill");

        mapping.setEmployee(emp);
        mapping.setSkill(skill);

        return employeeSkillRepository.save(mapping);
    }

    @Override
    public EmployeeSkill updateEmployeeSkill(Long id, EmployeeSkill mapping) {
        EmployeeSkill existing = employeeSkillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeSkill not found"));

        mapping.setId(existing.getId());
        return createEmployeeSkill(mapping);
    }

    @Override
    public List<EmployeeSkill> getSkillsForEmployee(Long employeeId) {
        return employeeSkillRepository.findByEmployeeIdAndActiveTrue(employeeId);
    }

    @Override
    public List<EmployeeSkill> getEmployeesBySkill(Long skillId) {
        return employeeSkillRepository.findBySkillIdAndActiveTrue(skillId);
    }

    @Override
    public void deactivateEmployeeSkill(Long id) {
        EmployeeSkill es = employeeSkillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeSkill not found"));
        es.setActive(false);
        employeeSkillRepository.save(es);
    }
}
