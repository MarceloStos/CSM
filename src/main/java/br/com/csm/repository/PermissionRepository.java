package br.com.csm.repository;

import br.com.csm.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    List<Permission> findByApplicationId(UUID applicationId);

    boolean existsByApplicationIdAndName(UUID applicationId, String name);
}
