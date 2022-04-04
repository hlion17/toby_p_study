package springbook.user.service;

import springbook.user.domain.User;

import java.sql.SQLException;

public interface UserService {
    void add(User user);

    void upgradeLevels();
}
