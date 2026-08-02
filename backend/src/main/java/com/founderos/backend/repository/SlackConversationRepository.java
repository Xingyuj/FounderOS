package com.founderos.backend.repository;
import com.founderos.backend.domain.SlackConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SlackConversationRepository extends JpaRepository<SlackConversation, UUID> { Optional<SlackConversation> findBySlackTeamIdAndSlackChannelIdAndSlackThreadTs(String teamId, String channelId, String threadTs); }
