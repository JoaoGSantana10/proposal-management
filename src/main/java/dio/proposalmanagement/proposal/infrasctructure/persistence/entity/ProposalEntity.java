package dio.proposalmanagement.proposal.infrasctructure.persistence.entity;

import dio.proposalmanagement.proposal.domain.Owner;
import dio.proposalmanagement.proposal.domain.OwnerId;
import dio.proposalmanagement.proposal.domain.Proposal;
import dio.proposalmanagement.proposal.domain.ProposalId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * Modelo JPA da proposta armazenado no banco.
 *
 * <p>Os métodos de fábrica isolam a conversão entre tipos simples persistíveis
 * e os value objects do domínio.</p>
 */
public class ProposalEntity { // Representação da nossa tabela do banco de dados
    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false)
    private String ownerName;

    /**
     * Converte a entidade de domínio para o modelo persistível.
     *
     * @param proposal proposta de domínio
     * @return entidade pronta para o Spring Data
     */
    public static ProposalEntity from(Proposal proposal){
        return new ProposalEntity(
                proposal.getId().id(),
                proposal.getTitle(),
                proposal.getDescription().orElse(null),
                proposal.getOwner().id().id(),
                proposal.getOwner().name()
        );
    }

    /**
     * Reconstitui a proposta de domínio a partir dos dados persistidos.
     *
     * @return proposta de domínio
     */
    public Proposal toDomain() {
        return new Proposal(
                new ProposalId(this.id),
                this.title,
                Optional.ofNullable(this.description),
                new Owner(new OwnerId(this.ownerId), this.ownerName)
        );
    }


}
