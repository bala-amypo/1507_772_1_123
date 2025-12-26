package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "employee_skills")
public class EmployeeSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Many skills can belong to one employee
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // 🔗 Many employees can have one skill
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    // ✅ Only allowed values
    @NotBlank
    @Pattern(
        regexp = "Beginner|Intermediate|Advanced|Expert",
        message = "Proficiency level must be Beginner, Intermediate, Advanced, or Expert"
    )
    private String proficiencyLevel;

    // ✅ Must be ≥ 0
    @Min(0)
    private Integer yearsOfExperience;

    // ✅ Default value = true
    private Boolean active = true;

    // 🔹 Default constructor (REQUIRED by JPA)
    public EmployeeSkill() {
    }

    // 🔹 Parameterized constructor
    public EmployeeSkill(Employee employee, Skill skill, String proficiencyLevel,
                         Integer yearsOfExperience, Boolean active) {
        this.employee = employee;
        this.skill = skill;
        this.proficiencyLevel = proficiencyLevel;
        this.yearsOfExperience = yearsOfExperience;
        this.active = active;
    }

    // =======================
    // GETTERS AND SETTERS
    // =======================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
