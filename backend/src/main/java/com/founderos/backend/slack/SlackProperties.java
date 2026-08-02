package com.founderos.backend.slack;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "slack")
public class SlackProperties {
    private boolean enabled;
    private String signingSecret = "";
    private String botToken = "";
    private String teamId = "";
    private String founderUserId = "";
    private String adminToken = "";
    private long signatureToleranceSeconds = 300;
    private int maxAttempts = 5;
    public boolean isEnabled() { return enabled; } public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getSigningSecret() { return signingSecret; } public void setSigningSecret(String value) { signingSecret = value; }
    public String getBotToken() { return botToken; } public void setBotToken(String value) { botToken = value; }
    public String getTeamId() { return teamId; } public void setTeamId(String value) { teamId = value; }
    public String getFounderUserId() { return founderUserId; } public void setFounderUserId(String value) { founderUserId = value; }
    public String getAdminToken() { return adminToken; } public void setAdminToken(String value) { adminToken = value; }
    public long getSignatureToleranceSeconds() { return signatureToleranceSeconds; } public void setSignatureToleranceSeconds(long value) { signatureToleranceSeconds = value; }
    public int getMaxAttempts() { return maxAttempts; } public void setMaxAttempts(int value) { maxAttempts = value; }
    public void requireConfigured() {
        if (!enabled || blank(signingSecret) || blank(teamId) || blank(founderUserId)) throw new SlackSecurityException("Slack integration is not configured");
    }
    private boolean blank(String value) { return value == null || value.isBlank(); }
}
