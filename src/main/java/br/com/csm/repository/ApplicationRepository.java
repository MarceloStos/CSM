package br.com.csm.repository;

import br.com.csm.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    //Só para validar a unicidade
    boolean existsByClientId(String clientId);

    Optional<Application> findByClientId(String clientId);

    List<Application> findAllByDeactivatedAtIsNull();
}
