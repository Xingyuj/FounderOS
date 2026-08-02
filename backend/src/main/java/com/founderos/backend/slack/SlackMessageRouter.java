package com.founderos.backend.slack;

import com.founderos.backend.domain.*;
import com.founderos.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class SlackMessageRouter {
    private static final Map<String,AgentRole> PREFIXES = Map.ofEntries(
        Map.entry("chief of staff", AgentRole.CHIEF_OF_STAFF), Map.entry("chief", AgentRole.CHIEF_OF_STAFF), Map.entry("秘书", AgentRole.CHIEF_OF_STAFF),
        Map.entry("product lead", AgentRole.PRODUCT_LEAD), Map.entry("product", AgentRole.PRODUCT_LEAD), Map.entry("产品", AgentRole.PRODUCT_LEAD),
        Map.entry("research analyst", AgentRole.RESEARCH_ANALYST), Map.entry("research", AgentRole.RESEARCH_ANALYST), Map.entry("研究", AgentRole.RESEARCH_ANALYST),
        Map.entry("engineering lead", AgentRole.ENGINEERING_LEAD), Map.entry("engineering", AgentRole.ENGINEERING_LEAD), Map.entry("工程", AgentRole.ENGINEERING_LEAD),
        Map.entry("growth lead", AgentRole.GROWTH_LEAD), Map.entry("growth", AgentRole.GROWTH_LEAD), Map.entry("增长", AgentRole.GROWTH_LEAD)
    );
    private final SlackChannelBindingRepository bindings;
    private final SlackConversationRepository conversations;
    private final WorkTaskRepository tasks;
    private final SlackOutboxMessageRepository outbox;
    public SlackMessageRouter(SlackChannelBindingRepository bindings, SlackConversationRepository conversations, WorkTaskRepository tasks, SlackOutboxMessageRepository outbox) {
        this.bindings = bindings; this.conversations = conversations; this.tasks = tasks; this.outbox = outbox;
    }
    @Transactional
    public void route(SlackInboxEvent event) {
        String threadTs = blank(event.getSlackThreadTs()) ? event.getSlackMessageTs() : event.getSlackThreadTs();
        var existing = conversations.findBySlackTeamIdAndSlackChannelIdAndSlackThreadTs(event.getSlackTeamId(), event.getSlackChannelId(), threadTs);
        if (existing.isPresent()) {
            SlackConversation conversation = existing.get();
            AgentRole role = conversation.getAgentRole() == null ? AgentRole.CHIEF_OF_STAFF : conversation.getAgentRole();
            outbox.save(new SlackOutboxMessage(event.getSlackTeamId(), event.getSlackChannelId(), threadTs, role, heading(role) + "\nUpdate received for task " + shortId(conversation.getTaskId()) + ".", null));
            return;
        }
        boolean dm = "im".equalsIgnoreCase(event.getChannelType());
        ParsedMessage parsed = parse(event.getText());
        SlackChannelBinding binding = dm ? null : bindings.findBySlackTeamIdAndSlackChannelId(event.getSlackTeamId(), event.getSlackChannelId()).orElse(null);
        AgentRole role = parsed.explicitRole() != null ? parsed.explicitRole() : binding != null ? binding.getPrimaryAgentRole() : AgentRole.CHIEF_OF_STAFF;
        UUID projectId = binding == null ? null : binding.getProjectId();
        SlackConversationKind kind = dm ? SlackConversationKind.DM : binding != null && binding.getChannelKind() == SlackChannelKind.PROJECT_CHANNEL ? SlackConversationKind.PROJECT_CHANNEL : SlackConversationKind.FUNCTIONAL_CHANNEL;
        SlackConversation conversation = conversations.save(new SlackConversation(event.getSlackTeamId(), event.getSlackChannelId(), threadTs, kind, projectId, role));
        String description = parsed.text().isBlank() ? "Founder requested follow-up" : parsed.text();
        WorkTask task = tasks.save(new WorkTask(projectId, title(description), description, role, TaskCreator.FOUNDER, conversation.getId()));
        conversation.attachTask(task.getId()); conversations.save(conversation);
        String scope = projectId == null ? "Inbox" : "Project " + projectId;
        String response = heading(role) + "\nAssigned · Task " + shortId(task.getId()) + " · " + scope + "\n" + description;
        outbox.save(new SlackOutboxMessage(event.getSlackTeamId(), event.getSlackChannelId(), threadTs, role, response, null));
    }
    private ParsedMessage parse(String raw) {
        String text = raw == null ? "" : raw.trim();
        String normalized = text.toLowerCase(Locale.ROOT);
        for (var entry : PREFIXES.entrySet()) {
            for (String separator : List.of(":", "：")) {
                String prefix = entry.getKey() + separator;
                if (normalized.startsWith(prefix)) return new ParsedMessage(entry.getValue(), text.substring(prefix.length()).trim());
            }
        }
        return new ParsedMessage(null, text.replaceFirst("^<@[A-Z0-9]+>\\s*", "").trim());
    }
    private String title(String text) { String oneLine = text.replaceAll("\\s+", " ").trim(); return oneLine.substring(0, Math.min(120, oneLine.length())); }
    private String heading(AgentRole role) { return role.displayName() + " · FounderOS"; }
    private String shortId(UUID id) { return id == null ? "unassigned" : id.toString().substring(0, 8).toUpperCase(Locale.ROOT); }
    private boolean blank(String value) { return value == null || value.isBlank(); }
    private record ParsedMessage(AgentRole explicitRole, String text) {}
}
