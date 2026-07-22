# Memória do Projeto — Mercado DSC/UFPB

## Identidade do Projeto
- **Nome**: Trokets Biscuit — Funko Pop e-commerce
- **Disciplina**: Desenvolvimento de Sistemas Corporativos
- **Professor**: Rodrigo Rebouças
- **Instituição**: Universidade Federal da Paraíba — Campus IV
- **Equipe**: Equipe 21

## Stack Técnica
| Camada | Tecnologia | Versão |
|--------|-----------|--------|
| Linguagem | Java | 21 |
| Framework | Spring Boot | 3.4.5 |
| Build | Maven | 3.9+ |
| Frontend | React + Vite | 18.3.1 + 5.4.1 |
| Estilos | Bootstrap | 5.3.3 |
| Banco | PostgreSQL | 16 |
| Migrations | Flyway | 11.x |
| Segurança | Spring Security + JWT | 6.x |
| Armazenamento | MinIO / AWS S3 | SDK v2 |
| IA (visão) | Spring AI + LiteLLM | 1.0.0 |
| Testes | JUnit 5 + Testcontainers | - |

## Estrutura de Pacotes
```
br.ufpb.dsc.mercado
├── config/          # Configurações Spring (Security, Web, etc.)
├── controller/      # Controllers MVC (recebem requests HTTP)
├── domain/          # Entidades JPA (mapeamento objeto-relacional)
├── dto/             # Data Transfer Objects (Records Java)
├── exception/       # Exceções de domínio
├── repository/      # Interfaces Spring Data JPA
└── service/         # Lógica de negócio (@Transactional)
```

## Comandos Essenciais

### Desenvolvimento
```bash
# Subir ambiente completo (banco + app + adminer)
docker compose -f docker/docker-compose.dev.yml up

# Só o banco (para rodar a app localmente com mvn)
docker compose -f docker/docker-compose.dev.yml up postgres adminer

# Rodar aplicação local (perfil dev)
mvn spring-boot:run

# Rodar testes (requer Docker para Testcontainers)
mvn test
```

### Build e Verificações
```bash
# Build sem testes
mvn clean package -DskipTests

# Build com testes
mvn clean verify

# SAST: SpotBugs + FindSecBugs + OWASP Dependency Check
mvn verify -Psecurity

# Verificar dependências desatualizadas
mvn versions:display-dependency-updates -Pversions

# Trivy local (scan filesystem)
docker compose -f docker/docker-compose.dev.yml --profile scan up trivy

# Trivy scan da imagem (depois de fazer o build)
docker build -f docker/Dockerfile -t mercado:latest .
docker run --rm aquasec/trivy image mercado:latest
```

### Produção
```bash
# Build imagem de produção
docker build -f docker/Dockerfile -t mercado:latest .

# Subir produção (requer .env configurado)
docker compose -f docker/docker-compose.prod.yml up -d
```

## Acesso Local
- **App**: http://localhost:8080
- **Login**: admin / admin123
- **Adminer (DB UI)**: http://localhost:8888
- **Health Check**: http://localhost:8080/actuator/health

## Decisões Arquiteturais

### Por que React SPA em vez de Thymeleaf/HTMX?
React separa claramente frontend e backend. O backend expõe uma API REST pura (JSON), o frontend é hospedável em CDN. JWT stateless elimina a necessidade de sessão no servidor.

### Por que Flyway para migrations?
Controle versionado do schema do banco. Cada alteração no banco deve ser uma migration nova (nunca editar migrations já aplicadas). Garante rastreabilidade e reversibilidade.

### Por que UserDetailsService com banco de dados?
Autenticação persistida no PostgreSQL via `Usuario` entity. Senhas armazenadas com BCrypt. JWT gerado com `JwtService` e validado por `JwtAuthFilter` em cada requisição.

### Por que perfil 'security' separado?
SpotBugs e OWASP Dependency-Check são lentos. Separar em perfil permite que o build do dia-a-dia seja rápido, rodando segurança no CI.

## Convenções de Código
- Nomes em português no domínio (entidades, métodos de negócio)
- Endpoints REST em português
- Comentários em português
- Commits no padrão Conventional Commits: `feat:`, `fix:`, `docs:`, `refactor:`
- Records Java para DTOs (imutáveis por padrão)
- `@Transactional(readOnly = true)` em métodos de consulta

## Ferramentas de Segurança
| Ferramenta | Escopo | Comando |
|------------|--------|---------|
| SpotBugs + FindSecBugs | SAST bytecode Java | `mvn verify -Psecurity` |
| Semgrep | SAST código-fonte | `semgrep --config=auto src/` |
| Trivy (fs) | Vulnerabilidades em libs | docker compose `--profile scan` |
| Trivy (image) | Vulnerabilidades na imagem Docker | `trivy image mercado:latest` |
| OWASP Dependency-Check | CVEs em dependências | `mvn verify -Psecurity` |

## Acesso Local
- **App**: http://localhost:8121
- **Login padrão**: definido em `.env` (ADMIN_EMAIL / ADMIN_SENHA)
- **Adminer (DB UI)**: http://localhost:8888
- **Health Check**: http://localhost:8121/actuator/health
