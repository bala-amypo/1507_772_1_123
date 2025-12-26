package com.example.demo.controller;

import com.example.demo.dto.EmployeeSearchRequest;
import com.example.demo.model.Employee;
import com.example.demo.model.SearchQueryRecord;
import com.example.demo.service.SearchQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchQueryController {

    private final SearchQueryService searchQueryService;

    public SearchQueryController(SearchQueryService searchQueryService) {
        this.searchQueryService = searchQueryService;
    }

    @PostMapping("/employees")
    public List<Employee> searchEmployees(
            @RequestBody EmployeeSearchRequest request
    ) {
        return searchQueryService.searchEmployeesBySkills(
                request.getSkills(),
                1L
        );
    }

    @GetMapping("/{id}")
    public SearchQueryRecord getById(@PathVariable Long id) {
        return searchQueryService.getQueryById(id);
    }

    @GetMapping("/user/{userId}")
    public List<SearchQueryRecord> getByUser(@PathVariable Long userId) {
        return searchQueryService.getQueriesForUser(userId);
    }
}
