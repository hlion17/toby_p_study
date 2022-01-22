package springbook.user.dao;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;

public class UserDaoConnectionCountingTest {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);

        // Dao 사용코드
        User user = new User();

        user.setId("test");
        user.setName("김테스트");
        user.setPassword("1234");

        dao.add(user);

        System.out.println("회원가입 성공");

        User findUser = dao.get(user.getId());

        System.out.println("회원조회 성공");
        System.out.println(findUser.getId());
        System.out.println(findUser.getName());
        System.out.println(findUser.getPassword());

        // db사용 횟수 테스트 코드
        CountingConnectionMaker ccm = context.getBean("connectionMaker", CountingConnectionMaker.class);
        System.out.println("Connection counter = " + ccm.getCounter());

    }
}
