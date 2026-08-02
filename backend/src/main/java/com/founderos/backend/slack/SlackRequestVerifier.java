package com.founderos.backend.slack;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;

@Component
public class SlackRequestVerifier {
    private final SlackProperties properties;
    private final Clock clock;
    @Autowired public SlackRequestVerifier(SlackProperties properties) { this(properties, Clock.systemUTC()); }
    SlackRequestVerifier(SlackProperties properties, Clock clock) { this.properties = properties; this.clock = clock; }
    public void verify(String timestamp, String signature, String rawBody) {
        properties.requireConfigured();
        long requestTime;
        try { requestTime = Long.parseLong(timestamp); } catch (RuntimeException e) { throw new SlackSecurityException("Missing or invalid Slack timestamp"); }
        if (Math.abs(clock.instant().getEpochSecond() - requestTime) > properties.getSignatureToleranceSeconds()) throw new SlackSecurityException("Stale Slack request");
        if (signature == null || !signature.startsWith("v0=")) throw new SlackSecurityException("Missing or invalid Slack signature");
        String expected = "v0=" + hmac(properties.getSigningSecret(), "v0:" + timestamp + ":" + rawBody);
        if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), signature.getBytes(StandardCharsets.UTF_8))) throw new SlackSecurityException("Invalid Slack signature");
    }
    private String hmac(String secret, String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return java.util.HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new IllegalStateException("Unable to verify Slack request", e); }
    }
}
