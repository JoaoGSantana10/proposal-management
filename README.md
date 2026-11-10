# proposal-management

 <div align="center">

# 🤝 Influencer & Brand Connect

### Gerenciamento de propostas com segurança baseada em papéis (RBAC)

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6.x-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Status](https://img.shields.io/badge/status-em_desenvolvimento-yellow?style=for-the-badge)

</div>

---

## 📖 Sobre o projeto

Uma **plataforma de Media Kits** onde influenciadores digitais expõem propostas e marcas realizam contratações diretas, tudo em um ambiente centralizado.

O grande desafio deste estudo de caso é **segurança**: garantir que cada perfil acesse **apenas as funcionalidades que lhe pertencem**, evitando acesso indevido a dados de terceiros. Para isso, o projeto evolui de uma API totalmente pública para um modelo **protegido por identidades digitais**, usando **Spring Security**.

## 🎯 Regras de negócio

- 👤 Existem dois tipos de usuário: **Influenciador** e **Marca (Brand)**.
- 📢 O **Influenciador** pode **criar propostas** na plataforma.
- 🏢 A **Marca** pode **visualizar todas as propostas** disponíveis no sistema, mas **não pode criar** propostas.
- 🔒 O Influenciador **só enxerga as próprias propostas**, nunca as de outros influenciadores.

## 🔐 Modelo de segurança (RBAC)

O acesso é controlado por **papéis (Role-Based Access Control)**:

| Ação                          | 🎤 Influenciador | 🏷️ Marca |
| ----------------------------- | :--------------: | :------: |
| Criar proposta (`POST`)       |        ✅        |    ❌    |
| Ver propostas públicas        |        ❌        |    ✅    |
| Ver as próprias propostas     |        ✅        |   N/A    |

### Perfis

- **Influencer** – pode postar propostas de mídia, mas é restrito de visualizar propostas de outros influenciadores.
- **Brand** – possui permissão para visualizar propostas públicas, porém não pode criar conteúdo como influenciador.

### Domínio e imutabilidade

- **UUIDs** como identificadores, garantindo integridade e rastreabilidade dos contratos gerados.
- **Java Records** para DTOs e objetos imutáveis.

## 🛠️ Tecnologias

- **Java** 21 *(ajuste para a sua versão)*
- **Spring Boot**
- **Spring Security**
- **Spring Web**
- **Spring Data JPA**
- **Banco de dados:** `<PostgreSQL | MySQL | H2>`
- **Gradle** *(ou Maven)*

## 🏗️ Estrutura do projeto

```text
src/main/java/com/seuprojeto
├── config/          # Configuração do Spring Security (SecurityFilterChain)
├── controller/      # Endpoints REST
├── dto/             # Java Records (requests/responses)
├── model/           # Entidades (User, Proposal, Role)
├── repository/      # Repositórios Spring Data
└── service/         # Regras de negócio
```

> 💡 Ajuste os pacotes conforme a estrutura real do seu projeto.

## 🔌 Endpoints

| Método | Rota                  | Descrição                          | Acesso        |
| :----: | --------------------- | ---------------------------------- | ------------- |
| `POST` | `/auth/register`      | Cadastra um novo usuário           | Público       |
| `POST` | `/auth/login`         | Autentica o usuário                | Público       |
| `POST` | `/proposals`          | Cria uma proposta                  | `INFLUENCER`  |
| `GET`  | `/proposals`          | Lista as propostas públicas        | `BRAND`       |
| `GET`  | `/proposals/me`       | Lista as próprias propostas        | `INFLUENCER`  |

> 💡 Substitua pelas rotas reais da sua API.

### Exemplo de requisição

```http
POST /proposals
Authorization: Bearer <seu_token>
Content-Type: application/json

{
  "title": "Campanha de verão",
  "description": "Pacote de 3 vídeos curtos + stories",
  "price": 1500.00
}
```

## 🚀 Como executar

### Pré-requisitos

- JDK 21+
- Git
- Banco de dados configurado

### Passo a passo

```bash
# 1. Clone o repositório
git clone https://github.com/JoaoGSantana10/<nome-do-repositorio>.git

# 2. Entre na pasta
cd <nome-do-repositorio>

# 3. Configure o application.properties (ou application.yml)

# 4. Execute a aplicação
./gradlew bootRun
```

A API ficará disponível em `http://localhost:8080`.

## ⚙️ Configuração

Exemplo de `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/seu_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

## 🧪 Testes

```bash
./gradlew test
```

## 🗺️ Roadmap

- [x] Modelagem de usuários e papéis
- [x] Controle de acesso por papéis (RBAC)
- [ ] Autenticação com JWT
- [ ] Contratação direta de propostas
- [ ] Testes de integração para as regras de acesso
- [ ] Documentação com Swagger/OpenAPI

## 👨‍💻 Autor

**João Guilherme Santana de Oliveira**
Estudante de Engenharia de Software na UnB (FGA)

[![GitHub](https://img.shields.io/badge/GitHub-JoaoGSantana10-181717?style=flat&logo=github)](https://github.com/JoaoGSantana10)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-João_Guilherme-0A66C2?style=flat&logo=linkedin)](https://linkedin.com/in/joão-guilherme-santana-de-oliveira-a73472416)

---

<div align="center">

Feito com ☕ e Spring Security

</div>
