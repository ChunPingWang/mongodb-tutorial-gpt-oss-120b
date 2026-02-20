package com.course.mongodb.m07.repository;

import com.course.mongodb.m07.domain.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByAccountId(String accountId);
    List<Report> findByMonth(String month);
    List<Report> findByAccountIdAndMonth(String accountId, String month);
}
