package dio.proposalmanagement.auth.infrastructure.persistense.entity;

import dio.proposalmanagement.auth.domain.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity // reprenseta uma tabela no banco de dados, então o Spring vai criar uma tabela no banco de dados com o nome da classe.
@Data
@NoArgsConstructor // cria um construtor sem argumentos.
@AllArgsConstructor // cria um construtor com todos os argumentos.
public class User implements UserDetails {
    @Id // adiciona a anotação de id, que é a chave primária da tabela.
    @GeneratedValue(strategy = GenerationType.UUID) // gera um id único para cada usuário.
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // salva o valor do enum como string no banco de dados.
    private UserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
