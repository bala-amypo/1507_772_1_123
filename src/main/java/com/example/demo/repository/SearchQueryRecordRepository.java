package com.example.demo.repository;

import com.example.demo.model.SearchQueryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchQueryRecordRepository
        extends JpaRepository<SearchQueryRecord, Long> {

    List<SearchQueryRecord> findBySearcherId(Long searcherId);
}
