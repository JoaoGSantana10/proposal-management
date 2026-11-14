package dio.proposalmanagement.proposal.infrasctructure.persistence.repository;

import dio.proposalmanagement.proposal.infrasctructure.persistence.entity.ProposalEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;


/**
 * Repositório Spring Data para a tabela de propostas.
 */
public interface ProposalEntityRepository extends CrudRepository<ProposalEntity, UUID> {
    /**
     * Busca entidades pelo UUID do proprietário.
     *
     * @param ownerId UUID do proprietário
     * @return entidades pertencentes ao usuário
     */
    List<ProposalEntity> findAllByOwnerId(UUID ownerId);
}
