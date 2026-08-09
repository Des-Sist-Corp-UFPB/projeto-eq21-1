# Convenções do Projeto

## Estrutura de Migrations Flyway

```
V{número}__{descrição_com_underscores}.sql
V1__criar_tabela_produto.sql
V2__adicionar_indice_preco.sql
V3__criar_tabela_usuario.sql
```

- Nunca editar uma migration já commitada
- Descrição em português, snake_case
- Incrementar o número sequencialmente

## Conventional Commits

```
feat: adicionar filtro por categoria de produto
fix: corrigir cálculo de desconto no preço
docs: atualizar README com instruções de deploy
refactor: extrair validação de preço para método privado
test: adicionar teste de integração para FunkoService
chore: atualizar dependências do pom.xml
```

## Nomenclatura Java

| Elemento | Convenção | Exemplo |
|---|---|---|
| Package | lowercase | `br.ufpb.dsc.mercado.service` |
| Classe | PascalCase | `FunkoService` |
| Método | camelCase | `buscarPorId()` |
| Constante | UPPER_SNAKE | `MAX_NOME_LENGTH` |
| Variável | camelCase | `funkoRequest` |

## Nomenclatura React (Frontend)

- Componentes: PascalCase (`ModalForm`, `BtnAcao`)
- Funções e estados: camelCase (`carregar`, `setAnalisando`)
- Arrow functions para callbacks e funções assíncronas internas
- Props em camelCase: `onSalvar`, `onFechar`

## Padrão REST (Controllers)

- Controllers anotados com `@RestController`
- DTOs validados com `@Valid` e Bean Validation (`@NotBlank`, `@Size`, etc.)
- Erros retornam JSON: `{ "erro": "mensagem" }` (via `GlobalExceptionHandler`)
- Multipart para endpoints que recebem arquivos (`consumes = "multipart/form-data"`)

## Segurança — Boas Práticas

- Usar parâmetros nomeados em queries JPA (nunca concatenar strings)
- Variáveis sensíveis em `.env` (nunca hardcoded no código)
- CSRF desabilitado — autenticação JWT stateless (sem sessão no servidor)
- Senhas armazenadas com BCrypt
- JWT validado em cada requisição via `JwtAuthFilter`
