package com.founderos.backend.repository;
import com.founderos.backend.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.*;
public interface SlackInboxEventRepository extends JpaRepository<SlackInboxEvent, UUID> {
    Optional<SlackInboxEvent> findByExternalId(String externalId);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select e from SlackInboxEvent e where e.status = com.founderos.backend.domain.QueueStatus.PENDING and e.availableAt <= :now order by e.createdAt asc limit 1")
    Optional<SlackInboxEvent> claimNext(@Param("now") Instant now);
}
