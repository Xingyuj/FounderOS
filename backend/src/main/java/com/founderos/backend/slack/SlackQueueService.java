package com.founderos.backend.slack;

import com.founderos.backend.domain.*;
import com.founderos.backend.repository.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;

@Service
public class SlackQueueService {
    private final SlackInboxEventRepository inbox;
    private final SlackOutboxMessageRepository outbox;
    public SlackQueueService(SlackInboxEventRepository inbox, SlackOutboxMessageRepository outbox) { this.inbox = inbox; this.outbox = outbox; }
    @Transactional public Optional<SlackInboxEvent> claimInbox() { var next = inbox.claimNext(Instant.now()); next.ifPresent(SlackInboxEvent::processing); return next; }
    @Transactional public Optional<SlackOutboxMessage> claimOutbox() { var next = outbox.claimNext(Instant.now()); next.ifPresent(SlackOutboxMessage::processing); return next; }
    public void save(SlackInboxEvent event) { inbox.save(event); }
    public void save(SlackOutboxMessage message) { outbox.save(message); }
    @EventListener(ApplicationReadyEvent.class)
    public void recoverInterruptedClaims() {
        inbox.findAll().stream().filter(e -> e.getStatus() == QueueStatus.PROCESSING).forEach(e -> { e.retry("Recovered interrupted inbox processing"); inbox.save(e); });
        outbox.findAll().stream().filter(m -> m.getStatus() == QueueStatus.PROCESSING).forEach(m -> { m.retry("Recovered interrupted outbox delivery"); outbox.save(m); });
    }
}
