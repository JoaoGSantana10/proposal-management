package dio.proposalmanagement.proposal.infrasctructure.http.request;

import dio.proposalmanagement.proposal.application.input.CreateProposalInput;

import java.util.Optional;

/**
 * Corpo HTTP aceito pelo endpoint de criação de propostas.
 *
 * @param title título informado pelo cliente
 * @param description descrição opcional
 */
public record CreateProposalRequest(String title, Optional<String> description) {
    /**
     * Converte o DTO HTTP em entrada da camada de aplicação.
     *
     * @return entrada do caso de uso
     */
    public CreateProposalInput toInput() {
        return new CreateProposalInput(title, description);
    }
}
