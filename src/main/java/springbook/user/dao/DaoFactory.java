package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

//    // 생성자를 통한 DI
//    @Bean
//    public UserDao userDao() {
//        return new UserDao(connectionMaker());
//    }
     //수정자를 통한 DI
    @Bean
    public UserDao userDao() {
        UserDao dao = new UserDao();
//        dao.setConnectionMaker(connectionMaker());
        dao.setDataSource(dataSource());
        return dao;
    }

//    @Bean
//    public ConnectionMaker connectionMaker() {
//        return new DConnectionMaker();
//    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(org.h2.Driver.class);
        dataSource.setUrl("jdbc:h2:tcp://localhost:9092/~/toby");
        dataSource.setUsername("hlion17");
        dataSource.setPassword("1234");

        return dataSource;
    }
}
