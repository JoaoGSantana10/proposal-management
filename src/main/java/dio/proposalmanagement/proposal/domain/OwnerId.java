package dio.proposalmanagement.proposal.domain;

import java.util.UUID;

/**
 * Identificador tipado do proprietário de uma proposta.
 *
 * @param id UUID do usuário proprietário
 */
public record OwnerId(UUID id) {
}
