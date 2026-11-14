package dio.proposalmanagement.proposal.application.list;

import dio.proposalmanagement.proposal.domain.OwnerId;
import dio.proposalmanagement.proposal.domain.Proposal;
import dio.proposalmanagement.proposal.domain.ProposalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Estratégia que retorna todas as propostas persistidas.
 */
public class AllStrategy implements Strategy {
    private final ProposalRepository proposalRepository;

    /**
     * Cria a estratégia com o repositório de propostas.
     *
     * @param proposalRepository repositório de domínio
     */
    public AllStrategy(ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }

    @Override
    /**
     * Busca todas as propostas; o ownerId não restringe este escopo.
     *
     * @param ownerId proprietário autenticado, mantido pelo contrato comum
     * @return todas as propostas
     */
    public List<Proposal> getProposals(OwnerId ownerId) {
        return proposalRepository.findAll();
    }

    @Override
    /**
     * @return {@link AccessScope#ALL}
     */
    public AccessScope getScope() {
        return AccessScope.ALL;
    }
}
