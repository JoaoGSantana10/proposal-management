package dio.proposalmanagement.auth.infrastructure.security;

import dio.proposalmanagement.auth.infrastructure.persistense.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Para o Spring reconhecer a classe como um serviço e fazer a injeção de dependência
public class JpaUserDetailsSerivce implements UserDetailsService {
    private final UserRepository userRepository; // Injeção de dependência do UserRepository

    public JpaUserDetailsSerivce (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                // Lança uma exceção se o usuário não for encontrado
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
