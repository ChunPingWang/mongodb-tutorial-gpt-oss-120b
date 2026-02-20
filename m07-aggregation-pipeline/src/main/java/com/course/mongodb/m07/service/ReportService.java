package com.course.mongodb.m07.service;

import com.course.mongodb.m07.domain.Report;
import com.course.mongodb.m07.repository.ReportRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository repository;

    public ReportService(ReportRepository repository) {
        this.repository = repository;
    }

    public Report createReport(String accountId, String month, Double income, Double expense) {
        Report report = new Report(accountId, month, income, expense);
        return repository.save(report);
    }

    public List<Report> findAll() {
        return repository.findAll();
    }

    public Report findById(String id) {
        return repository.findById(id).orElse(null);
    }

    public List<Report> findByAccountId(String accountId) {
        return repository.findByAccountId(accountId);
    }

    public List<Report> findByMonth(String month) {
        return repository.findByMonth(month);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
