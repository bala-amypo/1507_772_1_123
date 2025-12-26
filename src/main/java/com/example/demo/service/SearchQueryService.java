package com.example.demo.service;

import com.example.demo.model.Employee;
import com.example.demo.model.SearchQueryRecord;

import java.util.List;

public interface SearchQueryService {

    SearchQueryRecord saveQuery(SearchQueryRecord record);

    List<Employee> searchEmployeesBySkills(List<String> skills, Long userId);

    SearchQueryRecord getQueryById(Long id);

    List<SearchQueryRecord> getQueriesForUser(Long userId);
}
