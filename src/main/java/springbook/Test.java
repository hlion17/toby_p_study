package springbook;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import springbook.user.dao.DConnectionMaker;
import springbook.user.dao.DaoFactory;
import springbook.user.dao.UserDao;
import springbook.user.domain.User;

import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

//        DaoFactory factory = new DaoFactory();
//        UserDao dao1 = factory.userDao();
//        UserDao dao2 = factory.userDao();
//
//        System.out.println(dao1);
//        System.out.println(dao2);
//
//        System.out.println("Application Context 사용 결과");
//
//        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
//        UserDao dao3 = context.getBean("userDao", UserDao.class);
//        UserDao dao4 = context.getBean("userDao", UserDao.class);
//
//        System.out.println(dao3);
//        System.out.println(dao4);


        ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
        UserDao dao = context.getBean("userDao", UserDao.class);
        System.out.println(dao.getCount());
    }
}
