package org.example.DAO;

import org.example.Entity.AppUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUsersDAO extends JpaRepository<AppUsers, Long> {

    AppUsers findAppUsersByTelegramUserId(Long id);
}
