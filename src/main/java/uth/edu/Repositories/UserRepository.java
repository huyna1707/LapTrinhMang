package uth.edu.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uth.edu.Models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // thêm dòng này
}