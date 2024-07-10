package org.example.DAO;

import org.example.Entity.AppUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUsersDAO extends JpaRepository<AppUsers, Long> {
    Optional<AppUsers> findByTelegramUserId(Long id);
    Optional<AppUsers> findById(Long id);
    Optional<AppUsers> findByEmail(String email);

}
