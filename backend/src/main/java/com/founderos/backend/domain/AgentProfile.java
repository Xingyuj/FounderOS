package com.founderos.backend.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name = "agent_profile")
public class AgentProfile {
    @Id @Enumerated(EnumType.STRING) @Column(length = 40) private AgentRole role;
    @Column(name = "display_name", nullable = false, unique = true, length = 100) private String displayName;
    @Column(nullable = false, columnDefinition = "text") private String responsibility;
    @Column(nullable = false) private boolean active;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    protected AgentProfile() {}
    public AgentRole getRole() { return role; }
    public String getDisplayName() { return displayName; }
    public String getResponsibility() { return responsibility; }
    public boolean isActive() { return active; }
}
