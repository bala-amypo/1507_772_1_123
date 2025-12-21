package com.example.demo.controller;

import com.example.demo.dto.EmployeeSearchRequest;
import com.example.demo.model.Employee;
import com.example.demo.service.SearchQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchQueryController {

    private final SearchQueryService service;

    public SearchQueryController(SearchQueryService service) {
        this.service = service;
    }

    @PostMapping("/employees")
    public List<Employee> search(@RequestBody EmployeeSearchRequest request) {
        return service.searchEmployeesBySkills(request.getSkills(), 1L);
    }
}
