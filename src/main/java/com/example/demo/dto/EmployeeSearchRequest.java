package com.example.demo.dto;

import java.util.List;

public class EmployeeSearchRequest {

    private List<String> skills;

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }
}
