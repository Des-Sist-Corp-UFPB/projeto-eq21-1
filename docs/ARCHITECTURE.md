# Arquitetura do Sistema

## Visão Geral

```
Browser (React SPA)
  │
  │  Requisições REST (JSON) com JWT no header
  ▼
Controller (Spring MVC — @RestController)
  │  Recebe requests HTTP, valida DTOs, delega ao Service
  ▼
Service (@Transactional)
  │  Lógica de negócio, orquestra operações
  ▼
Repository (Spring Data JPA)
  │  Abstração do banco, queries automáticas
  ▼
PostgreSQL
```

## Padrão React SPA + REST API

O frontend é uma **Single Page Application** em React (Vite) que consome a API REST do backend.
A comunicação é feita exclusivamente via JSON, com autenticação JWT no header `Authorization`.

```
Browser (React)                 Backend (Spring Boot)
  │                               │
  │  POST /auth/login             │
  │──────────────────────────────►│
  │◄──────────────────────────────│  { token, email, role }
  │                               │
  │  GET /api/funkos              │
  │  Authorization: Bearer <jwt>  │
  │──────────────────────────────►│
  │◄──────────────────────────────│  Page<FunkoResponse>
```

**Vantagens desta abordagem**:
- Separação clara entre frontend e backend
- Frontend independente, facilmente hospedável em CDN
- JWT stateless: sem sessão no servidor

## Flyway: Gerenciamento de Schema

```
V1__criar_tabela_produto.sql  ← aplicado na 1ª inicialização
V2__criar_tabela_funko.sql    ← aplicado na sequência
```

**Regra de ouro**: Nunca edite uma migration já aplicada. Crie sempre uma nova.

## Camadas

### Controller
- Anotado com `@RestController`
- Recebe requisição HTTP
- Valida DTO com `@Valid`
- Chama Service
- Retorna JSON (ResponseEntity ou objeto serializado)
- NÃO contém lógica de negócio

### Service
- Anotado com `@Service` e `@Transactional`
- Contém toda a lógica de negócio
- Lança exceções de domínio (`FunkoNaoEncontradoException`)
- Usa Repository para persistência

### Repository
- Interface que estende `JpaRepository`
- Queries derivadas do nome do método (Spring Data)
- Para queries complexas: `@Query` com JPQL

### Domain (Entidade)
- Classe JPA mapeada para tabela do banco
- NÃO deve conter lógica de negócio complexa
- `@PrePersist`/`@PreUpdate` para timestamps automáticos

## Integrações Externas

| Integração | Finalidade | Configuração |
|-----------|-----------|-------------|
| MinIO / S3 | Armazenamento de imagens dos Funkos | `aws.s3.*` em application.yml |
| LLM via LiteLLM | Análise de imagem para pré-preencher cadastro | `openai.*` em application.yml |

## Auditoria (AOP)

Operações críticas em `FunkoService` e `AuthService` são interceptadas por `AuditoriaAspect`
e persistidas em `log_auditoria` com usuário, operação, status e timestamp.
