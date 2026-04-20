package com.example.eventnotificator.notification.service;

import com.example.eventnotificator.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationCounterService {

    private static final Logger log = LoggerFactory.getLogger(NotificationCounterService.class);

    private final StringRedisTemplate redisTemplate;
    private final NotificationRepository notificationRepository;

    public NotificationCounterService(StringRedisTemplate redisTemplate,
                                      NotificationRepository notificationRepository) {
        this.redisTemplate = redisTemplate;
        this.notificationRepository = notificationRepository;
    }

    public void incrementUnread(Long userId, long delta) {
        try {
            redisTemplate.opsForValue().increment(unreadKey(userId), delta);
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis is unavailable, skipping unread counter increment for userId={}", userId, ex);
        }
    }

    public void syncUnreadFromDatabase(Long userId) {
        try {
            long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);
            redisTemplate.opsForValue().set(unreadKey(userId), Long.toString(unreadCount));
        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis is unavailable, skipping unread counter sync for userId={}", userId, ex);
        }
    }

    private String unreadKey(Long userId) {
        return "notif:unread:" + userId;
    }
}