package dio.proposalmanagement.auth.infrastructure.http;

import dio.proposalmanagement.auth.infrastructure.persistense.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Authenticator;

@RestController
@RequestMapping
public class Controller {

    @GetMapping
    /**
     * O Spring disponibiliza uma anotação chamada @AuthenticationPrincipal que vai pegar o usuário autenticado e
     * injetar ele no parâmetro do método.
     */
    public String hello(@AuthenticationPrincipal User user){
        return "Hello World " + user.getId();
    }

    @GetMapping("/influencer")
    // Eu posso falar que o usuário atenticado precisa ter uma role específica para acessar esse endpoint.
    @PreAuthorize("hasRole('INFLUENCER')")
    public String influencerEndpoint(){
        return "Hello INFLUENCER";
    }

    @GetMapping("/brand")
    @PreAuthorize("hasRole('BRAND')")
    public String brandEndpoint(){
        return "Hello BRAND";
    }
}
