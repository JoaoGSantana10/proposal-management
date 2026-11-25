package dio.proposalmanagement.proposal.infrasctructure.persistence.repository;

import dio.proposalmanagement.proposal.domain.OwnerId;
import dio.proposalmanagement.proposal.domain.Proposal;
import dio.proposalmanagement.proposal.domain.ProposalRepository;
import dio.proposalmanagement.proposal.infrasctructure.persistence.entity.ProposalEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.StreamSupport;

@Repository
/**
 * Adaptador JPA que implementa a porta de repositório do domínio.
 */
public class JpaProposalRepository implements ProposalRepository {
    private final ProposalEntityRepository proposalEntityRepository;

    /**
     * Cria o adaptador com o repositório Spring Data.
     *
     * @param proposalEntityRepository repositório de entidades
     */
    public JpaProposalRepository(ProposalEntityRepository proposalEntityRepository) {
        this.proposalEntityRepository = proposalEntityRepository;
    }

    @Override
    /**
     * Consulta todas as entidades e as reconstrói como propostas de domínio.
     *
     * @return propostas persistidas
     */
    public List<Proposal> findAll() {
        var iterable = proposalEntityRepository.findAll();
        return StreamSupport
                .stream(iterable.spliterator(), false)
                .map(ProposalEntity::toDomain)
                .toList();
    }

    @Override
    /**
     * Consulta por proprietário e converte os resultados para o domínio.
     *
     * @param ownerId identificador tipado do proprietário
     * @return propostas do proprietário
     */
    public List<Proposal> findByOwnerId(OwnerId ownerId) {
        return proposalEntityRepository.findAllByOwnerId(ownerId.id())
                .stream()
                .map(ProposalEntity::toDomain)
                .toList();
    }

    @Override
    /**
     * Converte e salva uma proposta.
     *
     * @param proposal proposta de domínio
     * @return proposta reconstituída a partir da entidade salva
     */
    public Proposal save(Proposal proposal) {
        var entity = ProposalEntity.from(proposal);
        var saved = proposalEntityRepository.save(entity);
        return saved.toDomain();
    }
}
