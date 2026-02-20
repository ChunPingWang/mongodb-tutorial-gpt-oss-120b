package com.course.mongodb.m18.service;

import com.course.mongodb.m18.domain.SchemaVersion;
import com.course.mongodb.m18.repository.SchemaVersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SchemaVersionService {

    private final SchemaVersionRepository repository;

    public SchemaVersionService(SchemaVersionRepository repository) {
        this.repository = repository;
    }

    public SchemaVersion createMigration(String version, String description, String migrationScript) {
        if (repository.existsByVersion(version)) {
            throw new IllegalStateException("Migration version " + version + " already exists");
        }
        
        SchemaVersion schemaVersion = new SchemaVersion(version, description, migrationScript);
        return repository.save(schemaVersion);
    }

    public SchemaVersion applyMigration(String version, String appliedBy) {
        SchemaVersion schemaVersion = repository.findByVersion(version)
            .orElseThrow(() -> new IllegalArgumentException("Migration not found: " + version));
        
        long startTime = System.currentTimeMillis();
        
        try {
            schemaVersion.markApplied(appliedBy);
            schemaVersion.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            
            return repository.save(schemaVersion);
        } catch (Exception e) {
            schemaVersion.markFailed(e.getMessage());
            schemaVersion.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            return repository.save(schemaVersion);
        }
    }

    public Optional<SchemaVersion> findByVersion(String version) {
        return repository.findByVersion(version);
    }

    public List<SchemaVersion> findAllMigrations() {
        return repository.findAllByOrderByVersionDesc();
    }

    public List<SchemaVersion> findByState(SchemaVersion.SchemaState state) {
        return repository.findByState(state);
    }

    public Optional<SchemaVersion> findLatestApplied() {
        return repository.findTopByStateOrderByAppliedAtDesc(SchemaVersion.SchemaState.APPLIED);
    }

    public List<SchemaVersion> getPendingMigrations() {
        return repository.findByState(SchemaVersion.SchemaState.PENDING);
    }

    public boolean isMigrationApplied(String version) {
        return repository.findByVersion(version)
            .map(sv -> sv.getState() == SchemaVersion.SchemaState.APPLIED)
            .orElse(false);
    }

    public SchemaVersion rollbackMigration(String version) {
        SchemaVersion schemaVersion = repository.findByVersion(version)
            .orElseThrow(() -> new IllegalArgumentException("Migration not found: " + version));
        
        if (schemaVersion.getState() != SchemaVersion.SchemaState.APPLIED) {
            throw new IllegalStateException("Can only rollback applied migrations");
        }
        
        schemaVersion.setState(SchemaVersion.SchemaState.ROLLED_BACK);
        return repository.save(schemaVersion);
    }
}
