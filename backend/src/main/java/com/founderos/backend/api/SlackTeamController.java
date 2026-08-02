package com.founderos.backend.api;

import com.founderos.backend.api.SlackApiModels.*;
import com.founderos.backend.slack.SlackTeamService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/api")
public class SlackTeamController {
    private final SlackTeamService service;
    public SlackTeamController(SlackTeamService service) { this.service = service; }
    @GetMapping("/slack/agents") public List<AgentView> agents() { return service.agents(); }
    @PutMapping("/slack/channels/{channelId}") public ChannelBindingView bind(@PathVariable String channelId, @Valid @RequestBody BindChannelRequest request) { return service.bind(channelId, request); }
    @GetMapping("/tasks/{id}") public TaskView task(@PathVariable UUID id) { return service.task(id); }
    @GetMapping("/tasks") public List<TaskView> tasks() { return service.tasks(); }
    @PostMapping("/slack/decisions/{id}/publish") public PublishDecisionResponse publish(@PathVariable UUID id, @Valid @RequestBody PublishDecisionRequest request) { return service.publishDecision(id, request); }
}
