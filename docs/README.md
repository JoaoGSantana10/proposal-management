# Documentação do Proposal Management

Este documento descreve a estrutura e o comportamento implementados atualmente no
projeto. Os nomes de pacotes abaixo preservam a grafia existente no código,
inclusive `persistense` e `infrasctructure`.

## 1. Visão geral

O Proposal Management é uma API REST em Java com Spring Boot para autenticar
usuários e gerenciar propostas. Há dois papéis de usuário:

- `ROLE_INFLUENCER`: pode criar propostas e consultar somente as próprias;
- `ROLE_BRAND`: pode consultar todas as propostas.

A aplicação separa o fluxo HTTP, os casos de uso, o domínio e a persistência.
Assim, os casos de uso dependem do contrato `ProposalRepository`, e não
diretamente da implementação JPA.

### Tecnologias

- Java 25 e Gradle Wrapper 9.8.2;
- Spring Boot 4.1.2;
- Spring Web para os endpoints REST;
- Spring Security para autenticação e autorização;
- Spring Data JPA e Hibernate para persistência;
- MySQL 9.6 como banco de dados local;
- Lombok para gerar métodos comuns de classes Java;
- JUnit 5 e Spring Boot Test para testes.

## 2. Estrutura de pastas e pacotes

```text
src/main/java/dio/proposalmanagement/
├── ProposalManagementApplication.java
├── auth/
│   ├── domain/
│   │   └── UserRole.java
│   └── infrastructure/
│       ├── http/
│       │   └── Controller.java
│       ├── persistense/
│       │   ├── entity/User.java
│       │   └── repository/UserRepository.java
│       └── security/
│           ├── JpaUserDetailsSerivce.java
│           ├── RestUsernamePasswordAuthenticationFilter.java
│           └── SecurityConfig.java
└── proposal/
    ├── application/
    │   ├── CreateProposalUseCase.java
    │   ├── ListProposalsUseCase.java
    │   ├── input/CreateProposalInput.java
    │   ├── list/
    │   │   ├── AccessScope.java
    │   │   ├── AllStrategy.java
    │   │   ├── Factory.java
    │   │   ├── OwnStrategy.java
    │   │   └── Strategy.java
    │   └── output/ProposalOutput.java
    ├── domain/
    │   ├── Owner.java
    │   ├── OwnerId.java
    │   ├── Proposal.java
    │   ├── ProposalId.java
    │   └── ProposalRepository.java
    └── infrasctructure/
        ├── http/ProposalController.java
        ├── http/request/CreateProposalRequest.java
        ├── persistence/entity/ProposalEntity.java
        ├── persistence/repository/
        │   ├── JpaProposalRepository.java
        │   └── ProposalEntityRepository.java
        └── response/ProposalResponse.java
```

### Responsabilidade das camadas

- **Domínio (`domain`)**: representa conceitos e regras centrais, como proposta,
  proprietário e contrato de persistência.
- **Aplicação (`application`)**: coordena operações de negócio e converte os
  dados entre entrada, domínio e saída.
- **Infraestrutura (`infrastructure` / `infrasctructure`)**: integra o domínio
  com HTTP, banco de dados e Spring Security.

## 3. Autenticação e usuários

### `auth.domain.UserRole`

Enum com os papéis aceitos: `ROLE_INFLUENCER` e `ROLE_BRAND`. O valor do enum é
persistido como texto e convertido para uma authority do Spring Security.
`hasRole("INFLUENCER")` corresponde à authority `ROLE_INFLUENCER`; por isso,
as constantes incluem o prefixo `ROLE_`.

### `auth.infrastructure.persistense.entity.User`

Entidade de usuário e principal usado pelo Spring Security. Guarda UUID,
username, senha codificada e papel. Implementa `UserDetails`; `getAuthorities()`
converte o papel do usuário em um `SimpleGrantedAuthority`.

As anotações de persistência e Lombok nesta classe estão detalhadas na seção
[Anotações](#7-anotações-e-recursos-do-framework).

### `auth.infrastructure.persistense.repository.UserRepository`

Interface baseada em `CrudRepository<User, UUID>`. Spring Data implementa as
operações CRUD a partir da interface. O método
`findByUsername(String username)` é uma consulta derivada: o Spring Data
reconhece o nome do método e busca pelo campo `username`. O resultado é
`Optional<User>` porque pode não existir usuário com esse nome.

### `auth.infrastructure.security.JpaUserDetailsSerivce`

Implementa `UserDetailsService`, contrato que o Spring Security usa para
carregar um usuário pelo identificador de login. Consulta
`UserRepository.findByUsername`; se não encontra o usuário, lança
`UsernameNotFoundException`.

### `auth.infrastructure.security.RestUsernamePasswordAuthenticationFilter`

Especializa `UsernamePasswordAuthenticationFilter` para receber credenciais em
JSON, em vez de depender do formulário padrão. O endereço configurado é
`POST /api/auth/login`. A classe interna `LoginRequest` define os campos
`username` e `password` esperados no corpo.

O filtro desserializa a requisição com `ObjectMapper`, cria um token ainda não
autenticado e delega a verificação ao `AuthenticationManager`. Em sucesso,
configura a resposta como HTTP 200, sem produzir token JWT ou corpo JSON. Erros
de leitura do JSON são convertidos em `RuntimeException` no código atual.

### `auth.infrastructure.security.SecurityConfig`

Centraliza os beans de segurança:

- `securityFilterChain`: desabilita CSRF, permite `/api/auth/login`, exige
  autenticação nas demais requisições e posiciona o filtro REST no ponto do
  `UsernamePasswordAuthenticationFilter`;
- `passwordEncoder`: fornece `BCryptPasswordEncoder` para codificar e comparar
  senhas;
- `initDatabase`: se o repositório não contém usuários, cria dois influenciadores
  e uma marca de desenvolvimento, codificando as senhas antes de persistir.

O contexto de segurança está configurado com
`requireExplicitSave(false)`. A configuração não declara um JWT; o login
continua sujeito ao mecanismo de contexto/sessão do Spring Security.

### `auth.infrastructure.http.Controller`

Controller auxiliar de autenticação, distinto do controller de propostas.
Expõe a rota raiz `GET /`, que retorna uma saudação com o UUID do usuário
autenticado, além de `GET /influencer` e `GET /brand`, protegidas por role.
Esses endpoints demonstram o uso de principal autenticado e autorização por
método.

## 4. Domínio de propostas

### `proposal.domain.Proposal`

Entidade de domínio com `ProposalId`, título, descrição opcional e `Owner`.
O construtor usado na criação recebe título, descrição e proprietário e gera um
novo identificador. Outro construtor, gerado pelo Lombok, permite reconstituir
o objeto com um identificador já existente ao carregar dados persistidos.

### `proposal.domain.ProposalId` e `proposal.domain.OwnerId`

Records que encapsulam UUIDs para distinguir identificadores de propostas e de
proprietários nos tipos da aplicação. `ProposalId` também oferece um construtor
sem argumentos que gera um UUID aleatório.

### `proposal.domain.Owner`

Record com o identificador tipado (`OwnerId`) e o nome do proprietário. O
controller constrói esse valor a partir do usuário autenticado.

### `proposal.domain.ProposalRepository`

Contrato de persistência do domínio, com operações para listar todas as
propostas, listar por proprietário e salvar uma proposta. Os casos de uso usam
essa interface; `JpaProposalRepository` fornece a implementação concreta.

## 5. Casos de uso e estratégias de listagem

### Criação

`CreateProposalUseCase.execute` recebe um `CreateProposalInput` e um `Owner`,
converte a entrada para `Proposal`, salva pelo contrato `ProposalRepository` e
retorna um `ProposalOutput`.

`CreateProposalInput` é um record com título e descrição opcional. Seu método
`toDomain(Owner)` associa os dados da requisição ao proprietário autenticado.

### Listagem

`ListProposalsUseCase.execute` recebe um `AccessScope` e um `OwnerId`. Obtém a
estratégia correspondente por `Factory`, consulta as propostas e converte cada
resultado para `ProposalOutput`.

- `AccessScope` enumera `OWN` e `ALL`.
- `Strategy` define os métodos para consultar propostas e informar o escopo que
  a implementação atende.
- `OwnStrategy` chama `findByOwnerId`, limitando a consulta às propostas do
  usuário informado.
- `AllStrategy` chama `findAll`; neste escopo, o argumento `OwnerId` não é
  usado para filtrar.
- `Factory` recebe todas as implementações `Strategy` registradas no Spring e
  monta um mapa indexado pelo escopo. `getStrategy` devolve a implementação
  correspondente.

`ProposalOutput` é um record de saída da aplicação. Mantém identificadores em
formato textual e descrição como `Optional`, sem depender de tipos JPA ou do
formato JSON do controller.

## 6. HTTP e persistência de propostas

### `proposal.infrasctructure.http.ProposalController`

Define `POST /proposals` e `GET /proposals`.

- `POST /proposals` exige `ROLE_INFLUENCER`. Converte o usuário autenticado em
  `Owner`, transforma o corpo em entrada de aplicação e chama o caso de uso de
  criação.
- `GET /proposals` aceita `ROLE_INFLUENCER` ou `ROLE_BRAND`. A role é convertida
  para `OWN` ou `ALL`, respectivamente, antes de chamar o caso de uso de
  listagem.

### `CreateProposalRequest` e `ProposalResponse`

`CreateProposalRequest` representa o JSON recebido na criação e converte os
campos para `CreateProposalInput`.

`ProposalResponse` representa o JSON enviado ao cliente. Seu record interno
`OwnerResponse` estrutura os dados do proprietário. A conversão transforma a
descrição opcional em valor nulo quando ausente; a política de inclusão JSON
omite esse campo nulo.

### `ProposalEntity`

Entidade JPA para a tabela de propostas. Armazena UUIDs e campos simples:
identificador, título, descrição, UUID do proprietário e nome do proprietário.
`from(Proposal)` converte do domínio para persistência; `toDomain()` reconstrói
os value objects e o `Optional` ao ler do banco.

### Repositórios JPA

- `ProposalEntityRepository` estende `CrudRepository` e declara
  `findAllByOwnerId(UUID)`, outra consulta derivada pelo Spring Data.
- `JpaProposalRepository` implementa `ProposalRepository`, delega as operações
  ao repositório Spring Data e converte entidades para objetos de domínio. Para
  `findAll`, adapta o `Iterable` retornado pelo CRUD repository a um stream.

## 7. Anotações e recursos do framework

### Inicialização e injeção de dependências

- `@SpringBootApplication`: ativa configuração automática do Spring Boot,
  varredura de componentes e configuração da aplicação.
- `@Configuration`: indica uma classe de configuração Spring.
- `@Bean`: registra como bean o objeto retornado pelo método.
- `@Component`, `@Service` e `@Repository`: registram classes como componentes
  detectados pelo Spring. `@Service` marca serviços/casos de uso; `@Repository`
  marca adaptadores de persistência; `@Component` é um estereótipo genérico.
  Também habilitam a injeção por construtor usada no projeto.

### HTTP e autorização

- `@RestController`: declara um controller cujos retornos são serializados na
  resposta HTTP.
- `@RequestMapping`: define um caminho base para as rotas do controller.
- `@GetMapping` e `@PostMapping`: associam métodos a requisições GET e POST.
- `@RequestBody`: converte o corpo HTTP para o tipo Java do parâmetro.
- `@AuthenticationPrincipal`: injeta o principal autenticado no método.
- `@PreAuthorize`: aplica uma regra de autorização antes da execução do método.
  É habilitada pela configuração `@EnableMethodSecurity`.
- `@EnableWebSecurity`: ativa a integração de configuração web do Spring
  Security.
- `@EnableMethodSecurity`: permite aplicar segurança em métodos, como
  `@PreAuthorize`.

### Persistência

- `@Entity`: identifica uma classe como entidade JPA persistida.
- `@Id`: marca a chave primária.
- `@GeneratedValue(strategy = GenerationType.UUID)`: pede à persistência a
  geração do UUID do usuário.
- `@Column(unique = true, nullable = false)`: configura unicidade e
  obrigatoriedade da coluna username.
- `@Column(nullable = false)`: declara colunas obrigatórias no modelo relacional.
- `@Enumerated(EnumType.STRING)`: salva o nome do enum como texto, em vez do
  índice numérico.

### JSON

- `@JsonInclude(JsonInclude.Include.NON_NULL)`: exclui da serialização os campos
  com valor `null`; é usada em `ProposalResponse` para omitir descrição ausente.

### Lombok e Java

- `@Data`: gera getters, setters, `toString`, `equals` e `hashCode`.
- `@Getter`: gera getters para os campos.
- `@NoArgsConstructor`: gera construtor sem argumentos, necessário para a
  instanciação de entidades JPA.
- `@AllArgsConstructor`: gera construtor com todos os campos.
- `@Override`: indica que o método implementa ou substitui uma declaração de
  supertipo; o compilador verifica essa correspondência.
- `record`: recurso da linguagem Java usado para transportar dados imutáveis,
  como requests, responses, IDs e DTOs.

### Testes

- `@SpringBootTest`: inicializa o contexto da aplicação para teste de
  integração.
- `@Test`: identifica um método de teste JUnit.

## 8. Fluxos da API

### Login

1. O cliente envia `username` e `password` em `POST /api/auth/login`.
2. O filtro REST lê o JSON e cria um token de autenticação não autenticado.
3. O `AuthenticationManager` solicita o usuário a `JpaUserDetailsSerivce`.
4. O serviço carrega o usuário pelo `UserRepository`.
5. O Spring Security compara a senha recebida com o hash BCrypt armazenado.
6. Em caso de sucesso, a resposta configurada é HTTP 200.

Exemplo de corpo:

```json
{
  "username": "fitness_vibe",
  "password": "password"
}
```

### Criar proposta

1. O cliente autenticado envia `POST /proposals`.
2. `@PreAuthorize` exige o papel de influenciador.
3. O controller obtém o usuário autenticado e cria o `Owner`.
4. Request, input e domínio são convertidos em sequência.
5. O adaptador JPA salva a entidade e devolve o domínio reconstruído.
6. O controller converte a saída da aplicação em `ProposalResponse`.

Exemplo de corpo:

```json
{
  "title": "Campanha de verão",
  "description": "Divulgação de produto nas redes sociais"
}
```

### Listar propostas

1. O cliente autenticado envia `GET /proposals`.
2. `@PreAuthorize` permite influenciadores e marcas.
3. O controller converte a role em `OWN` ou `ALL`.
4. A fábrica escolhe `OwnStrategy` ou `AllStrategy`.
5. O adaptador consulta e converte os resultados para respostas HTTP.

## 9. Configuração e execução local

O `compose.yml` inicia MySQL 9.6, cria o banco `proposals`, usuário `app` e
publica a porta `3306` do container como `3307` no host. Os dados ficam no volume
Docker `data`.

`application.properties` define o nome da aplicação, habilita o log SQL e usa
`spring.jpa.hibernate.ddl-auto=create`. Essa opção recria o schema ao iniciar,
portanto os dados das tabelas não devem ser considerados persistentes entre
inicializações.

Na raiz do projeto:

```bash
docker compose up -d
./gradlew bootRun
```

Para executar os testes:

```bash
./gradlew test
```

Os usuários seed (`fitness_vibe`, `tech_guru` e `logistics`) usam a senha
`password` e são apenas para desenvolvimento. As credenciais do Compose e os
usuários de exemplo não são apropriados para produção.

## 10. Testes e limites atuais

O teste existente, `ProposalManagementApplicationTests.contextLoads`, verifica
se o contexto Spring consegue iniciar. Ainda não há testes unitários dos casos de
uso, testes de autorização ou testes de ponta a ponta dos endpoints.

Outros comportamentos a considerar ao evoluir o projeto:

- os requests não usam validação declarativa, como `@Valid` e restrições
  `@NotBlank`;
- não há tratamento de erro HTTP padronizado nem documentação OpenAPI;
- o login não devolve token JWT;
- o schema é gerenciado por `ddl-auto=create`, sem migrações;
- credenciais e usuários iniciais estão definidos para desenvolvimento;
- a grafia dos pacotes `persistense` e `infrasctructure` é mantida para refletir
  o código atual.

Esses pontos descrevem o estado observado do repositório, não garantias para
ambientes de produção.
