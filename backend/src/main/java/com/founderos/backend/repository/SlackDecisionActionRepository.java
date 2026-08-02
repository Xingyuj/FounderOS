package com.founderos.backend.repository;
import com.founderos.backend.domain.SlackDecisionAction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SlackDecisionActionRepository extends JpaRepository<SlackDecisionAction, UUID> { List<SlackDecisionAction> findByDecisionId(UUID decisionId); }
