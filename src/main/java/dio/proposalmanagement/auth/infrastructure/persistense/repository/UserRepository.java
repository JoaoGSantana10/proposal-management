package dio.proposalmanagement.auth.infrastructure.persistense.repository;

import dio.proposalmanagement.auth.infrastructure.persistense.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
/**
 * Porta de persistência de usuários baseada em Spring Data.
 */
public interface UserRepository extends CrudRepository<User, UUID> {
    /**
     * Busca um usuário pelo nome usado no login.
     *
     * @param username nome de usuário
     * @return usuário encontrado ou {@link Optional#empty()}
     */
    Optional<User> findByUsername(String username);
}
