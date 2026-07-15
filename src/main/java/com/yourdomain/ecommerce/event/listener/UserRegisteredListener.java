package com.yourdomain.ecommerce.event.listener;

import com.yourdomain.ecommerce.event.UserRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserRegisteredListener {

    @Async("taskExecutor")
    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("User registered: id={} username={} email={}",
                event.getUserId(), event.getUsername(), event.getEmail());
        // TODO: send welcome email, create default cart, enqueue marketing job, etc.
    }
}
