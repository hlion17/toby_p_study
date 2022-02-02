package springbook;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import springbook.user.dao.UserDaoJdbc;

import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

//        DaoFactory factory = new DaoFactory();
//        UserDaoJdbc dao1 = factory.userDao();
//        UserDaoJdbc dao2 = factory.userDao();
//
//        System.out.println(dao1);
//        System.out.println(dao2);
//
//        System.out.println("Application Context 사용 결과");
//
//        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
//        UserDaoJdbc dao3 = context.getBean("userDao", UserDaoJdbc.class);
//        UserDaoJdbc dao4 = context.getBean("userDao", UserDaoJdbc.class);
//
//        System.out.println(dao3);
//        System.out.println(dao4);


        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        UserDaoJdbc dao = context.getBean("userDao", UserDaoJdbc.class);
        System.out.println(dao.getCount());
    }
}
