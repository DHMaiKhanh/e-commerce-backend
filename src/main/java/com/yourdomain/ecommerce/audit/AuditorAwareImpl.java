package com.yourdomain.ecommerce.audit;

import com.yourdomain.ecommerce.constants.AppConstants;
import com.yourdomain.ecommerce.security.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(SecurityUtils.getCurrentUsername().orElse(AppConstants.SYSTEM_USER));
    }
}
