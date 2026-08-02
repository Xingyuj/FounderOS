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
    @GetMapping("/slack/agents") public List<AgentView> agents(@RequestHeader("X-FounderOS-Admin-Token") String token) { service.authorizeAdmin(token); return service.agents(); }
    @PutMapping("/slack/channels/{channelId}") public ChannelBindingView bind(@RequestHeader("X-FounderOS-Admin-Token") String token, @PathVariable String channelId, @Valid @RequestBody BindChannelRequest request) { service.authorizeAdmin(token); return service.bind(channelId, request); }
    @GetMapping("/tasks/{id}") public TaskView task(@RequestHeader("X-FounderOS-Admin-Token") String token, @PathVariable UUID id) { service.authorizeAdmin(token); return service.task(id); }
    @GetMapping("/tasks") public List<TaskView> tasks(@RequestHeader("X-FounderOS-Admin-Token") String token) { service.authorizeAdmin(token); return service.tasks(); }
    @PostMapping("/slack/decisions/{id}/publish") public PublishDecisionResponse publish(@RequestHeader("X-FounderOS-Admin-Token") String token, @PathVariable UUID id, @Valid @RequestBody PublishDecisionRequest request) { service.authorizeAdmin(token); return service.publishDecision(id, request); }
}
