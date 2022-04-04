package springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.policy.UserLevelUpgradePolicy;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl_copy implements UserService {

    private DataSource dataSource;
    private PlatformTransactionManager transactionManager;
    private UserLevelUpgradePolicy userLevelUpgradePolicy;
    UserDao userDao;

    // 프로퍼티 이름은 관례를 따라 transactionManager 라고 만드는 것이 편리히다.
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    // Connection 을 생성할 떄 사용할 DataSource 를 DI 받도록 한다.
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // UserDao 주입자
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    // 유저 레벨 정책 주입자
    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    public void upgradeLevels() {
        // 트랜잭션 시작
        TransactionStatus status =
                // DI 받은 트랜잭션 매니저를 공유해서 사용한다.
                // 멀티스레드 환경에서도 안전하다.
                this.transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            // 트랜잭션 안에서 진행되는 작업
            upgradeLevelsInternal();

            this.transactionManager.commit(status);
        } catch (RuntimeException e) {
            this.transactionManager.rollback(status);
            throw e;
        }
    }

    private void upgradeLevelsInternal() {
        List<User> users = userDao.getAll();
        for (User user : users) {
            if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                userLevelUpgradePolicy.upgradeLevel(user);
            }
        }
    }

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

}
