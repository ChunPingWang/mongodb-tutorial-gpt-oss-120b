package com.course.mongodb.m18.repository;

import com.course.mongodb.m18.domain.SchemaVersion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchemaVersionRepository extends MongoRepository<SchemaVersion, String> {
    Optional<SchemaVersion> findByVersion(String version);
    List<SchemaVersion> findByState(SchemaVersion.SchemaState state);
    List<SchemaVersion> findAllByOrderByVersionDesc();
    Optional<SchemaVersion> findTopByStateOrderByAppliedAtDesc(SchemaVersion.SchemaState state);
    boolean existsByVersion(String version);
    List<SchemaVersion> findByAppliedBy(String appliedBy);
}
