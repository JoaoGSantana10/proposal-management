package dio.proposalmanagement.proposal.application.output;

import dio.proposalmanagement.proposal.domain.Proposal;

import java.util.Optional;

/**
 * Saída da aplicação independente do modelo JPA e do formato HTTP.
 *
 * @param id identificador textual da proposta
 * @param title título
 * @param description descrição opcional
 * @param ownerId identificador textual do proprietário
 * @param ownerName nome do proprietário
 */
public record ProposalOutput(String id, String title, Optional<String> description, String ownerId, String ownerName) {
    /**
     * Projeta uma proposta de domínio para a saída da aplicação.
     *
     * @param proposal proposta de domínio
     * @return saída pronta para o adaptador HTTP
     */
    public static ProposalOutput from(Proposal proposal){
        return new ProposalOutput(
                proposal.getId().id().toString(),
                proposal.getTitle(),
                proposal.getDescription(),
                proposal.getOwner().id().toString(),
                proposal.getOwner().name());
    }
}
