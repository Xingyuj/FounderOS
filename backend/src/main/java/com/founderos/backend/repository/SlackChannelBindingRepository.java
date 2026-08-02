package com.founderos.backend.repository;
import com.founderos.backend.domain.SlackChannelBinding;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SlackChannelBindingRepository extends JpaRepository<SlackChannelBinding, UUID> { Optional<SlackChannelBinding> findBySlackTeamIdAndSlackChannelId(String teamId, String channelId); }
