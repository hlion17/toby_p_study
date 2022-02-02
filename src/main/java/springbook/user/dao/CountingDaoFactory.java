package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CountingDaoFactory {

    // 생성자 DI 주입방식
//    @Bean
//    public UserDaoJdbc userDao() {
//        return new UserDaoJdbc(connectionMaker());
//    }

    @Bean
    public UserDaoJdbc userDao() {
        UserDaoJdbc dao = new UserDaoJdbc();
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
