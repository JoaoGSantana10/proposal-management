package dio.proposalmanagement.proposal.application.list;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
/**
 * Resolve a estratégia de consulta registrada para cada escopo.
 */
public class Factory {
    private final Map<AccessScope, Strategy> strategies;

    /**
     * Indexa as estratégias fornecidas pelo container Spring pelo escopo.
     *
     * @param strategies estratégias descobertas por injeção de dependência
     */
    public Factory(List<Strategy> strategies) {
        this.strategies = strategies.stream().
                collect
                        (Collectors.toMap(
                                Strategy::getScope,
                                Function.identity()));
    }

    /**
     * Obtém a estratégia correspondente ao escopo.
     *
     * @param scope escopo desejado
     * @return estratégia registrada para o escopo
     */
    public Strategy getStrategy(AccessScope scope) {
        return strategies.get(scope);
    }
}
