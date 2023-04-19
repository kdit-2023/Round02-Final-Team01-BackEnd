package shop.donutmarket.donut.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import shop.donutmarket.donut.domain.user.model.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long>{
    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
}
