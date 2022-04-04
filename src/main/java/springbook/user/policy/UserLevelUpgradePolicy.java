package springbook.user.policy;

import springbook.user.domain.User;

import java.sql.SQLException;

public interface UserLevelUpgradePolicy {
    boolean canUpgradeLevel(User user);

    void upgradeLevel(User user);
}
