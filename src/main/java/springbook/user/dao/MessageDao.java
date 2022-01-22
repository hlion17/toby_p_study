package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class MessageDao implements ConnectionMaker {

    private ConnectionMaker connectionMaker;

    public MessageDao(ConnectionMaker connectionMaker) {
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
