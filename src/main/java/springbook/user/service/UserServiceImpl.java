package springbook.user.service;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
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

public class UserServiceImpl implements UserService {

//    private DataSource dataSource;
    private UserLevelUpgradePolicy userLevelUpgradePolicy;
    UserDao userDao;

    // Connection 을 생성할 떄 사용할 DataSource 를 DI 받도록 한다.
//    public void setDataSource(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }

    // UserDao 주입자
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    // 유저 레벨 정책 주입자
    public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
        this.userLevelUpgradePolicy = userLevelUpgradePolicy;
    }

    // 사용자등급 설정
    public void upgradeLevels() throws RuntimeException {
        List<User> users = userDao.getAll();
        for (User user : users) {
            if (userLevelUpgradePolicy.canUpgradeLevel(user)) {
                userLevelUpgradePolicy.upgradeLevel(user);
            }
        }
    }

    // 회원등록
    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

}
