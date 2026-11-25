package dio.proposalmanagement.proposal.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
/**
 * Entidade de negócio que representa uma proposta e seu proprietário.
 */
public class Proposal {
    private ProposalId id; // Identificador fortemente tipado para a proposta
    private String title;
    private Optional<String> description;
    private Owner owner;

    /**
     * Cria uma proposta nova com um identificador gerado.
     *
     * @param title título da proposta
     * @param description descrição opcional
     * @param owner proprietário da proposta
     */
    public Proposal(String title, Optional<String> description, Owner owner){
        this.id = new ProposalId();
        this.title = title;
        this.description = description;
        this.owner = owner;
    }
}
