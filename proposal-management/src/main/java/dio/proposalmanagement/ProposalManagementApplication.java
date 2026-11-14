package dio.proposalmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/**
 * Ponto de entrada da aplicação Spring Boot.
 *
 * <p>A anotação habilita a configuração automática, a varredura de componentes
 * e a inicialização do servidor HTTP embutido.</p>
 */
public class ProposalManagementApplication {

    /**
     * Inicializa o contexto Spring e a aplicação web.
     *
     * @param args argumentos recebidos pela JVM
     */
    public static void main(String[] args) {
        SpringApplication.run(ProposalManagementApplication.class, args);
    }

}
