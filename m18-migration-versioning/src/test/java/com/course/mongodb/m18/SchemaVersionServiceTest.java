package com.course.mongodb.m18;

import com.course.mongodb.m18.domain.SchemaVersion;
import com.course.mongodb.m18.service.SchemaVersionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchemaVersionServiceTest {

    @Mock
    private SchemaVersionRepository repository;

    @InjectMocks
    private SchemaVersionService service;

    @Test
    void createMigration_shouldSaveNewMigration() {
        SchemaVersion version = new SchemaVersion(
            "1.0.0", 
            "Create users collection", 
            "db.createCollection('users')"
        );
        when(repository.existsByVersion("1.0.0")).thenReturn(false);
        when(repository.save(any(SchemaVersion.class))).thenReturn(version);

        SchemaVersion result = service.createMigration(
            "1.0.0", 
            "Create users collection", 
            "db.createCollection('users')"
        );

        assertNotNull(result);
        assertEquals("1.0.0", result.getVersion());
        assertEquals(SchemaVersion.SchemaState.PENDING, result.getState());
    }

    @Test
    void createMigration_shouldThrowWhenVersionExists() {
        when(repository.existsByVersion("1.0.0")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> 
            service.createMigration("1.0.0", "Desc", "script")
        );
    }

    @Test
    void applyMigration_shouldMarkAsApplied() {
        SchemaVersion version = new SchemaVersion("1.0.0", "Desc", "script");
        when(repository.findByVersion("1.0.0")).thenReturn(Optional.of(version));
        when(repository.save(any(SchemaVersion.class))).thenReturn(version);

        SchemaVersion result = service.applyMigration("1.0.0", "admin");

        assertEquals(SchemaVersion.SchemaState.APPLIED, result.getState());
        assertEquals("admin", result.getAppliedBy());
        assertNotNull(result.getAppliedAt());
    }

    @Test
    void isMigrationApplied_shouldReturnTrueWhenApplied() {
        SchemaVersion version = new SchemaVersion("1.0.0", "Desc", "script");
        version.markApplied("admin");
        when(repository.findByVersion("1.0.0")).thenReturn(Optional.of(version));

        boolean result = service.isMigrationApplied("1.0.0");

        assertTrue(result);
    }

    @Test
    void rollbackMigration_shouldChangeState() {
        SchemaVersion version = new SchemaVersion("1.0.0", "Desc", "script");
        version.markApplied("admin");
        when(repository.findByVersion("1.0.0")).thenReturn(Optional.of(version));
        when(repository.save(any(SchemaVersion.class))).thenReturn(version);

        SchemaVersion result = service.rollbackMigration("1.0.0");

        assertEquals(SchemaVersion.SchemaState.ROLLED_BACK, result.getState());
    }
}
