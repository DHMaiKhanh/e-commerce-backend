package com.yourdomain.ecommerce.specification;

import com.yourdomain.ecommerce.entity.User;
import com.yourdomain.ecommerce.enums.UserStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<User> withKeyword(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) {
                return cb.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("username")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("fullName")), pattern)
            );
        };
    }

    public static Specification<User> withStatus(UserStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public static Specification<User> notDeleted() {
        return (root, query, cb) -> cb.notEqual(root.get("status"), UserStatus.DELETED);
    }

    public static Specification<User> build(String keyword, UserStatus status) {
        List<Specification<User>> specs = new ArrayList<>();
        specs.add(notDeleted());
        specs.add(withKeyword(keyword));
        specs.add(withStatus(status));
        return specs.stream().reduce(Specification.where(null), Specification::and);
    }
}
