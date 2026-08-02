package com.founderos.backend.repository;
import com.founderos.backend.domain.WorkTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface WorkTaskRepository extends JpaRepository<WorkTask, UUID> { List<WorkTask> findByProjectIdOrderByCreatedAtAsc(UUID projectId); }
