import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.policy.UserLevelUpgradePolicyImpl;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;
import springbook.user.service.UserServiceTx;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test_applicationContext.xml")
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserServiceImpl userServiceImpl;
    List<User> users;

    @Autowired
    UserDao userDao;

    @Autowired
    DataSource dataSource;

    @Autowired
    PlatformTransactionManager transactionManager;

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 50;

    @Test
    public void bean() {
        assertNotNull(this.userService);
    }

    @Before
    public void setUp() {
        // 배열을 리스트로 만들어주는 메서드
        users = Arrays.asList(
                new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 9),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
                new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD),
                new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @Test
    public void upgradeLevels() {
        // 고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성하면 된다.
        UserServiceImpl userServiceImpl = new UserServiceImpl();
        UserLevelUpgradePolicyImpl userLevelUpgradePolicy = new UserLevelUpgradePolicyImpl();

        // 목 오브젝트로 만든 UserDao 를 직접 DI 해준다.
        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);
        userServiceImpl.setUserLevelUpgradePolicy(userLevelUpgradePolicy);
        userLevelUpgradePolicy.setUserDao(mockUserDao);

        // Given
//        userDao.deleteAll();
//        for (User user : users) {
//            userDao.add(user);
//        }

        userServiceImpl.upgradeLevels();

        // MockUserDao 로부터 업데이트 결과를 가져온다.
        List<User> updated = mockUserDao.getUpdated();
        assertEquals(2, updated.size());
        // 업데이트 횟수와 정보를 확인한다.
        checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
        checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);


        // When
//        userService.upgradeLevels();

        // Then
//        checkLevel(users.get(0), false);
//        checkLevel(users.get(1), true);
//        checkLevel(users.get(2), false);
//        checkLevel(users.get(3), true);
//        checkLevel(users.get(4), false);
    }



    // 처음 가입하는 사용자의 기본 Level 이 BASIC 인지 테스트
    @Test
    public void add() {
        userDao.deleteAll();

        // GOLD 레벨이 이미 지정된 User 라면
        // 레벨을 초기화하지 않아야한다.
        User userWithLevel = users.get(4);
        // 레벨이 비어 있는 사용자. 로직에 따라 등록중에
        // BASIC 레벨로 설정되어야 한다.
        User userWithOutLevel = users.get(0);
        userWithOutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithOutLevel);

        // DB 에 저장된 결과를 가져와 확인한다.
        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithOutLevelRead = userDao.get(userWithOutLevel.getId());

        assertEquals(userWithLevelRead.getLevel(), userWithLevel.getLevel());
        assertEquals(userWithOutLevelRead.getLevel(), Level.BASIC);
    }

    // 어떤 레벨로 바뀔 것인가가 아니라,
    // 다음 레벨로 업그레이드 될 것인가 아닌가를 지정한다.
    private void checkLevel(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) {
            // 업그레이드가 일어났는지 확인
            // 다음 레벨이 무엇인지는 Level 에게 확인
            assertEquals(user.getLevel().nextLevel(), userUpdate.getLevel());
        } else {
            assertEquals(user.getLevel(), userUpdate.getLevel());
        }
    }

    @Test
    public void upgradeAllOrNoting() {
        // 예외를 발생시킬 네 번째 사용자의 id를 넣어서 테스트용 UserServiceImpl 대역 오브젝트를 생성한다.
        TestUserLevelUpgradePolicy testUserLevelUpgradePolicy = new TestUserLevelUpgradePolicy(users.get(3).getId());
        // UserDao 수동 DI
        testUserLevelUpgradePolicy.setUserDao(this.userDao);

        UserServiceTx txUserService = new UserServiceTx();
        UserServiceImpl userService = new UserServiceImpl();

        userService.setUserLevelUpgradePolicy(testUserLevelUpgradePolicy);
        userService.setUserDao(this.userDao);

        txUserService.setTransactionManager(this.transactionManager);
        txUserService.setUserService(userService);

        userDao.deleteAll();
        for (User user : users) userDao.add(user);

        try {
            // TestUserService 는 업그레이드 작업 중에 예외가 발생해야 한다.
            // 정상 종료라면 문제가 있으니 실패
            txUserService.upgradeLevels();
            fail("TestUserLevelUpgradePolicyException Expected");
        } catch (TestUserLevelUpgradePolicyException e) {
            // TestUserLevelUpgradePolicy 가 던져주는 예외를 잡아서 계속 진행되도록 한다.
            // 그 외의 예외라면 테스트 실패
        }

        // 예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레벨이 처음 상태로 바뀌었나 확인
        checkLevel(users.get(1), false);
    }

    // 사용자 레벨 업그레이드시 장애상황 가정한 테스트용 대역 클래스
    static class TestUserLevelUpgradePolicy extends UserLevelUpgradePolicyImpl {
        private String id;

        // 예외를 발생시킬 User 오브젝트의 id를 지정할 수 있게 만든다.
        public TestUserLevelUpgradePolicy(String id) {
            this.id = id;
        }

        // UserLevelUpgradePolicy 를 오버라이딩 한다.
        @Override
        public void upgradeLevel(User user) {
            // 지정된 id의 User 오브젝트가 발견되면 예외를 던져서 작업을 강제 중단
            if (user.getId().equals(this.id)) throw new TestUserLevelUpgradePolicyException();
            super.upgradeLevel(user);
        }

    }

    // 테스트용 예외 클래스 - 장애상황 가정
    static class TestUserLevelUpgradePolicyException extends RuntimeException {}

    // upgradeLevels() 메소드 고립 테스트를 위한 UserDao 목오브젝트
    static class MockUserDao implements UserDao {
        // 레벨 업그레이드 후보 User 오브젝트 목록
        private List<User> users;
        // 업그레이드 대상 오브젝트를 저장해 둘 목록
        private List<User> updated = new ArrayList<>();

        private MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return updated;
        }


        @Override
        public List<User> getAll() {
            return this.users;
        }

        @Override
        public void update(User user) {
            updated.add(user);
        }

        @Override
        public void add(User user) { throw new UnsupportedOperationException(); }
        @Override
        public User get(String id) { throw new UnsupportedOperationException(); }
        @Override
        public void deleteAll() { throw new UnsupportedOperationException(); }
        @Override
        public int getCount() { throw new UnsupportedOperationException(); }
    }

    // id와 level을 확인하는 간단한 헬퍼 메서드
    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertEquals(expectedId, updated.getId());
        assertEquals(expectedLevel, updated.getLevel());
    }

}
