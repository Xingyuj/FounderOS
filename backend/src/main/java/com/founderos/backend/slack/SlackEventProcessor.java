package com.founderos.backend.slack;

import com.founderos.backend.api.ApiModels.ResolveDecisionRequest;
import com.founderos.backend.domain.*;
import com.founderos.backend.repository.*;
import com.founderos.backend.service.FounderOsService;
import org.springframework.stereotype.Service;

@Service
public class SlackEventProcessor {
    private final SlackMessageRouter router;
    private final SlackDecisionActionRepository actions;
    private final SlackOutboxMessageRepository outbox;
    private final FounderOsService founderOs;
    public SlackEventProcessor(SlackMessageRouter router, SlackDecisionActionRepository actions, SlackOutboxMessageRepository outbox, FounderOsService founderOs) { this.router = router; this.actions = actions; this.outbox = outbox; this.founderOs = founderOs; }
    public void process(SlackInboxEvent event) {
        if ("MESSAGE".equals(event.getEventType())) { router.route(event); return; }
        if ("DECISION_ACTION".equals(event.getEventType())) { resolveDecision(event); return; }
        throw new IllegalArgumentException("Unsupported Slack inbox event type");
    }
    private void resolveDecision(SlackInboxEvent event) {
        SlackDecisionAction action = actions.findById(event.getActionToken()).orElseThrow(() -> new IllegalArgumentException("Decision action token not found"));
        if (action.getConsumedAt() != null) {
            outbox.save(new SlackOutboxMessage(event.getSlackTeamId(), event.getSlackChannelId(), event.getSlackThreadTs(), AgentRole.CHIEF_OF_STAFF, "Chief of Staff · FounderOS\nThis decision was already recorded.", null));
            return;
        }
        founderOs.resolve(action.getDecisionId(), new ResolveDecisionRequest(action.getSelectedOption(), event.getFounderComment()));
        action.consume(); actions.save(action);
        String result = "Chief of Staff · FounderOS\nDecision recorded: " + action.getSelectedOption();
        if (event.getSlackMessageTs() == null || event.getSlackMessageTs().isBlank()) outbox.save(new SlackOutboxMessage(event.getSlackTeamId(), event.getSlackChannelId(), event.getSlackThreadTs(), AgentRole.CHIEF_OF_STAFF, result, null));
        else outbox.save(SlackOutboxMessage.update(event.getSlackTeamId(), event.getSlackChannelId(), event.getSlackMessageTs(), AgentRole.CHIEF_OF_STAFF, result, "[]"));
    }
}
