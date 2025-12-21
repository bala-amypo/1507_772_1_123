package com.example.demo.repository;

import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeSkillRepository extends JpaRepository<EmployeeSkill, Long> {

    // Query to find employees who have all the given skills and match a specific userId
    @Query("""
        SELECT es.employee
        FROM EmployeeSkill es
        WHERE es.skill.name IN :skills
        AND es.employee.active = true
        AND (:userId IS NULL OR es.employee.id = :userId)
        GROUP BY es.employee
        HAVING COUNT(DISTINCT es.skill.name) = :#{#skills.size()}
    """)
    List<Employee> findEmployeesByAllSkillNames(
            @Param("skills") List<String> skills,
            @Param("userId") Long userId
    );

    // Find all active skills for a specific employee
    List<EmployeeSkill> findByEmployeeIdAndActiveTrue(Long employeeId);

    // Find all active employee skills for a specific skill
    List<EmployeeSkill> findBySkillIdAndActiveTrue(Long skillId);
}
