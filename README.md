# Trokets Biscuit — Funko Pop E-Commerce 🧸🎮

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot 3.4.5](https://img.shields.io/badge/Spring%20Boot-3.4.5-green.svg)](https://spring.io/projects/spring-boot)
[![React 18](https://img.shields.io/badge/React-18.3.1-61dafb.svg)](https://react.dev/)
[![Vite 5](https://img.shields.io/badge/Vite-5.4.1-646cff.svg)](https://vitejs.dev/)
[![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Flyway 11](https://img.shields.io/badge/Flyway-11-red.svg)](https://flywaydb.org/)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0-brightgreen.svg)](https://spring.io/projects/spring-ai)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Sistema corporativo de e-commerce e catálogo para colecionáveis **Funko Pop**, desenvolvido para a disciplina de **Desenvolvimento de Sistemas Corporativos (DSC)** da **Universidade Federal da Paraíba (UFPB - Campus IV)**.

---

## 📌 Informações Acadêmicas

- **Instituição**: Universidade Federal da Paraíba (UFPB) — Campus IV (Rio Tinto / Mamanguape)
- **Disciplina**: Desenvolvimento de Sistemas Corporativos (DSC)
- **Professor**: Rodrigo Rebouças
- **Equipe**: Equipe 21 (`eq21`)
- **Projeto**: Trokets Biscuit — E-commerce & Catálogo Inteligente de Funko Pops

---

## 🚀 Visão Geral do Sistema

O **Trokets Biscuit** é uma plataforma corporativa completa que combina uma arquitetura moderna **React SPA + Spring Boot REST API** com recursos avançados de Inteligência Artificial, Armazenamento de Objetos (S3) e Auditoria Automatizada.

### 🌟 Diferenciais e Destaques

- **🧠 Visão Computacional com Spring AI (GPT-4o)**: Ao enviar a foto de um Funko Pop, o sistema utiliza IA multimodal via Spring AI / LiteLLM para identificar automaticamente o personagem, a franquia e sugerir um preço de mercado.
- **📦 Armazenamento em Object Storage S3 / MinIO**: As fotos enviadas pelos usuários são salvas em bucket compatível com S3 (MinIO no dev, S3 em prod), gerando URLs públicas acessíveis.
- **🛡️ Segurança JWT & Controle de Acesso (RBAC)**: Autenticação stateless baseada em tokens JWT com suporte aos perfis `ROLE_ADMIN` e `ROLE_USER` e senhas criptografadas via BCrypt.
- **📜 Log de Auditoria Automático via Spring AOP**: Interceptação não-invasiva de operações críticas (`CRIAR`, `ATUALIZAR`, `EXCLUIR`, `LOGIN`, `REGISTRAR`), persistida em transações isoladas (`REQUIRES_NEW`) para registrar falhas mesmo em caso de rollback.
- **⚡ Frontend SPA Reativo (React + Vite + Bootstrap)**: Interface web responsiva, rápida e interativa para gerenciamento e navegação do catálogo.
- **🧪 Cobertura de Testes >90% e Testcontainers**: Testes automatizados com banco PostgreSQL real gerenciado via Testcontainers e relatórios JaCoCo.
- **🔍 Análise Estática de Segurança (SAST) e CI/CD**: Verificações automatizadas com SpotBugs, FindSecBugs, OWASP Dependency Check e scanner Trivy integrados ao GitHub Actions.

---

## 🏗️ Arquitetura do Sistema

O projeto adota uma arquitetura descentralizada com separação clara entre **Frontend (React SPA)** e **Backend (Spring Boot REST API)**:

```
┌─────────────────────────────────────────────────────────┐
│                 Navegador Web / Client                  │
│               React 18 SPA + Vite (Porta 8121)          │
└────────────────────────────┬────────────────────────────┘
                             │
                             │ Requisições HTTP REST (JSON)
                             │ + Authorization: Bearer <JWT>
                             ▼
┌─────────────────────────────────────────────────────────┐
│                 Spring Boot 3.4.5 Backend               │
│                                                         │
│  ┌───────────────────┐       ┌───────────────────────┐  │
│  │   Controllers     │ ───►  │     Spring Security   │  │
│  │ (@RestController) │       │   JWT Auth Filter     │  │
│  └─────────┬─────────┘       └───────────────────────┘  │
│            │                                            │
│            ▼                                            │
│  ┌───────────────────┐       ┌───────────────────────┐  │
│  │  Services (@Trans)│ ───►  │   AuditoriaAspect     │  │
│  │ Lógica de Negócio │       │     (Spring AOP)      │  │
│  └────┬────┬────┬────┘       └───────────────────────┘  │
└───────┼────┼────┼───────────────────────────────────────┘
        │    │    │
  ┌─────┘    │    └─────────────┐
  ▼          ▼                  ▼
┌─────────┐ ┌───────────────┐ ┌───────────────────────────┐
│PostgreSQL│ │MinIO / S3    │ │Spring AI + LiteLLM        │
│(Flyway) │ │Upload Imagens │ │(Análise de Foto GPT-4o)   │
└─────────┘ └───────────────┘ └───────────────────────────┘
```

---

## 🛠️ Stack Tecnológica

| Camada / Componente | Tecnologia | Versão | Descrição |
| :--- | :--- | :--- | :--- |
| **Linguagem Backend** | Java | 21 | Recursos modernos (Records, Pattern Matching) |
| **Framework Principal** | Spring Boot | 3.4.5 | Web, Data JPA, Security, Actuator, AOP |
| **Frontend SPA** | React + Vite | 18.3.1 / 5.4.1 | UI dinâmica e reativa em JavaScript/JSX |
| **Estilização** | Bootstrap | 5.3.3 | Framework CSS para layout responsivo |
| **Banco de Dados** | PostgreSQL | 16-alpine | Banco relacional corporativo |
| **Migrations** | Flyway | 11.x | Versionamento automatizado de DDL/DML |
| **Object Storage** | MinIO / AWS S3 | AWS SDK v2 (2.31) | Armazenamento e entrega de imagens |
| **Inteligência Artificial** | Spring AI + LiteLLM | 1.0.0 | Integração com modelo GPT-4o de visão |
| **Segurança & JWT** | Spring Security + JJWT | 6.x / 0.12.6 | Autenticação stateless e RBAC |
| **Gerenciamento Build** | Apache Maven | 3.9+ | Gerenciamento de dependências e lifecycles |
| **Testes Integration** | JUnit 5 + Testcontainers | 1.20.4 | Testes de integração em containers Docker |
| **Cobertura de Código** | JaCoCo | 0.8.12 | Análise e relatórios de cobertura de testes |
| **Segurança (SAST)** | SpotBugs / OWASP / Trivy | Latest | Análise de vulnerabilidade e CVEs |
| **Containerização** | Docker & Docker Compose | 27+ | Ambientes isolados para Dev e Produção |
| **CI/CD** | GitHub Actions | Workflows | Integrado com GHCR e deploy automático SSH |

---

## 📁 Estrutura do Projeto

```
projeto-eq21-1/
├── .github/workflows/
│   └── deploy.yml               # Pipeline de CI/CD (Testes, SAST, Build Docker, Deploy SSH)
├── docker/
│   ├── Dockerfile               # Dockerfile multi-stage de produção
│   ├── Dockerfile.dev           # Dockerfile otimizado para desenvolvimento (hot-reload)
│   ├── docker-compose.dev.yml   # Compose local (Postgres, MinIO, App, pgAdmin, Trivy)
│   └── docker-compose.prod.yml  # Compose de produção
├── docs/
│   ├── ARCHITECTURE.md          # Detalhes e diagramas de arquitetura
│   ├── CONVENTIONS.md           # Padronizações de código e commits
│   ├── SECURITY.md              # Diretrizes de segurança e SAST
│   ├── opentelemetry.md         # Guia de métricas e observabilidade
│   └── opentelemetry-logs.md    # Estrutura de logs distribuídos
├── frontend/
│   ├── public/                  # Arquivos estáticos do frontend
│   ├── src/                     # Código fonte React (Componentes, Páginas, Serviços)
│   ├── index.html               # Entry point HTML da SPA
│   ├── package.json             # Dependências React/Vite/Bootstrap
│   └── vite.config.js           # Configuração de build do Vite
├── src/main/java/br/ufpb/dsc/mercado/
│   ├── config/                  # Configurações (Security, S3Client, BucketInitializer, etc.)
│   ├── controller/              # Endpoints REST (Auth, Funko, Upload, Ping, Spa)
│   ├── domain/                  # Entidades JPA (Funko, Usuario, LogAuditoria, Role)
│   ├── dto/                     # Data Transfer Objects imutáveis (Records Java)
│   ├── exception/               # Handler global de exceções e exceções de domínio
│   ├── repository/              # Spring Data JPA Repositories
│   └── service/                 # Serviços de negócio, Visão IA, Upload e Auditoria
├── src/main/resources/
│   ├── db/migration/            # Scripts Flyway (V1, V2, V3, V4 SQL)
│   ├── application.yml          # Configuração base Spring Boot
│   ├── application-dev.yml      # Configuração para perfil desenvolvimento
│   └── application-prod.yml     # Configuração para perfil produção
├── .env.example                 # Modelo de variáveis de ambiente
├── AGENTS.md                    # Contexto para assistentes e ferramentas de IA
├── CLAUDE.md                    # Diretrizes e comandos de desenvolvimento
├── owasp-suppressions.xml       # Exceções de falsos positivos do OWASP Dependency Check
├── spotbugs-exclude.xml         # Exceções de regras estáticas do SpotBugs
└── pom.xml                      # Arquivo do Maven com dependências e perfis
```

---

## ⚙️ Funcionalidades Detalhadas

### 1. 🛍️ Catálogo e CRUD de Funko Pops
- **Listagem e Busca**: Consulta paginada (`Pageable`) com suporte a busca textual por nome ou franquia.
- **Cadastro e Edição**: Upload multipart de imagens, vinculação de preço, franquia, nome e descrição.
- **Exclusão**: Remoção de cadastros com log de auditoria associado.

### 2. 🤖 Reconhecimento Automático com IA (Spring AI)
- **Endpoint**: `POST /api/funkos/analyze-image`
- **Fluxo**: Ao carregar a foto do produto no formulário, a imagem é codificada em Base64 e enviada via Spring AI para o modelo `gpt-4o`.
- **Retorno**: A IA extrai e estrutura um JSON com:
  - `nome`: Nome provável do personagem.
  - `franquia`: Franquia ou universo do produto (ex.: *Star Wars*, *Marvel*, *Harry Potter*).
  - `preco`: Preço estimado de mercado em BRL.

### 3. ☁️ Upload e Armazenamento no MinIO / S3
- **Bucket Automático**: Na inicialização da aplicação, o `BucketInitializer` garante a criação do bucket no MinIO/S3 e aplica a política de acesso de leitura pública.
- **Serviço Integrado**: `UploadService` utiliza a AWS SDK v2 (`S3Client`) para armazenar imagens e gerar a URL permanente salva no banco.

### 4. 🔒 Autenticação JWT e RBAC
- **Login e Registro**: Endpoints `/auth/login` e `/auth/register`.
- **Criptografia**: Senhas salvas com hash BCrypt.
- **Validação de Token**: Filtro customizado `JwtAuthFilter` intercepta cada requisição HTTP e extrai o perfil (`ROLE_ADMIN` ou `ROLE_USER`).

### 5. 📜 Log de Auditoria Automatizado (Spring AOP)
- **Operações Auditadas**: `CRIAR`, `ATUALIZAR`, `EXCLUIR`, `REGISTRAR`, `LOGIN`.
- **Tabela**: Mapeada em `log_auditoria` guardando usuário, ação, ID do recurso, status (`SUCESSO`/`FALHA`), mensagem de erro e timestamp.
- **Isolamento**: Persistência configurada com `Propagation.REQUIRES_NEW`, assegurando que mesmo se a transação do negócio falhar e sofrer rollback, o log de erro continuará gravado no banco.

---

## 📋 Pré-requisitos para Execução

Antes de iniciar, certifique-se de ter instalado em sua máquina:

1. **Java JDK 21** (Recomendado: [Eclipse Temurin 21](https://adoptium.net/temurin/releases/?version=21))
2. **Apache Maven 3.9+** ([Download Maven](https://maven.apache.org/download.cgi))
3. **Docker Desktop** ([Download Docker](https://www.docker.com/products/docker-desktop/))
4. **Node.js 18+** (Opcional, apenas se for desenvolver/editar o frontend React localmente)

---

## 🔧 Configuração do Ambiente (`.env`)

Crie o arquivo de variáveis de ambiente com base no exemplo fornecido:

```bash
cp .env.example .env
```

Edite o arquivo `.env` ajustando as credenciais conforme necessário:

```env
# Banco de Dados PostgreSQL
DB_USERNAME=eq21
DB_PASSWORD=sua-senha-segura-aqui

# MinIO / AWS S3 Storage
MINIO_ACCESS_KEY=eq21
MINIO_SECRET=sua-chave-minio-segura

# Segredos de Autenticação JWT
JWT_SECRET=sua-chave-jwt-com-pelo-menos-32-caracteres-gerada
JWT_EXPIRATION_MS=86400000

# Usuário Administrador Padrão (Criado na primeira subida)
ADMIN_EMAIL=admin@trokets.com
ADMIN_SENHA=admin123

# OpenAI / LiteLLM (Análise de Imagem por IA)
OPENAI_BASE_URL=https://llm.rodrigor.com
OPENAI_API_KEY=sk-sua-chave-de-api-aqui
OPENAI_MODEL=gpt-4o

# Docker Container Registry (Usado no Deploy CI/CD)
APP_IMAGE=ghcr.io/seu-usuario/mercado:latest
```

---

## 🚦 Como Rodar o Projeto

### Opção 1 — Desenvolvimento Híbrido (Recomendado para Devs)

Nesta opção, a infraestrutura (PostgreSQL, MinIO e pgAdmin) roda em containers Docker, enquanto o Backend Java e o Frontend React rodam localmente na sua máquina para recarregamento rápido do código.

```bash
# 1. Suba os serviços de infraestrutura no Docker
docker compose -f docker/docker-compose.dev.yml up postgres minio pgadmin

# 2. Em um novo terminal, inicie o Backend Spring Boot
mvn spring-boot:run

# 3. (Opcional) Em outro terminal, inicie o servidor de Dev do Frontend
cd frontend
npm install
npm run dev
```

---

### Opção 2 — Containerizado Completo (Docker Compose)

Suba toda a aplicação (Banco, MinIO, pgAdmin e Backend/Frontend integrados) em containers isolados com um único comando:

```bash
docker compose -f docker/docker-compose.dev.yml up --build
```

Aguarde até a exibição da mensagem de sucesso no log:
`Started MercadoApplication in X.XXX seconds`

---

### Opção 3 — Ambiente de Produção

Para subir o ambiente de produção otimizado:

```bash
# Gerar a imagem de produção
docker build -f docker/Dockerfile -t mercado:latest .

# Subir com o Compose de Produção
docker compose -f docker/docker-compose.prod.yml up -d
```

---

## 🌐 Endpoints, URLs e Acesso Local

Quando o ambiente estiver em execução, acesse os serviços nas seguintes URLs:

| Serviço / Recurso | Endereço | Descrição / Credenciais Padrão |
| :--- | :--- | :--- |
| **Aplicação Web (SPA)** | `http://localhost:8121` | Interface principal do e-commerce |
| **Frontend Vite (Dev)** | `http://localhost:5173` | Servidor de desenvolvimento React |
| **Adminer / pgAdmin** | `http://localhost:8888` | UI do banco (Host: `postgres`, BD/User: `eq21`) |
| **MinIO Console** | `http://localhost:9001` | Painel de controle do S3 (`MINIO_ACCESS_KEY` / `MINIO_SECRET`) |
| **MinIO S3 API** | `http://localhost:9000` | Endpoint de envio e recuperação de arquivos |
| **Health Check (Actuator)** | `http://localhost:8121/actuator/health` | Status de saúde da aplicação e conexão BD |
| **Ping Service** | `http://localhost:8121/ping` | Verificação simples de uptime |

---

## 📡 Resumo da API REST

### 🔑 Autenticação (`/auth`)
| Método | Endpoint | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Público | Cadastra um novo usuário no sistema |
| `POST` | `/auth/login` | Público | Autentica usuário e retorna o Token JWT |

### 🧸 Gestão de Funkos (`/api/funkos`)
| Método | Endpoint | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/funkos` | Público | Listagem paginada (suporta parâmetro `?busca=...`) |
| `GET` | `/api/funkos/{id}` | Público | Retorna os detalhes de um Funko pelo ID |
| `POST` | `/api/funkos` | Admin | Cria um novo Funko Pop (suporta upload de foto) |
| `PUT` | `/api/funkos/{id}` | Admin | Atualiza os dados de um Funko existente |
| `DELETE` | `/api/funkos/{id}` | Admin | Remove um Funko Pop do catálogo |
| `POST` | `/api/funkos/analyze-image` | Autenticado | Envia imagem para análise automática via GPT-4o |

### 📤 Uploads de Arquivos (`/api/uploads`)
| Método | Endpoint | Acesso | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/uploads/imagens` | Autenticado | Realiza upload direto de imagens para o MinIO/S3 |

---

## 🧪 Testes e Qualidade de Código

### Rodar Testes de Integração
Os testes utilizam **Testcontainers** para subir uma instância real e limpa do PostgreSQL em container durante a execução.

```bash
mvn test
```

### Relatório de Cobertura (JaCoCo)
A cobertura do projeto está acima de **90% de instruções**. Para gerar o relatório detalhado:

```bash
# Executar a verificação e gerar relatório JaCoCo
mvn clean verify

# O relatório em HTML estará em:
# target/site/jacoco/index.html
```

---

## 🛡️ Análise de Segurança e SAST

O projeto adota uma esteira de segurança em conformidade com as diretrizes da disciplina:

```bash
# 1. Executar análise estática de código (SpotBugs + FindSecBugs + OWASP Dependency Check)
mvn verify -Psecurity

# 2. Verificar dependências desatualizadas
mvn versions:display-dependency-updates -Pversions

# 3. Executar verificação de vulnerabilidades no sistema de arquivos com Trivy
docker compose -f docker/docker-compose.dev.yml --profile scan up trivy

# 4. Análise de segurança da Imagem Docker de produção
docker build -f docker/Dockerfile -t mercado:latest .
docker run --rm aquasec/trivy image mercado:latest
```

---

## 🔄 CI/CD e Deploy Automatizado (GitHub Actions)

O repositório possui uma pipeline configurada em `.github/workflows/deploy.yml` que executa automaticamente a cada `push` na branch `main`:

1. **Testes e Análise SAST**: Executa `mvn test` e `mvn verify -Psecurity`.
2. **Build Docker**: Compila o pacote e constrói a imagem publicada no **GitHub Container Registry (GHCR)**.
3. **Deploy SSH**: Conecta ao servidor de produção (`dsc.rodrigor.com`) e atualiza o container em execução.

### Secrets Necessários no Repositório GitHub
Para habilitar a automação completa no GitHub:

- `SSH_DEPLOY_KEY`: Chave SSH privada autorizada no servidor de hospedagem.
- `NVD_API_KEY`: Chave de API do *National Vulnerability Database* para acelerar as verificações do OWASP Dependency Check.

---

## 📝 Convenções de Desenvolvimento

- **Commits**: Seguir a especificação [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`).
- **DTOs**: Utilizar exclusivamente **Records Java** imutáveis no pacote `dto`.
- **Migrations Database**: NUNCA alterar migrations Flyway que já foram aplicadas. Adicionar novos scripts sequenciais (ex.: `V5__descricao_da_mudanca.sql`).
- **Lógica de Negócio**: Deve residir na camada `service` com a anotação `@Transactional`.

---

## 🤝 Equipe e Créditos

Desenvolvido pela **Equipe 21**:
- **Emilly Poliane**
- **Victor Kawê**
- **Equipe 21 — Projeto DSC UFPB**
- [Vídeo de apresentação do sistema](https://drive.google.com/file/d/1vtvoVyY4BWM0Rlclss6lCApiNIKIlPae/view?usp=sharing)

Orientação: **Prof. Rodrigo Rebouças**  
Universidade Federal da Paraíba (UFPB) — Campus IV  

---

<p align="center">
  Desenvolvido com ☕ Java 21, ⚡ Spring Boot e ⚛️ React.
</p>
