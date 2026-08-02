package com.founderos.backend.slack;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.founderos.backend.api.SlackApiModels.*;
import com.founderos.backend.domain.*;
import com.founderos.backend.repository.*;
import com.founderos.backend.service.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class SlackTeamService {
    private final AgentProfileRepository agents;
    private final SlackChannelBindingRepository bindings;
    private final ProjectRepository projects;
    private final WorkTaskRepository tasks;
    private final DecisionRepository decisions;
    private final SlackDecisionActionRepository actions;
    private final SlackOutboxMessageRepository outbox;
    private final SlackProperties properties;
    private final ObjectMapper mapper;
    public SlackTeamService(AgentProfileRepository agents, SlackChannelBindingRepository bindings, ProjectRepository projects, WorkTaskRepository tasks, DecisionRepository decisions, SlackDecisionActionRepository actions, SlackOutboxMessageRepository outbox, SlackProperties properties, ObjectMapper mapper) {
        this.agents = agents; this.bindings = bindings; this.projects = projects; this.tasks = tasks; this.decisions = decisions; this.actions = actions; this.outbox = outbox; this.properties = properties; this.mapper = mapper;
    }
    public List<AgentView> agents() { return agents.findByActiveTrueOrderByDisplayNameAsc().stream().map(AgentView::of).toList(); }
    public void authorizeAdmin(String token) {
        String expected = properties.getAdminToken();
        if (expected == null || expected.isBlank() || token == null || !MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), token.getBytes(StandardCharsets.UTF_8))) throw new SlackSecurityException("Invalid FounderOS Slack admin token");
    }
    @Transactional
    public ChannelBindingView bind(String channelId, BindChannelRequest request) {
        requireConfiguredTeam();
        UUID projectId = request.projectId();
        if (projectId != null && !projects.existsById(projectId)) throw new NotFoundException("Project not found");
        Set<UUID> replaced = new HashSet<>();
        bindings.findBySlackTeamIdAndSlackChannelId(properties.getTeamId(), channelId).ifPresent(binding -> replaced.add(binding.getId()));
        if (projectId != null) bindings.findByProjectId(projectId).ifPresent(binding -> replaced.add(binding.getId()));
        if (!replaced.isEmpty()) { bindings.deleteAllById(replaced); bindings.flush(); }
        return ChannelBindingView.of(bindings.save(new SlackChannelBinding(properties.getTeamId(), channelId, request.kind(), request.primaryAgentRole(), projectId, request.label())));
    }
    public TaskView task(UUID id) { return tasks.findById(id).map(TaskView::of).orElseThrow(() -> new NotFoundException("Task not found")); }
    public List<TaskView> tasks() { return tasks.findAll().stream().map(TaskView::of).toList(); }
    @Transactional
    public PublishDecisionResponse publishDecision(UUID decisionId, PublishDecisionRequest request) {
        requireConfiguredTeam();
        FounderDecision decision = decisions.findById(decisionId).orElseThrow(() -> new NotFoundException("Decision not found"));
        if (decision.getStatus() != DecisionStatus.OPEN) throw new IllegalArgumentException("Only open decisions can be published");
        List<SlackDecisionAction> existing = actions.findByDecisionId(decisionId);
        Map<String,SlackDecisionAction> byOption = new HashMap<>(); existing.forEach(a -> byOption.put(a.getSelectedOption(), a));
        List<SlackDecisionAction> optionActions = decision.getOptions().stream().map(option -> {
            SlackDecisionAction existingAction = byOption.get(option);
            return existingAction != null ? existingAction : actions.save(new SlackDecisionAction(decisionId, option));
        }).toList();
        String text = "Chief of Staff · FounderOS\nApproval required · " + decision.getQuestion();
        String blocks = blocks(decision, optionActions);
        SlackOutboxMessage message = outbox.save(new SlackOutboxMessage(properties.getTeamId(), request.channelId(), request.threadTs(), AgentRole.CHIEF_OF_STAFF, text, blocks));
        return new PublishDecisionResponse(message.getId(), decisionId, optionActions.stream().map(SlackDecisionAction::getToken).toList());
    }
    private String blocks(FounderDecision decision, List<SlackDecisionAction> optionActions) {
        try {
            List<Map<String,Object>> blocks = new ArrayList<>();
            blocks.add(Map.of("type", "header", "text", Map.of("type", "plain_text", "text", "Chief of Staff · Approval required")));
            blocks.add(Map.of("type", "section", "text", Map.of("type", "mrkdwn", "text", "*" + decision.getQuestion() + "*\n" + Optional.ofNullable(decision.getContext()).orElse(""))));
            List<Map<String,Object>> elements = new ArrayList<>();
            for (SlackDecisionAction action : optionActions) elements.add(Map.of("type", "button", "action_id", "resolve_decision", "text", Map.of("type", "plain_text", "text", truncate(action.getSelectedOption(), 75)), "value", action.getToken().toString()));
            blocks.add(Map.of("type", "actions", "elements", elements));
            return mapper.writeValueAsString(blocks);
        } catch (JsonProcessingException e) { throw new IllegalStateException("Unable to render Slack decision", e); }
    }
    private void requireConfiguredTeam() { if (properties.getTeamId() == null || properties.getTeamId().isBlank()) throw new IllegalStateException("Slack team ID is not configured"); }
    private String truncate(String value, int limit) { return value.substring(0, Math.min(value.length(), limit)); }
}
