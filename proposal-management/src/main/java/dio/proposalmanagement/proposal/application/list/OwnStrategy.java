package dio.proposalmanagement.proposal.application.list;

import dio.proposalmanagement.proposal.domain.OwnerId;
import dio.proposalmanagement.proposal.domain.Proposal;
import dio.proposalmanagement.proposal.domain.ProposalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Estratégia que restringe a consulta às propostas do usuário autenticado.
 */
public class OwnStrategy implements Strategy {
    private final ProposalRepository proposalRepository;

    /**
     * Cria a estratégia com o repositório de propostas.
     *
     * @param proposalRepository repositório de domínio
     */
    public OwnStrategy(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    @Override
    /**
     * Busca as propostas do proprietário informado.
     *
     * @param ownerId proprietário autenticado
     * @return propostas pertencentes ao proprietário
     */
    public List<Proposal> getProposals(OwnerId ownerId) {
        return proposalRepository.findByOwnerId(ownerId);
    }

    @Override
    /**
     * @return {@link AccessScope#OWN}
     */
    public AccessScope getScope() {
        return AccessScope.OWN;
    }
}
