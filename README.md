# Sistema Mercado — Projeto Base DSC/UFPB

Projeto base (boilerplate) para a disciplina **Desenvolvimento de Sistemas Corporativos**.

**Professor**: Rodrigo Rebouças | **UFPB — Campus IV**

---

## Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| Backend | Java 21 + Spring Boot 3.4.5 |
| Frontend | React 18 + Vite 5 (SPA) |
| IA / Visão | Spring AI 1.0.0 (OpenAI-compatible) |
| Object Storage | MinIO (compatível com AWS S3) |
| Banco | PostgreSQL 16 |
| Migrações | Flyway 11 |
| Segurança | Spring Security 6 + JWT (JJWT 0.12.6) |
| Build | Maven 3.9 |
| CI/CD | GitHub Actions |

---

## Guia de Instalação para Alunos

### Passo 1 — Instale o Java 21

O projeto requer Java 21. Recomendamos o **Eclipse Temurin** (distribuição gratuita da Adoptium).

**Windows / macOS / Linux:**
1. Acesse https://adoptium.net/temurin/releases/?version=21
2. Baixe o instalador para seu sistema operacional
3. Execute o instalador e siga as instruções

**Verificar se está correto:**
```bash
java -version
# Esperado: openjdk version "21.x.x" ...
```

> **Dica para Windows:** durante a instalação, marque a opção *"Add to PATH"* e *"Set JAVA_HOME"*.

---

### Passo 2 — Instale o Maven

O Maven é a ferramenta de build do projeto.

**macOS (com Homebrew):**
```bash
brew install maven
```

**Windows:**
1. Acesse https://maven.apache.org/download.cgi
2. Baixe o arquivo `apache-maven-3.x.x-bin.zip`
3. Extraia para uma pasta (ex.: `C:\maven`)
4. Adicione `C:\maven\bin` à variável de ambiente `PATH`

**Linux (Ubuntu/Debian):**
```bash
sudo apt install maven
```

**Verificar:**
```bash
mvn -version
# Esperado: Apache Maven 3.x.x
```

---

### Passo 3 — Instale o Docker Desktop

O Docker sobe o banco de dados PostgreSQL sem precisar instalar nada manualmente.

1. Acesse https://www.docker.com/products/docker-desktop/
2. Baixe e instale o Docker Desktop para seu sistema
3. Abra o Docker Desktop e aguarde ele inicializar (ícone na barra de tarefas)

**Verificar:**
```bash
docker -v
# Esperado: Docker version 27.x.x ...
```

> **Importante:** o Docker Desktop deve estar **em execução** sempre que você for rodar o projeto.

---

### Passo 4 — Clone o repositório

```bash
git clone <URL-DO-REPOSITÓRIO>
cd base_projeto
```

> Substitua `<URL-DO-REPOSITÓRIO>` pela URL fornecida pelo professor.

---

### Passo 5 — Execute o projeto

Você tem duas opções. **Recomendamos a Opção A para a primeira execução.**

#### Opção A: Tudo com Docker (mais simples)

Um único comando sobe o banco, a aplicação e o Adminer (interface web do banco):

```bash
docker compose -f docker/docker-compose.dev.yml up --build
```

Aguarde as mensagens de inicialização. Quando aparecer algo como:
```
Started MercadoApplication in X.XXX seconds
```
...a aplicação está pronta.

#### Opção B: Banco no Docker + aplicação local (recomendado para desenvolvimento)

Esta opção permite editar o código e ver as mudanças mais rápido:

```bash
# Terminal 1 — sobe o banco de dados
docker compose -f docker/docker-compose.dev.yml up postgres adminer

# Terminal 2 — roda a aplicação (em outro terminal, na mesma pasta)
mvn spring-boot:run
```

---

### Passo 6 — Acesse no browser

| O que | Endereço |
|-------|----------|
| Aplicação | http://localhost:8121 |
| Login | usuário: `admin@test.com` / senha: `admin123` |
| Adminer (banco) | http://localhost:8888 |
| Health check | http://localhost:8121/actuator/health |

---

### Parando o projeto

```bash
# Parar a aplicação: Ctrl+C no terminal onde está rodando

# Parar os containers Docker:
docker compose -f docker/docker-compose.dev.yml down
```

---

## Solução de Problemas Comuns

### "Port 8121 already in use"
Outra aplicação está usando a porta 8121. Para liberar:
```bash
# macOS / Linux
lsof -ti:8121 | xargs kill

# Windows (PowerShell)
netstat -ano | findstr :8121
# Anote o PID da última coluna e execute:
taskkill /PID <número-do-pid> /F
```

### "Cannot connect to the Docker daemon"
O Docker Desktop não está em execução. Abra o aplicativo Docker Desktop e aguarde inicializar.

### "Connection refused" ao banco de dados
O container do PostgreSQL ainda não subiu. Aguarde alguns segundos e tente novamente. Você pode verificar com:
```bash
docker compose -f docker/docker-compose.dev.yml ps
# O container "mercado-postgres-dev" deve estar com status "healthy"
```

### Erro de compilação Java
Verifique se o Java 21 está sendo usado pelo Maven:
```bash
mvn -version
# A linha "Java version:" deve mostrar 21.x.x
```
Se mostrar outra versão, configure a variável `JAVA_HOME` apontando para o Java 21.

### Flyway: "Found non-empty schema(s) with no schema history table"
O banco existe mas foi criado sem as migrations. Apague os dados e recomece:
```bash
docker compose -f docker/docker-compose.dev.yml down -v
docker compose -f docker/docker-compose.dev.yml up postgres
```

---

## Log de Auditoria

O sistema registra automaticamente as ações de escrita por meio de um aspecto AOP, sem necessidade de código nos controllers ou services de domínio.

### O que é auditado

| Ação | Operação registrada |
|------|-------------------|
| Criação de funko | `CRIAR` |
| Atualização de funko | `ATUALIZAR` |
| Exclusão de funko | `EXCLUIR` |
| Registro de usuário | `REGISTRAR` |
| Login de usuário | `LOGIN` |

### Onde fica armazenado

Tabela `log_auditoria` no PostgreSQL, com os campos:

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | BIGINT | Chave primária |
| `recurso` | VARCHAR(50) | Entidade afetada (`FUNKO`, `USUARIO`) |
| `recurso_id` | VARCHAR(50) | ID do registro afetado |
| `operacao` | VARCHAR(50) | Nome da operação (`CRIAR`, `EXCLUIR`, etc.) |
| `usuario` | VARCHAR(255) | E-mail do usuário autenticado |
| `detalhe` | VARCHAR(500) | Informações adicionais (ex.: `nome=Batman`) |
| `status` | VARCHAR(10) | `SUCESSO` ou `FALHA` |
| `mensagem_erro` | VARCHAR(500) | Mensagem de erro (quando `status=FALHA`) |
| `ocorrido_em` | TIMESTAMP | Data/hora da ocorrência |

### Como foi implementado

Utilizando **Spring AOP** com `@Around` advice que intercepta métodos dos services antes e depois da execução. Em caso de exceção, o status é registrado como `FALHA` e a exceção é re-lançada. A persistência usa `Propagation.REQUIRES_NEW` para garantir que o log seja salvo mesmo se a transação principal sofrer rollback.

### Classes participantes

- `src/main/java/br/ufpb/dsc/mercado/config/AuditoriaAspect.java` — aspecto AOP que intercepta `FunkoService` e `AuthService`
- `src/main/java/br/ufpb/dsc/mercado/service/AuditoriaService.java` — persiste os logs com `REQUIRES_NEW`
- `src/main/java/br/ufpb/dsc/mercado/domain/LogAuditoria.java` — entidade JPA mapeada para `log_auditoria`
- `src/main/java/br/ufpb/dsc/mercado/repository/LogAuditoriaRepository.java` — repositório Spring Data JPA

---

## Integração com Serviço Externo

O sistema integra dois serviços externos além do banco de dados:

### 1. MinIO / AWS S3 — Object Storage de imagens

**Para que é usado:** armazenamento de imagens dos Funko Pops carregadas pelos administradores. Cada imagem é enviada ao bucket S3-compatível e a URL pública é salva no banco.

**Quais classes participam:**
- `src/main/java/br/ufpb/dsc/mercado/config/S3Config.java` — cria o bean `S3Client` com endpoint, região e credenciais configuráveis
- `src/main/java/br/ufpb/dsc/mercado/config/BucketInitializer.java` — cria o bucket na inicialização se ainda não existir e aplica política de leitura pública
- `src/main/java/br/ufpb/dsc/mercado/service/UploadService.java` — realiza upload de arquivos via `S3Client.putObject()`
- `src/main/java/br/ufpb/dsc/mercado/controller/UploadController.java` — endpoint `POST /api/uploads/imagens`

**Variáveis de ambiente:**
```
AWS_S3_ENDPOINT=<URL do MinIO, ex.: http://minio:9000>
AWS_S3_PUBLIC_ENDPOINT=<URL pública, ex.: http://localhost:9000>
AWS_S3_REGION=us-east-1
AWS_S3_BUCKET=<nome do bucket>
AWS_S3_ACCESS_KEY=<chave de acesso>
AWS_S3_SECRET_KEY=<chave secreta>
```

---

### 2. LiteLLM / OpenAI-compatible — Análise de imagens com IA

**Para que é usado:** análise automática de imagens de Funko Pops. O usuário seleciona uma foto do produto e o sistema chama um modelo de visão (gpt-4o via proxy LiteLLM) para identificar nome do personagem, franquia e preço sugerido, pré-preenchendo o formulário de cadastro.

**Quais classes participam:**
- `src/main/java/br/ufpb/dsc/mercado/service/FunkoAnaliseService.java` — envia a imagem ao modelo via `ChatClient` (Spring AI) e parseia o JSON retornado
- `src/main/java/br/ufpb/dsc/mercado/dto/FunkoAnaliseResponse.java` — record de resposta com campos `nome`, `franquia` e `preco`
- `src/main/java/br/ufpb/dsc/mercado/controller/FunkoController.java` — endpoint `POST /api/funkos/analyze-image`

**Variáveis de ambiente:**
```
OPENAI_BASE_URL=<URL base do proxy LiteLLM, ex.: https://llm.rodrigor.com/v1>
OPENAI_API_KEY=<chave de API>
OPENAI_MODEL=gpt-4o
```

---

## Testes e Cobertura

**Cobertura de testes: 93% de instruções** (115 de 1.727 instruções não cobertas).

Relatório completo disponível em: [`cobertura/index.html`](cobertura/index.html)

```bash
# Rodar todos os testes (requer Docker em execução — usa Testcontainers)
mvn test

# Rodar com relatório de cobertura (JaCoCo)
mvn clean test jacoco:report
# Relatório: abra o arquivo target/site/jacoco/index.html no browser

# Regenerar a pasta cobertura/
cp -r target/site/jacoco cobertura/
```

---

## Análise de Segurança (SAST)

```bash
# SpotBugs + FindSecBugs + OWASP Dependency Check
mvn verify -Psecurity

# Trivy: scan de vulnerabilidades no filesystem
docker compose -f docker/docker-compose.dev.yml --profile scan up trivy

# Verificar dependências desatualizadas
mvn versions:display-dependency-updates -Pversions
```

Veja `docs/SECURITY.md` para detalhes.

---

## Configurando o Deploy Automático (GitHub Actions)

O projeto inclui um pipeline de CI/CD em `.github/workflows/deploy.yml` que:
- roda os testes automaticamente a cada `push` na branch `main`
- executa análise de segurança (SAST) no código e nas dependências
- constrói a imagem Docker de produção e faz o deploy no servidor da disciplina

Para ativar o deploy, você precisa configurar **dois secrets** e uma **variável** no seu repositório GitHub.

---

### Secret 1 — Chave SSH de deploy (`SSH_DEPLOY_KEY`)

O servidor da disciplina (`dsc.rodrigor.com`) já está preparado para receber deploys.
A chave SSH que autoriza o acesso está disponível na página da disciplina:

**Acesse: https://gd.dsc.rodrigor.com** e copie a chave SSH privada disponibilizada pelo professor.

Depois, adicione no seu repositório:

1. No GitHub, acesse seu repositório → **Settings**
2. No menu lateral: **Secrets and variables → Actions**
3. Clique em **New repository secret**
4. Nome: `SSH_DEPLOY_KEY`
5. Valor: cole a chave privada copiada do portal (o texto completo, incluindo as linhas `-----BEGIN...` e `-----END...`)
6. Clique em **Add secret**

---

### Secret 2 — Chave da API do NVD (`NVD_API_KEY`)

#### O que é o NVD?

**NVD** significa *National Vulnerability Database* — é o banco de dados oficial do governo americano (NIST) que cataloga todas as vulnerabilidades de segurança conhecidas em softwares. Cada vulnerabilidade recebe um identificador chamado **CVE** (ex.: CVE-2024-12345) e uma nota de gravidade chamada **CVSS** (de 0 a 10).

O **OWASP Dependency Check** (uma das ferramentas de segurança do projeto) consulta esse banco para verificar se as bibliotecas que o seu projeto usa possuem vulnerabilidades conhecidas.

#### Por que preciso de uma chave?

Sem a chave, o download do banco de dados NVD é muito lento (pode levar 20+ minutos no CI/CD, ou até falhar por timeout). Com a chave gratuita, o download é feito via API e leva menos de 2 minutos.

#### Como obter (gratuito, leva ~1 minuto)

1. Acesse https://nvd.nist.gov/developers/request-an-api-key
2. Preencha seu e-mail institucional (use o e-mail da UFPB se possível)
3. Marque a caixa de uso não-comercial
4. Clique em **Submit**
5. Acesse seu e-mail — você receberá a chave em segundos

#### Adicionando ao repositório

1. No GitHub: **Settings → Secrets and variables → Actions**
2. Clique em **New repository secret**
3. Nome: `NVD_API_KEY`
4. Valor: cole a chave recebida por e-mail
5. Clique em **Add secret**

> **Sem a chave ainda?** O pipeline funciona mesmo sem ela, mas o OWASP Dependency Check
> pode demorar muito ou falhar por timeout. Configure assim que possível.

---

### Variável — Nome da imagem Docker (`APP_IMAGE`)

O pipeline publica a imagem Docker no GitHub Container Registry (GHCR) com o nome do seu repositório. Você não precisa configurar isso manualmente — o workflow usa `${{ github.repository }}` para montar o nome automaticamente.

Mas o arquivo `.env` no servidor precisa saber qual imagem usar. O script de deploy atualiza isso automaticamente na primeira execução.

---

### Verificando se o deploy funcionou

Após configurar os secrets e fazer um `push` na branch `main`:

1. No GitHub, clique na aba **Actions**
2. Você verá o workflow **"Build & Deploy"** em execução
3. Ele tem 3 etapas: **Testes e SAST → Build e push → Deploy em produção**
4. Se tudo der certo, a aplicação estará disponível em `https://dsc.rodrigor.com`

Se alguma etapa falhar, clique nela para ver os logs detalhados.

---

## Estrutura do Projeto

```
base_projeto/
├── .github/workflows/
│   └── deploy.yml           # Pipeline CI/CD (GitHub Actions)
├── src/main/java/br/ufpb/dsc/mercado/
│   ├── config/              # Configurações (Security, GlobalModelAttributes, etc.)
│   ├── controller/          # Controllers HTTP + HTMX
│   ├── domain/              # Entidades JPA
│   ├── dto/                 # Data Transfer Objects (Records)
│   ├── exception/           # Exceções de domínio
│   ├── repository/          # Interfaces Spring Data JPA
│   └── service/             # Lógica de negócio
├── src/main/resources/
│   ├── db/migration/        # Scripts Flyway (V1__, V2__, ...)
│   └── templates/           # Templates Thymeleaf
├── docker/                  # Dockerfiles + docker-compose
├── docs/                    # Documentação técnica
├── CLAUDE.md                # Memória para Claude Code
└── pom.xml
```

---

## Para Alunos: Adaptando o Boilerplate

1. **Renomear** a entidade `Produto` para sua entidade principal
2. **Criar migration** Flyway com a nova estrutura da tabela (`src/main/resources/db/migration/V2__...sql`)
3. **Atualizar** Repository, Service, Controller e templates seguindo os mesmos padrões
4. **Manter** a estrutura de pacotes e convenções (ver `docs/CONVENTIONS.md`)
5. **Nunca editar** migrations já aplicadas — sempre criar uma nova (`V3__`, `V4__`, ...)

> Dúvidas? Consulte a documentação em `docs/` ou o professor.
