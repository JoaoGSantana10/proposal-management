package dio.proposalmanagement.auth.infrastructure.security;

import dio.proposalmanagement.auth.infrastructure.persistense.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Para o Spring reconhecer a classe como um serviço e fazer a injeção de dependência
/**
 * Adapta o repositório JPA ao contrato de carregamento do Spring Security.
 */
public class JpaUserDetailsSerivce implements UserDetailsService {
    private final UserRepository userRepository; // Injeção de dependência do UserRepository

    /**
     * Cria o serviço com a porta de consulta de usuários.
     *
     * @param userRepository repositório usado para localizar o usuário
     */
    public JpaUserDetailsSerivce (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    /**
     * Carrega o principal pelo username informado no login.
     *
     * @param username nome procurado
     * @return usuário que implementa {@link UserDetails}
     * @throws UsernameNotFoundException quando o username não existe
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                // Lança uma exceção se o usuário não for encontrado
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
