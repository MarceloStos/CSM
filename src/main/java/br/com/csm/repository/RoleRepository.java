package br.com.csm.repository;

import br.com.csm.model.Role;
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
public interface RoleRepository extends JpaRepository<Role, UUID> {

    boolean existsByApplicationIdAndName(UUID applicationId, String name);

    List<Role> findByStatusNot(int status);

};