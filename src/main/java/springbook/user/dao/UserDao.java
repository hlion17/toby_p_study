package springbook.user.dao;

import com.mysql.cj.x.protobuf.MysqlxPrepare;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDao {

    // 매번 새로운 값으로 바뀌는 정보를 담은 인스턴스 변수
    // 심각한 문제가 발생한다.
//    private Connection c;
//    private User user;
    // 초기에 설정하면 사용 중에는 바뀌지 않는 읽기전용 인스턴스 변수
    // final 을 붙혀준다

    // 생성자를 통한 DI
//    private final ConnectionMaker connectionMaker;

    // 수정자를 통한 DI
//    private ConnectionMaker connectionMaker;
//
//    public void setConnectionMaker(ConnectionMaker connectionMaker) {
//        this.connectionMaker = connectionMaker;
//    }

    // 수정자를 통한 DI, DataSource 인터페이스 사용
    private DataSource dataSource;
//    private JdbcContext jdbcContext;
    private JdbcTemplate jdbcTemplate;

    // 수정자를 통해 jdbcContext 에 사용할 dataSource 의존성 주입 받음
    public void setDataSource(DataSource dataSource) {
//        this.jdbcContext = new JdbcContext();  // 의존성 주입 받을 때 객체를 생성하고
//        this.jdbcContext.setDataSource(dataSource);  // 의존성 주입
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.dataSource = dataSource;  // 아직 jdbcContext 적용하지 않는 메서드 떄문에 남겨둠

    }

    // 생성자
    // 수정자를 통한 DI 일 때
    public UserDao() {};

    // 생성자를 통한 DI일 때
//    public UserDao(ConnectionMaker connectionMaker) {
//        this.connectionMaker = connectionMaker;
//    }
// --------------


    // 수정자를 통한 jdbcContext DI 주입
//    public void setJdbcContext(JdbcContext jdbcContext) {
//        this.jdbcContext = jdbcContext;
//    }


    // DB 접근 메서드
    public void add(final User user) throws ClassNotFoundException, SQLException {
        // JdbcContext 사용
//        this.jdbcContext.workWithStatementStrategy(
//                new StatementStrategy() {
//                    @Override
//                    public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//                        PreparedStatement ps = c.prepareStatement(
//                                "insert into users(id, name, password) values(?,?,?)");
//                        ps.setString(1, user.getId());
//                        ps.setString(2, user.getName());
//                        ps.setString(3, user.getPassword());
//                        return ps;
//                    }
//                }
//        );

        // JdbcTemplate 사용
        this.jdbcTemplate.update("insert into users(id, name, password) values (?, ?, ?)",
                user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
//
//        Connection c = dataSource.getConnection();
//
//        PreparedStatement ps = c.prepareStatement(
//                "select * from users where id = ?");
//        ps.setString(1, id);
//
//        ResultSet rs = ps.executeQuery();
//
//        User user = null;
//        if (rs.next()) {
//            user = new User();
//            user.setId(rs.getString("id"));
//            user.setName(rs.getString("name"));
//            user.setPassword(rs.getString("password"));
//        }
//
//        rs.close();
//        ps.close();
//        c.close();
//
//        // 조회 데이터가 없으면 EmptyResultDataAccessException 예외를 발생시킴
//        if (user == null) throw new EmptyResultDataAccessException(1);
//
//        return user;

        return this.jdbcTemplate.queryForObject("select * from users where id = ?"
                , new Object[] {id}  // SQL에 바인딩할 파라미터 값. 가변인자 대신 배열을 사용
                ,new RowMapper<User>() {  // ResultSet 한 로우의 결과를 오브젝트에 매핑해주는 콜백(RowMapper)
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                return user;
            }
        });
    }

    // 전략패턴
    // 사용하는 Client 가 됨
    public void deleteAll() throws SQLException {
        // JdbcContext 사용
//        this.jdbcContext.executeSql("delete from users");

        // JdbcTemplate 사용
//        this.jdbcTemplate.update(
//                new PreparedStatementCreator() {
//                    @Override
//                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                        return con.prepareStatement("delete from users");
//                    }
//                }
//        );

        // JdbcTemplate 사용
        this.jdbcTemplate.update("delete from users");
    }



    // 메서드 추출
    private PreparedStatement makeStatement(Connection c) throws SQLException {
        return c.prepareStatement("delete from users; ");
    }

    // 전략패턴
    // 변하지 않는 부분 (context)
    // 변하는 부분은 인터페이스를 통해 간접적 의존한다. (strategy)
    public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;

        try {
            c = dataSource.getConnection();

            ps = stmt.makePreparedStatement(c);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (c != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    public int getCount() throws SQLException {
//        Connection c = null;
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//
//        try {
//            c = dataSource.getConnection();
//
//            ps = c.prepareStatement("SELECT count(*) FROM users; ");
//
//            rs = ps.executeQuery();
//            rs.next();
//            return rs.getInt(1);
//        } catch (SQLException e) {
//            throw e;
//        } finally {
//            try {
//                if (rs != null) rs.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            try {
//                if (ps != null) ps.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//            try {
//                if (c != null) c.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }

        // JdbcTemplate 콜백 함수 이용
//        return this.jdbcTemplate.query(new PreparedStatementCreator() {
//                                           @Override
//                                           public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                                               return con.prepareStatement("select count(*) from users");
//                                           }
//                                       }
//                , new ResultSetExtractor<Integer>() {
//                    @Override
//                    public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
//                        rs.next();
//                        return rs.getInt(1);
//                    }
//                });

        // JdbcTemplate queryForInt 메서드 사용
        return this.jdbcTemplate.queryForInt("select count(*) from users");

    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id",
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        User user = new User();
                        user.setId(rs.getString("id"));
                        user.setName(rs.getString("name"));
                        user.setPassword(rs.getString("password"));
                        return user;
                    }
                });
    }
}
