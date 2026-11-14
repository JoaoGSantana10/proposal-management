package dio.proposalmanagement.proposal.infrasctructure.http;

import dio.proposalmanagement.auth.domain.UserRole;
import dio.proposalmanagement.auth.infrastructure.persistense.entity.User;
import dio.proposalmanagement.proposal.application.CreateProposalUseCase;
import dio.proposalmanagement.proposal.application.ListProposalsUseCase;
import dio.proposalmanagement.proposal.application.list.AccessScope;
import dio.proposalmanagement.proposal.domain.Owner;
import dio.proposalmanagement.proposal.domain.OwnerId;
import dio.proposalmanagement.proposal.infrasctructure.http.request.CreateProposalRequest;
import dio.proposalmanagement.proposal.infrasctructure.response.ProposalResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proposals")
/**
 * Adaptador HTTP dos casos de uso de propostas.
 *
 * <p>Também traduz a role do usuário autenticado em um escopo de consulta.</p>
 */
public class ProposalController {
    private final CreateProposalUseCase createProposalUseCase;
    private final ListProposalsUseCase listProposalsUseCase;

    /**
     * Cria o controller com os casos de uso de criação e listagem.
     *
     * @param createProposalUseCase caso de uso de criação
     * @param listProposalsUseCase caso de uso de consulta
     */
    public ProposalController(CreateProposalUseCase createProposalUseCase,
                              ListProposalsUseCase listProposalsUseCase) {
        this.createProposalUseCase = createProposalUseCase;
        this.listProposalsUseCase = listProposalsUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('INFLUENCER')")
    /**
     * Cria uma proposta pertencente ao influenciador autenticado.
     *
     * @param request corpo da requisição
     * @param user usuário autenticado
     * @return proposta criada
     */
    public ProposalResponse createProposal(@RequestBody CreateProposalRequest request,
                                            @AuthenticationPrincipal User user) {
        var owner = new Owner(new OwnerId(user.getId()), user.getUsername());
        var output =this.createProposalUseCase.execute(request.toInput(), owner);
        return ProposalResponse.from(output);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('INFLUENCER', 'BRAND')")
    /**
     * Lista propostas no escopo permitido pela role do usuário.
     *
     * @param user usuário autenticado
     * @return propostas visíveis para o usuário
     */
    public List<ProposalResponse> findAllProposals(@AuthenticationPrincipal User user){
        var accessScope = getAccessScope(user.getRole());
        var ownerId = new OwnerId(user.getId());
        return listProposalsUseCase.execute(accessScope, ownerId)
                .stream()
                .map(ProposalResponse::from)
                .toList();
    }

    /**
     * Traduz a role de segurança em uma regra de visibilidade de propostas.
     *
     * @param role role do usuário
     * @return escopo correspondente
     */
    private static AccessScope getAccessScope(UserRole role){
        return switch (role) {
            case ROLE_INFLUENCER -> AccessScope.OWN;
            case ROLE_BRAND -> AccessScope.ALL;
        };
    }



}
