package springbook.user.dao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.domain.User;
import java.sql.SQLException;
import java.util.List;

import org.junit.runner.JUnitCore;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "/test_applicationContext.xml")
//@DirtiesContext
public class UserDaoTest {

//    @Autowired
//    ApplicationContext context;

    // 픽스처(fixture)
    @Autowired
    private UserDao dao;
    private User user1;
    private User user2;
    private User user3;

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        JUnitCore.main("springbook.user.dao.UserDaoTest");
    }

    @Before
    public void setUp() {
//        this.dao = context.getBean("userDao", UserDao.class);
        this.user1 = new User("test1", "김자바", "12341");
        this.user2 = new User("test2", "이자바", "12342");
        this.user3 = new User("test3", "박자바", "12343");

//        System.out.println("[컨테이너] : " + this.context);
//        System.out.println("[객체] : " + this);

//        DataSource dataSource = new SingleConnectionDataSource("jdbc:h2:tcp://localhost:9092/~/toby", "hlion17", "1234", true);
//        dao.setDataSource(dataSource);

//        // 테스트에서 의존성 직접 주입
//        dao = new UserDao();
//        DataSource dataSource = new SingleConnectionDataSource("jdbc:h2:tcp://localhost:9092/~/toby", "hlion17", "1234", true);
//        dao.setDataSource(dataSource);
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userGet1 = dao.get(user1.getId());
        assertThat(userGet1.getName(), is(user1.getName()));
        assertThat(userGet1.getPassword(), is(user1.getPassword()));

        User userGet2 = dao.get(user2.getId());
        assertThat(userGet2.getName(), is(user2.getName()));
        assertThat(userGet2.getPassword(), is(user2.getPassword()));

    }


    @Test
    public void count() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));

        dao.add(user2);
        assertThat(dao.getCount(), is(2));

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
        assertThat(users3.size(), is(3));
        // getAll()의 정렬결과에 따라서 비교 순서를 다르게 해줘야 될 수 도 있음
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));

    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertEquals(user1.getName(), user2.getName());
        assertEquals(user1.getPassword(), user2.getPassword());
    }
}






// main 메서드에 있던 코드
//        // 어노테이션 기반 설정파일 DaoFactory를 통해 어플리케이션 컨텍스트 생성
////        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
//
//        // XML 기반의 설정파일
//        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
//
//        // Application context 에서 userDao 라는 이름의 빈을 찾아 반환(클래스 타입은 UserDao 로 검색)
//        UserDao dao = context.getBean("userDao", UserDao.class);
//
////        UserDao dao = new DaoFactory().userDao();
//
//        User user = new User();
//
//        user.setId("test");
//        user.setName("김테스트");
//        user.setPassword("1234");
//
//        dao.add(user);
//
//        System.out.println("회원가입 성공");
//
//        User findUser = dao.get(user.getId());
//
////        System.out.println("회원조회 성공");
////        System.out.println(findUser.getId());
////        System.out.println(findUser.getName());
////        System.out.println(findUser.getPassword());
//
//
//        if (!user.getName().equals(findUser.getName())) {
//            System.out.println("테스트 실패( name )");
//        } else if (!user.getId().equals(findUser.getId())) {
//            System.out.println("테스트 실패( Id )");
//        } else if (!user.getPassword().equals(findUser.getPassword())) {
//            System.out.println("테스트 실패( password )");
//        } else {
//            System.out.println("조회 테스트 성공");
//        }
