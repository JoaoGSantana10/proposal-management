package dio.proposalmanagement.proposal.application.input;

import dio.proposalmanagement.proposal.domain.Owner;
import dio.proposalmanagement.proposal.domain.Proposal;

import java.util.Optional;

/**
 * Dados de aplicação necessários para criar uma proposta.
 *
 * @param title título informado pelo cliente
 * @param description descrição opcional
 */
public record CreateProposalInput(String title, Optional<String> description) {
    /**
     * Converte a entrada em uma entidade de domínio associada ao proprietário.
     *
     * @param owner proprietário autenticado
     * @return nova proposta
     */
    public Proposal toDomain(Owner owner) {
        return new Proposal(title, description, owner);
    }
}
