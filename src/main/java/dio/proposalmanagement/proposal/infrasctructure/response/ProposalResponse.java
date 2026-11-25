package dio.proposalmanagement.proposal.infrasctructure.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import dio.proposalmanagement.proposal.application.output.ProposalOutput;

@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Representação JSON devolvida pela API para uma proposta.
 *
 * @param id identificador da proposta
 * @param title título
 * @param description descrição, omitida quando ausente
 * @param owner proprietário da proposta
 */
public record ProposalResponse(String id, String title, String description, OwnerResponse owner) {
    /**
     * Proprietário aninhado na resposta HTTP.
     *
     * @param id identificador do proprietário
     * @param name nome do proprietário
     */
    public record OwnerResponse(String id, String name){

    }

    /**
     * Converte a saída da aplicação no formato exposto pela API.
     *
     * @param output saída produzida pelo caso de uso
     * @return resposta HTTP
     */
    public static ProposalResponse from(ProposalOutput output){
        return new ProposalResponse(
                output.id(),
                output.title(),
                output.description().orElse(null),
                new OwnerResponse(output.ownerId(), output.ownerName())
        );
    }
}
