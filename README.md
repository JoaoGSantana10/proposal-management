# Proposal Management

Aplicação REST para autenticação de usuários e gerenciamento de propostas entre influenciadores e marcas. O projeto usa Java 25, Spring Boot, Spring Security, Spring Data JPA e MySQL.

## Funcionalidades atuais

- autenticação por usuário e senha em `POST /api/auth/login`;
- usuários com as roles `INFLUENCER` e `BRAND`;
- criação de propostas por influenciadores;
- listagem das próprias propostas para influenciadores;
- listagem de todas as propostas para marcas;
- persistência de usuários e propostas em MySQL;
- criação inicial de três usuários de desenvolvimento quando a tabela de usuários está vazia.

## Tecnologias e requisitos

- Java 25;
- Gradle Wrapper (`./gradlew`);
- Docker e Docker Compose, para o MySQL local;
- Spring Boot 4.1.2;
- MySQL 9.6.

## Executando localmente

Na raiz do repositório, suba o banco e inicie o projeto:

```bash
docker compose up -d
./gradlew bootRun
```

Para executar os testes:

```bash
./gradlew test
```

O Compose publica o MySQL na porta `3307` do host e usa o banco `proposals`, usuário `app` e senha `app`. A aplicação usa `spring.jpa.hibernate.ddl-auto=create`, portanto o schema é recriado ao iniciar conforme a configuração atual.

## Usuários de desenvolvimento

Quando não existem usuários, a aplicação cria:

| Usuário | Senha | Role |
|---|---|---|
| `fitness_vibe` | `password` | `INFLUENCER` |
| `tech_guru` | `password` | `INFLUENCER` |
| `logistics` | `password` | `BRAND` |

Essas credenciais são apenas para desenvolvimento e não devem ser usadas em produção.

## API

### Login

`POST /api/auth/login`

```json
{
  "username": "fitness_vibe",
  "password": "password"
}
```

O filtro REST lê o JSON, autentica com o `UserDetailsService` baseado em JPA e retorna HTTP 200 quando a autenticação é bem-sucedida. A aplicação mantém a autenticação no contexto de segurança conforme a configuração atual.

### Criar proposta

`POST /proposals`

Requer a role `INFLUENCER`.

```json
{
  "title": "Campanha de verão",
  "description": "Divulgação de produto nas redes sociais"
}
```

O usuário autenticado é usado como proprietário da proposta. A resposta contém o identificador, título, descrição e dados do proprietário.

### Listar propostas

`GET /proposals`

Requer `INFLUENCER` ou `BRAND`. O escopo é definido pela role:

- `INFLUENCER`: retorna somente propostas do usuário autenticado;
- `BRAND`: retorna todas as propostas.

## Organização do código

```text
src/main/java/dio/proposalmanagement
├── auth
│   ├── domain
│   └── infrastructure
└── proposal
    ├── application
    ├── domain
    └── infrasctructure
compose.yml
build.gradle
DOCUMENTATION.md
```

`auth` concentra usuários, roles e segurança. `proposal` separa domínio, casos de uso e adaptadores HTTP/persistência. A explicação completa de cada pacote, classe e fluxo está em [`DOCUMENTATION.md`](DOCUMENTATION.md).

## Configuração e limitações atuais

- `application.properties` habilita a exibição de SQL e recria as tabelas ao iniciar;
- as credenciais do banco estão declaradas no `compose.yml` para ambiente local;
- não há ainda migrações de banco, tratamento de erros HTTP específico ou documentação OpenAPI;
- o endpoint de login não retorna token; ele usa o mecanismo de autenticação/sessão configurado pelo Spring Security;
- os nomes dos pacotes `infrasctructure` e `persistense` mantêm a grafia existente no código.

Consulte [`DOCUMENTATION.md`](DOCUMENTATION.md) para detalhes de arquitetura e implementação.
