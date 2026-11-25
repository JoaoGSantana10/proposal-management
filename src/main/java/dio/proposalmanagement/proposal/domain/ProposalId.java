package dio.proposalmanagement.proposal.domain;

import java.util.UUID;

/**
 * Identificador tipado de uma proposta.
 *
 * @param id UUID da proposta
 */
public record ProposalId(UUID id) {
    /**
     * Gera um novo identificador aleatório.
     */
    public ProposalId(){
        this(UUID.randomUUID());
    }
}
