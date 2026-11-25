package dio.proposalmanagement.proposal.domain;

/**
 * Valor do domínio com a identidade e o nome exibido do proprietário.
 *
 * @param id identificador tipado do proprietário
 * @param name nome usado na resposta da proposta
 */
public record Owner(OwnerId id, String name) {
}
