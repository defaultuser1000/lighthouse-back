package ru.zakrzhevskiy.lighthouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.zakrzhevskiy.lighthouse.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserById(Long id);
}
