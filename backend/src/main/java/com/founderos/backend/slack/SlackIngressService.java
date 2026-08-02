package com.founderos.backend.slack;

import com.founderos.backend.domain.SlackInboxEvent;
import com.founderos.backend.repository.SlackInboxEventRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class SlackIngressService {
    private final SlackInboxEventRepository events;
    public SlackIngressService(SlackInboxEventRepository events) { this.events = events; }
    public boolean accept(SlackInboxEvent event) {
        if (events.findByExternalId(event.getExternalId()).isPresent()) return false;
        try { events.saveAndFlush(event); return true; }
        catch (DataIntegrityViolationException duplicate) { return false; }
    }
}
