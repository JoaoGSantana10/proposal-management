package dio.proposalmanagement.proposal.domain;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Porta de persistência do agregado de propostas.
 *
 * <p>Os casos de uso dependem deste contrato, não da implementação JPA.</p>
 */
public interface ProposalRepository {
    /**
     * Busca todas as propostas.
     *
     * @return propostas persistidas
     */
    List<Proposal> findAll();
    /**
     * Busca propostas pertencentes a um usuário.
     *
     * @param ownerId identificador do proprietário
     * @return propostas do proprietário
     */
    List<Proposal> findByOwnerId(OwnerId ownerId);
    /**
     * Persiste uma proposta.
     *
     * @param proposal proposta de domínio
     * @return proposta persistida
     */
    Proposal save(Proposal proposal);
}
