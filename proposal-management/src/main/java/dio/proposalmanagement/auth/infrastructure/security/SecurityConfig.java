package dio.proposalmanagement.auth.infrastructure.security;

import dio.proposalmanagement.auth.domain.UserRole;
import dio.proposalmanagement.auth.infrastructure.persistense.entity.User;
import dio.proposalmanagement.auth.infrastructure.persistense.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration // O Spring Boot vai procurar todas as classes anotadas com 'Configuration' e vai iniciar elas primeiro.
@EnableWebSecurity // Fala para a aplicação Spring que vai utilizar o Spring Security para proteger a aplicação.
@EnableMethodSecurity /** permite que eu coloque algumas anotações em alguns métodos e dependendo da minha role,
dependendo de alguns parâmetros que eu posso passar para essas anotações, eu consigo liberar ou não o acesso a esse
método. */

public class SecurityConfig {

    // PEP: Todas as nossas chamadas todas as requisições HTTP vão passar por esse filtro de segurança.
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            RestUsernamePasswordAuthenticationFilter restUsernamePasswordAuthenticationFilter) throws Exception{
        /**
         * Está falando para a aplicação que todas as requisições precisam de
         * autenticação e que vamos utilizar o formulário de login padrão do Spring Security.
         */
        http
                .csrf(AbstractHttpConfigurer:: disable)

                // Sempre salva a sessão do usuário.
                .securityContext( context -> context.requireExplicitSave(false))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll() // configura a URL que definimos no nosso filtro.
                        .anyRequest().authenticated())

                .addFilterAt(restUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

               // .formLogin(Customizer.withDefaults());

        return http.build();
    }
    /**
     * O authenticate provider ele procura uma classe Details Service, para fazer a consulta de username e validar o
     * password, por enquanto a gente vai criar ela em memória eventualmente vamos mudar isso para buscar no banco de
     * dados.
     */

    /*
    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder){
        // O Spring vai definir qual a segurança de armazenamento de senha
        UserDetails influencer = User.withUsername("influencer")
                .password(passwordEncoder.encode("password"))
                .roles("INFLUENCER")
                .build();

        UserDetails brand = User.withUsername("brand")
                .password(passwordEncoder.encode("password"))
                .roles("BRAND")
                .build();
        return new InMemoryUserDetailsManager(influencer, brand);
    }
     */

    @Bean
    PasswordEncoder passwordEncoder(){
        // Define o algoritmo de criptografia que vai ser utilizado para criptografar a senha.
        return new BCryptPasswordEncoder();
    }

    // Quando a aplicação iniciar, vai verificar se o banco de dados está vazio, se estiver vazio ele vai criar os usuários.
    @Bean
    CommandLineRunner initDatabase(UserRepository repository, PasswordEncoder passwordEncoder){
        return args -> {
            if (repository.count() == 0) {
                User fitnessInfluencer = new User();
                fitnessInfluencer.setUsername("fitness_vibe");
                fitnessInfluencer.setPassword(passwordEncoder.encode("password"));
                fitnessInfluencer.setRole(UserRole.ROLE_INFLUENCER);

                User techInfluencer = new User();
                techInfluencer.setUsername("tech_guru");
                techInfluencer.setPassword(passwordEncoder.encode("password"));
                techInfluencer.setRole(UserRole.ROLE_INFLUENCER);

                User brand = new User();
                brand.setUsername("logistics");
                brand.setPassword(passwordEncoder.encode("password"));
                brand.setRole(UserRole.ROLE_BRAND);

                repository.saveAll(List.of(fitnessInfluencer, techInfluencer, brand));
            }
        };
    }

}
