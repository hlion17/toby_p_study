package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class AccountDao implements ConnectionMaker {

    private ConnectionMaker connectionMaker;

    public AccountDao(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    public void add() {

    }

    public void get() {

    }

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        return null;
    }
}
