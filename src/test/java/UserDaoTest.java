import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test_applicationContext.xml")
public class UserDaoTest {

    // 픽스처(fixture)
    @Autowired
    private UserDao dao;
    @Autowired
    DataSource datasource;
    private User user1;
    private User user2;
    private User user3;

//    public static void main(String[] args) throws SQLException, ClassNotFoundException {
//        JUnitCore.main("springbook.user.dao.UserDaoJdbcTest");
//    }

    @Before
    public void setUp() {
        this.user1 = new User("test1", "김자바", "12341", Level.BASIC, 1, 0);
        this.user2 = new User("test2", "이자바", "12342", Level.SILVER, 55, 10);
        this.user3 = new User("test3", "박자바", "12343", Level.GOLD, 100, 40);

    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        dao.add(user1);
        dao.add(user2);
        assertEquals(dao.getCount(), 2);

        // addAndGet 메서드에서 User 오브젝트를 비교하는 로직을 일정하게 수행 할 수 있도록 checkSameUser() 메서드 사용
        User userGet1 = dao.get(user1.getId());
        checkSameUser(userGet1, user1);

        User userGet2 = dao.get(user2.getId());
        checkSameUser(userGet2, user2);
    }


    @Test
    public void count() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
//            assertThat(dao.getCount(), is(0));
        assertEquals(dao.getCount(), 0);

        dao.add(user1);
//            assertThat(dao.getCount(), is(1));
        assertEquals(dao.getCount(), 1);

        dao.add(user2);
//            assertThat(dao.getCount(), is(2));
        assertEquals(dao.getCount(), 2);

        dao.add(user3);
//        assertThat(dao.getCount(), is(3));

        assertEquals(dao.getCount(), 3);

    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
//        assertThat(dao.getCount(), is(0));
        assertEquals(dao.getCount(), 0);

        dao.get("Unknown");
    }

    @Test
    public void getAll() throws SQLException, ClassNotFoundException {
        dao.deleteAll();

        List<User> user0 = dao.getAll();
        assertEquals(user0.size(), 0);

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
//            assertThat(users3.size(), is(3));
        assertEquals(users3.size(), 3);
        // getAll()의 정렬결과에 따라서 비교 순서를 다르게 해줘야 될 수 도 있음
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));

    }

    // spring-jdbc 와 spring 버전차이로 인해 다른 에러가 남 - 해결
    @Test(expected = DataAccessException.class)
    public void duplicateKey() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user1);
    }


    @Test
//    @Ignore
    public void sqlExceptionTranslate() {
        dao.deleteAll();

        try {
            dao.add(user1);
            dao.add(user1);
        } catch (DuplicateKeyException ex) {
            SQLException sqlEx = (SQLException) ex.getRootCause();
            // 스프링 예외 전환 api 이용
            SQLExceptionTranslator set =
                    new SQLErrorCodeSQLExceptionTranslator(this.datasource);

            assertEquals(set.translate(null, null, sqlEx), is(DuplicateKeyException.class));
        }
    }

    @Test
    public void update() {
        dao.deleteAll();

        dao.add(user1);
        dao.add(user2);

        user1.setName("update1");
        user1.setPassword("updatepass");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);

        User user1update = dao.get(user1.getId());
        User user2update = dao.get(user2.getId());
        checkSameUser(user1, user1update);
        checkSameUser(user2, user2update);
    }

    private void checkSameUser(User user1, User user2) {
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getName(), user2.getName());
        assertEquals(user1.getPassword(), user2.getPassword());
        assertEquals(user1.getLevel(), user2.getLevel());
        assertEquals(user1.getLogin(), user2.getLogin());
        assertEquals(user1.getRecommend(), user2.getRecommend());
    }
}
