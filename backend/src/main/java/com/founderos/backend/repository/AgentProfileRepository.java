package com.founderos.backend.repository;
import com.founderos.backend.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AgentProfileRepository extends JpaRepository<AgentProfile, AgentRole> { List<AgentProfile> findByActiveTrueOrderByDisplayNameAsc(); }
