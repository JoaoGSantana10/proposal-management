package dio.proposalmanagement.proposal.application;

import dio.proposalmanagement.proposal.application.input.CreateProposalInput;
import dio.proposalmanagement.proposal.application.output.ProposalOutput;
import dio.proposalmanagement.proposal.domain.Owner;
import dio.proposalmanagement.proposal.domain.ProposalRepository;
import org.springframework.stereotype.Service;

@Service
/**
 * Caso de uso responsável por criar e persistir propostas.
 */
public class CreateProposalUseCase {
    private final ProposalRepository proposalRepository;

    /**
     * Cria o caso de uso com sua porta de persistência.
     *
     * @param proposalRepository repositório de domínio
     */
    public CreateProposalUseCase (ProposalRepository proposalRepository) {
        this.proposalRepository = proposalRepository;
    }
    /**
     * Constrói a proposta, salva-a e devolve sua projeção de saída.
     *
     * @param input dados recebidos pela aplicação
     * @param owner proprietário autenticado
     * @return resultado da criação
     */
    public ProposalOutput execute(CreateProposalInput input, Owner owner) {
        var proposal = input.toDomain(owner);
        var saved = proposalRepository.save(proposal);

        return ProposalOutput.from(saved);
    }
}
