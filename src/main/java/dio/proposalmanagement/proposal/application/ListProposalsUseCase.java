package dio.proposalmanagement.proposal.application;

import dio.proposalmanagement.proposal.application.list.AccessScope;
import dio.proposalmanagement.proposal.application.list.Factory;
import dio.proposalmanagement.proposal.application.output.ProposalOutput;
import dio.proposalmanagement.proposal.domain.OwnerId;
import dio.proposalmanagement.proposal.domain.Proposal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Caso de uso que lista propostas conforme o escopo solicitado.
 */
public class ListProposalsUseCase {
    private final Factory factory;

    /**
     * Cria o caso de uso com a fábrica de estratégias de consulta.
     *
     * @param factory fábrica que resolve OWN ou ALL
     */
    public ListProposalsUseCase(Factory factory) {
        this.factory = factory;
    }

    /**
     * Seleciona uma estratégia, consulta o repositório e projeta os resultados.
     *
     * @param scope escopo de acesso
     * @param ownerId usuário autenticado, usado no escopo OWN
     * @return resultados da consulta
     */
    public List<ProposalOutput> execute(AccessScope scope, OwnerId ownerId) {
        var proposals = factory.getStrategy(scope).getProposals(ownerId);

        return proposals.stream().map(ProposalOutput::from).toList();
    }
}
