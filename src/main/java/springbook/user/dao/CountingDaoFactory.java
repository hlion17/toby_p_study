package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CountingDaoFactory {

    // 생성자 DI 주입방식
//    @Bean
//    public UserDao userDao() {
//        return new UserDao(connectionMaker());
//    }

    @Bean
    public UserDao userDao() {
        UserDao dao = new UserDao();
//        dao.setConnectionMaker(connectionMaker());
        return dao;
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new CountingConnectionMaker(realConnectionMaker());
    }

    @Bean
    public ConnectionMaker realConnectionMaker() {
        return new DConnectionMaker();
    }


}
