package br.com.csm.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    boolean existsByApplicationIdAndName(UUID applicationId, String name);

    List<Role> findByStatusNot(int status);

};