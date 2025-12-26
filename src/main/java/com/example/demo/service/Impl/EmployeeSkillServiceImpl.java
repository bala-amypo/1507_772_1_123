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

@Service   // ✅ FIX
public class EmployeeSkillServiceImpl implements EmployeeSkillService {

    private final EmployeeSkillRepository employeeSkillRepository;
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;

    public EmployeeSkillServiceImpl(
            EmployeeSkillRepository employeeSkillRepository,
            EmployeeRepository employeeRepository,
            SkillRepository skillRepository
    ) {
        this.employeeSkillRepository = employeeSkillRepository;
        this.employeeRepository = employeeRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public EmployeeSkill createEmployeeSkill(EmployeeSkill employeeSkill) {

        Employee emp = employeeRepository.findById(employeeSkill.getEmployee().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        Skill skill = skillRepository.findById(employeeSkill.getSkill().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        if (!emp.getActive()) {
            throw new IllegalArgumentException("inactive employee");
        }
        if (!skill.getActive()) {
            throw new IllegalArgumentException("inactive skill");
        }
        if (employeeSkill.getYearsOfExperience() < 0) {
            throw new IllegalArgumentException("Experience years");
        }
        if (!ProficiencyValidator.isValid(employeeSkill.getProficiencyLevel())) {
            throw new IllegalArgumentException("Invalid proficiency");
        }

        employeeSkill.setActive(true);
        return employeeSkillRepository.save(employeeSkill);
    }

    @Override
    public EmployeeSkill updateEmployeeSkill(Long id, EmployeeSkill employeeSkill) {
        EmployeeSkill existing = employeeSkillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeSkill not found"));

        existing.setProficiencyLevel(employeeSkill.getProficiencyLevel());
        existing.setYearsOfExperience(employeeSkill.getYearsOfExperience());

        return employeeSkillRepository.save(existing);
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
