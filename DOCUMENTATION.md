# Documentação detalhada

## 1. Objetivo

O `proposal-management` é uma API Spring Boot que controla propostas criadas por influenciadores e consultadas por influenciadores ou marcas. A aplicação demonstra uma separação em camadas:

1. **Domínio**: modelos e contratos independentes da persistência.
2. **Aplicação**: casos de uso e seleção do escopo de consulta.
3. **Infraestrutura**: HTTP, entidades JPA, repositórios Spring Data e segurança Spring Security.

O ponto de entrada é `ProposalManagementApplication`, que inicializa o contexto Spring.

## 2. Mapa de pacotes

### `dio.proposalmanagement`

Contém a classe de inicialização da aplicação.

### `auth.domain`

Contém `UserRole`, enum que representa as roles aceitas pela aplicação: `ROLE_INFLUENCER` e `ROLE_BRAND`.

### `auth.infrastructure.persistense.entity`

Contém `User`, entidade JPA que também implementa `UserDetails`. Ela representa o usuário no banco e fornece ao Spring Security o nome de usuário, senha, role e estado de autenticação.

### `auth.infrastructure.persistense.repository`

Contém `UserRepository`, abstração Spring Data para buscar usuários por UUID ou username.

### `auth.infrastructure.security`

Contém:

- `SecurityConfig`: cadeia de filtros, encoder BCrypt, autorização por endpoint e carga inicial de usuários;
- `JpaUserDetailsSerivce`: adaptação entre `UserRepository` e `UserDetailsService`;
- `RestUsernamePasswordAuthenticationFilter`: leitura do JSON de login e delegação da autenticação ao `AuthenticationManager`.

### `auth.infrastructure.http`

Contém `Controller`, controlador demonstrativo com endpoints raiz e endpoints protegidos por role.

### `proposal.domain`

Contém o núcleo do domínio:

- `Proposal`: entidade de negócio com título, descrição, proprietário e identificador;
- `ProposalId` e `OwnerId`: value objects baseados em UUID;
- `Owner`: dados do proprietário;
- `ProposalRepository`: contrato de persistência usado pelos casos de uso.

### `proposal.application`

Contém `CreateProposalUseCase` e `ListProposalsUseCase`, que orquestram as operações de criação e consulta sem conhecer detalhes do banco ou do HTTP.

### `proposal.application.input` e `proposal.application.output`

Contêm os objetos de entrada e saída da aplicação. `CreateProposalInput` converte dados de entrada para o domínio; `ProposalOutput` converte o domínio para uma saída estável para a camada HTTP.

### `proposal.application.list`

Implementa o padrão Strategy:

- `AccessScope` define `OWN` e `ALL`;
- `Strategy` define o contrato de consulta;
- `OwnStrategy` busca por proprietário;
- `AllStrategy` busca todas as propostas;
- `Factory` indexa as estratégias por escopo e devolve a implementação correta.

### `proposal.infrasctructure.http`

Contém `ProposalController`, responsável pelos endpoints `/proposals`, pela autorização baseada em role e pela montagem do proprietário a partir do usuário autenticado.

### `proposal.infrasctructure.http.request`

Contém `CreateProposalRequest`, representação do JSON recebido no endpoint de criação.

### `proposal.infrasctructure.response`

Contém `ProposalResponse`, representação JSON enviada ao cliente, incluindo o objeto aninhado `OwnerResponse`.

### `proposal.infrasctructure.persistence.entity`

Contém `ProposalEntity`, modelo JPA da tabela de propostas e conversores entre entidade e domínio.

### `proposal.infrasctructure.persistence.repository`

Contém:

- `ProposalEntityRepository`: repositório Spring Data para `ProposalEntity`;
- `JpaProposalRepository`: adaptador que implementa o contrato de domínio `ProposalRepository`.

## 3. Fluxo de autenticação

1. O cliente envia username e password para `POST /api/auth/login`.
2. `RestUsernamePasswordAuthenticationFilter` desserializa o corpo JSON em `LoginRequest`.
3. O filtro cria um `UsernamePasswordAuthenticationToken` não autenticado.
4. O `AuthenticationManager` consulta o `JpaUserDetailsSerivce`.
5. O serviço busca o usuário por username em `UserRepository`.
6. O Spring Security compara a senha recebida com o hash BCrypt armazenado.
7. `User.getAuthorities()` transforma a role em `SimpleGrantedAuthority`.
8. Em caso de sucesso, o filtro responde HTTP 200.

## 4. Fluxo de propostas

### Criação

`ProposalController.createProposal` recebe `CreateProposalRequest` e o usuário autenticado. O usuário é convertido em `Owner`; o request vira `CreateProposalInput`; `CreateProposalUseCase` cria um `Proposal`; `JpaProposalRepository` converte o domínio para `ProposalEntity`, salva via Spring Data e reconverte o resultado. Por fim, `ProposalResponse.from` monta o JSON de resposta.

### Consulta

`ProposalController.findAllProposals` converte a role do usuário em um `AccessScope`. Influenciadores usam `OWN`, marcas usam `ALL`. `ListProposalsUseCase` pede à `Factory` a estratégia correspondente, executa a consulta e converte cada `Proposal` em `ProposalOutput` e depois em `ProposalResponse`.

## 5. Modelo de dados

### Usuário

`User` possui UUID, username único, senha e role. A role é armazenada como texto por `@Enumerated(EnumType.STRING)`. O método `getAuthorities` expõe a própria constante `ROLE_INFLUENCER` ou `ROLE_BRAND` ao Spring Security.

### Proposta

`ProposalEntity` possui UUID, título obrigatório, descrição opcional, UUID do proprietário e nome do proprietário. A camada de domínio encapsula os UUIDs em `ProposalId` e `OwnerId`, enquanto a entidade mantém tipos simples adequados ao JPA.

## 6. Configuração

`build.gradle` define Java 25, Spring Boot, Spring Web, Spring Security, Spring Data JPA, Lombok e o driver MySQL. `compose.yml` cria o banco local `proposals` e publica a porta interna 3306 como 3307. `application.properties` define o nome da aplicação, habilita SQL no log e configura criação do schema.

## 7. Segurança e ambiente

Todas as requisições exigem autenticação, exceto `/api/auth/login`. A autorização de método é habilitada por `@EnableMethodSecurity`; os endpoints de proposta usam `@PreAuthorize`. O CSRF está desabilitado para o fluxo REST atual.

As credenciais e usuários seed são adequados apenas para desenvolvimento. Para produção, devem ser substituídos por segredos externos, migrações de schema, política de sessão/token definida e tratamento explícito de falhas de autenticação.

## 8. Testes

`ProposalManagementApplicationTests` verifica se o contexto Spring consegue ser carregado. O comando recomendado é:

```bash
./gradlew test
```

## 9. Pontos de evolução

- adicionar testes unitários dos casos de uso, estratégias e conversores;
- adicionar testes de integração dos endpoints e da autenticação;
- introduzir validação de payload e respostas de erro padronizadas;
- substituir `ddl-auto=create` por migrações;
- externalizar credenciais e usuários seed;
- documentar a API com OpenAPI/Swagger;
- decidir entre sessão e token para clientes REST;
- corrigir gradualmente as grafias dos pacotes, com refatoração coordenada.
