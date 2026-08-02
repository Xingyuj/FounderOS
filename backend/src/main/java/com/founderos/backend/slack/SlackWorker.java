package com.founderos.backend.slack;

import com.founderos.backend.slack.SlackWebApiClient.SlackDeliveryException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SlackWorker {
    private final SlackProperties properties;
    private final SlackQueueService queues;
    private final SlackEventProcessor processor;
    private final SlackWebApiClient client;
    public SlackWorker(SlackProperties properties, SlackQueueService queues, SlackEventProcessor processor, SlackWebApiClient client) { this.properties = properties; this.queues = queues; this.processor = processor; this.client = client; }
    @Scheduled(fixedDelayString = "${slack.worker-delay-ms:1000}") public void tick() { if (properties.isEnabled()) { processInboxOnce(); deliverOutboxOnce(); } }
    public boolean processInboxOnce() {
        var claimed = queues.claimInbox(); if (claimed.isEmpty()) return false; var event = claimed.get();
        try { processor.process(event); event.processed(); }
        catch (RuntimeException e) { if (event.getAttemptCount() < properties.getMaxAttempts() && retryable(e)) event.retry(e.getMessage()); else event.failed(e.getMessage()); }
        queues.save(event); return true;
    }
    public boolean deliverOutboxOnce() {
        var claimed = queues.claimOutbox(); if (claimed.isEmpty()) return false; var message = claimed.get();
        try { message.delivered(client.post(message)); }
        catch (SlackDeliveryException e) { if (e.isRetryable() && message.getAttemptCount() < properties.getMaxAttempts()) message.retry(e.getMessage()); else message.failed(e.getMessage()); }
        catch (RuntimeException e) { message.failed(e.getMessage()); }
        queues.save(message); return true;
    }
    private boolean retryable(RuntimeException e) { return !(e instanceof IllegalArgumentException); }
}
