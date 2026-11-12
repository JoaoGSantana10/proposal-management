package dio.proposalmanagement.proposal.domain;

import org.springframework.stereotype.Repository;

import java.util.List;

public interface ProposalRepository {
    List<Proposal> findAll();
    List<Proposal> findByOwnerId(OwnerId ownerId);
    Proposal save(Proposal proposal);
}
