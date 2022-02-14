import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test_applicationContext.xml")
public class UserServiceTest {
    @Autowired
    UserService userService;
    List<User> users;

    @Autowired
    UserDao userDao;

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
        // Given
        userDao.deleteAll();
        for (User user : users) { userDao.add(user); }

        // When
        userService.upgradeLevels();

        // Then
        checkLevel(users.get(0), false);
        checkLevel(users.get(1), true);
        checkLevel(users.get(2), false);
        checkLevel(users.get(3), true);
        checkLevel(users.get(4), false  );
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
            assertEquals(userUpdate.getLevel(), user.getLevel().nextLevel());
        } else {
            assertEquals(userUpdate.getLevel(), user.getLevel());
        }
    }
}
