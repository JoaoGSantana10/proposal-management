package dio.proposalmanagement.proposal.application.list;

import dio.proposalmanagement.proposal.domain.OwnerId;
import dio.proposalmanagement.proposal.domain.Proposal;

import java.util.List;

/**
 * Estratégia de consulta de propostas para um escopo específico.
 */
public interface Strategy {
    /**
     * Consulta propostas usando o proprietário quando aplicável.
     *
     * @param ownerId proprietário autenticado
     * @return propostas encontradas
     */
    List<Proposal> getProposals(OwnerId ownerId);
    /**
     * Informa o escopo atendido por esta estratégia.
     *
     * @return escopo da estratégia
     */
    AccessScope getScope();
}
