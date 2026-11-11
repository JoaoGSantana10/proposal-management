package dio.proposalmanagement.auth.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class RestUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // transforma o JSON em objeto Java, faz serialização e desserialização de objetos Java para JSON e vice-versa.
    private final ObjectMapper objectMapper;

    public RestUsernamePasswordAuthenticationFilter(AuthenticationConfiguration authenticationConfiguration,
                                                    ObjectMapper objectMapper) {
        /**
         * Pega o AuthenticationManager padrão do Spring Security e passa ele para o construtor do
         * UsernamePasswordAuthenticationFilter, que é a classe pai dessa classe.
         */
        super(authenticationConfiguration.getAuthenticationManager());
        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/api/auth/login"); // Define a URL de login personalizada
        setAuthenticationSuccessHandler((request, response, authentication) -> {
            response.setStatus(HttpServletResponse.SC_OK);
        });
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // Aqui você pode adicionar lógica personalizada para a autenticação
        // Por exemplo, você pode verificar se o usuário está bloqueado, se a senha está correta, etc.

        try {
            var loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            /**
             * Estamos falando que vamos criar um token que tem usuário e senha.
             * Que o Spring Security consegue manipular.
             */
            var token = UsernamePasswordAuthenticationToken.unauthenticated(
                    loginRequest.username(),
                    loginRequest.password());

                /**
                 * Ele vai tentar autenticar. Então ele vai verificar o usuário ver seu tenho no meu userDetailsService
                 * e vai pegar o password que veio, aplicar o password encoder que a gente definiu e validar se o password
                 * que ele tem registrado no userDetails faz o match com o password encodado que a gente mandou.
                */
            return getAuthenticationManager().authenticate(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public record LoginRequest(String username, String password) {

    }
}
