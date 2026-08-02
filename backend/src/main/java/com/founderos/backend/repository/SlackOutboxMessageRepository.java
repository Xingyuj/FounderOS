package com.founderos.backend.repository;
import com.founderos.backend.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.*;
public interface SlackOutboxMessageRepository extends JpaRepository<SlackOutboxMessage, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select m from SlackOutboxMessage m where m.status = com.founderos.backend.domain.QueueStatus.PENDING and m.availableAt <= :now order by m.createdAt asc limit 1")
    Optional<SlackOutboxMessage> claimNext(@Param("now") Instant now);
}
