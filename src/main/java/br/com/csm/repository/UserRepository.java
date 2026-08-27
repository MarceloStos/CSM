package br.com.csm.repository;

import br.com.csm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByLogin(String login);
    Optional<User> findByEmail(String email);

    Optional<User> findByLoginAndStatusAndDeletedAtIsNull(String login, Integer status);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissions " +
            "WHERE u.login = :login " +
            "AND u.status = :status " +
            "AND u.deletedAt IS NULL " +
            "AND (u.blockedUntil IS NULL OR u.blockedUntil < :now)")
    Optional<User> findActiveAndUnblockedUserWithPermissions(
            @Param("login") String login,
            @Param("status") Integer status,
            @Param("now") OffsetDateTime now
    );

    List<User> findAllByDeletedAtIsNull();
}