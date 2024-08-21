package com.unemployed.coreconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unemployed.coreconnect.model.entity.User;


@Repository
public interface  UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    
    User findByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.username = :username OR u.email = :email")
    boolean existsByUsernameOrEmail(String username, String email);
}
