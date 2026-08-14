# Como rodar a API

Guia prático de execução do **Sistema de Pedidos**: rodar na máquina, rodar em container, e alternar entre os perfis `dev` e `prod`. Para o que a API faz e como ela é modelada, veja o [README.md](README.md).

---

## 1. Pré-requisitos

| Ferramenta | Necessário para | Como conferir |
|---|---|---|
| **JDK 17** | rodar/compilar localmente | `java -version` → deve mostrar `17.x` |
| **Docker Desktop** | rodar em container | `docker --version` e `docker info` |
| Maven | — | **não precisa instalar**: o projeto usa o wrapper `mvnw` |

Se o `java -version` falhar ou apontar outra versão, instale um JDK 17 (Temurin, Microsoft OpenJDK, Corretto — qualquer distribuição serve) e aponte o `JAVA_HOME` para ele antes de chamar o wrapper:

```powershell
# PowerShell (Windows)
$env:JAVA_HOME = "C:\caminho\para\jdk-17"
```

```bash
# Git Bash / Linux / macOS
export JAVA_HOME=/caminho/para/jdk-17
```

Todos os comandos abaixo devem ser executados **na raiz do projeto** (a pasta que contém o `pom.xml`).

---

## 2. Rodar localmente (jeito normal)

### 2.1. Perfil `dev` — H2 em memória, sem configurar nada

Este é o modo de desenvolvimento: banco em memória, dados de exemplo criados no boot, nenhuma credencial necessária.

```powershell
# PowerShell (Windows)
$env:SPRING_PROFILES_ACTIVE = "dev"
.\mvnw.cmd spring-boot:run
```

```bash
# Git Bash / Linux / macOS
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

> ⚠️ **Por que definir o perfil na mão, se o padrão já é `dev`?**
> Porque PODE existir um arquivo `.env` com `SPRING_PROFILES_ACTIVE=prod`, e o projeto lê o `.env` automaticamente (dependência `spring-dotenv`). Ou seja: **`.\mvnw.cmd spring-boot:run` sem mais nada sobe apontando para o banco de produção no Supabase**, não para o H2. A variável de ambiente tem prioridade sobre o `.env`, então defini-la é o jeito confiável de garantir o H2.
> Alternativa equivalente: `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"`.

Quando o log mostrar `Started PedidosApplication in X seconds`, a aplicação está no ar:

| O quê | URL |
|---|---|
| API | http://localhost:8080 |
| Painel estático | http://localhost:8080/index.html |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI (JSON) | http://localhost:8080/v3/api-docs |
| Console do H2 | http://localhost:8080/h2-console |

Credenciais do console H2: JDBC URL `jdbc:h2:mem:devdb`, usuário `sa`, senha em branco.

Teste rápido de que subiu certo:

```powershell
curl http://localhost:8080/users
```

Deve responder com dois clientes (`Maria Brown` e `Alex Green`), criados pelo `TestConfig` a cada inicialização.

**Para parar:** `Ctrl+C` no terminal onde a aplicação está rodando.

### 2.2. Perfil `prod` local — PostgreSQL no Supabase

Serve para validar, da sua máquina, exatamente a configuração que vai para a nuvem.

**Passo 1 — criar o `.env`** (só na primeira vez; o arquivo não é versionado):

```powershell
copy .env.example .env
```

**Passo 2 — preencher** `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` com os dados de **Supabase → Project Settings → Database → Connection string**. Use o **Transaction Pooler** (porta 6543), que é o único que funciona em rede sem IPv6:

```
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://aws-0-<regiao>.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
DB_USERNAME=postgres.<project-ref>
DB_PASSWORD=<sua-senha>
```

O `prepareThreshold=0` **não é opcional** no pooler em modo transaction — sem ele a conexão quebra.

**Passo 3 — subir:**

```powershell
$env:SPRING_PROFILES_ACTIVE = "prod"
.\mvnw.cmd spring-boot:run
```

Em `prod` **não há** console H2 nem dados de exemplo: o `TestConfig` é desativado de propósito, para que cada deploy não duplique linhas no banco real.

> ⚠️ **Salve o `.env` em UTF-8 sem BOM.** O `Out-File -Encoding utf8` do PowerShell 5.1 grava BOM e a leitura do `.env` falha com `DotenvException: Malformed entry`, derrubando a aplicação antes de subir. Editar pelo IntelliJ/VS Code ou copiar com `copy` preserva a codificação certa.

### 2.3. Compilar e rodar o JAR

```powershell
.\mvnw.cmd clean package                 # gera target/SistemaDePedidos-0.0.1-SNAPSHOT.jar
java -jar target/SistemaDePedidos-0.0.1-SNAPSHOT.jar
```

Para escolher o perfil na hora de rodar o JAR:

```powershell
java -jar target/SistemaDePedidos-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

> O `clean package` roda os testes antes de empacotar. Para pular: `.\mvnw.cmd clean package -DskipTests`.

---

## 3. Os três perfis

O perfil é escolhido pela variável `SPRING_PROFILES_ACTIVE` (padrão do código: `dev`).

| Perfil | Banco | Dados de exemplo | Console H2 | Log de SQL | Quando usar |
|---|---|---|---|---|---|
| `dev` | H2 em memória (`jdbc:h2:mem:devdb`) | sim (`TestConfig`) | sim | sim | desenvolvimento e testes manuais |
| `test` | H2 em memória (`jdbc:h2:mem:testdb`) | sim (`TestConfig`) | não | não | testes automatizados |
| `prod` | PostgreSQL / Supabase | **não** | não | não | produção e homologação |

Cada perfil tem seu arquivo em `src/main/resources`: `application-dev.properties`, `application-test.properties`, `application-prod.properties`. O `application.properties` é comum aos três.

### Variáveis de ambiente do perfil `prod`

| Variável | Padrão | Obrigatória | Para quê |
|---|---|---|---|
| `DB_URL` | — | **sim** | JDBC URL do PostgreSQL |
| `DB_USERNAME` | — | **sim** | usuário do banco |
| `DB_PASSWORD` | — | **sim** | senha do banco |
| `DB_POOL_SIZE` | `5` | não | máximo de conexões no pool |
| `DB_POOL_MIN_IDLE` | `1` | não | conexões ociosas mantidas |
| `DDL_AUTO` | `update` | não | estratégia de schema do Hibernate |
| `PORT` | `8080` | não | porta HTTP (só tem efeito em `prod`) |

As três primeiras **não têm valor padrão de propósito**: sem elas a aplicação falha na inicialização, em vez de subir com um segredo fixo no código.

> 🔎 **Cuidado com o `PORT`:** ele só é lido pelo perfil `prod` (`server.port=${PORT:8080}` em `application-prod.properties`). Um valor errado ali — um `8080` que virou `808` por engano de digitação, por exemplo — faz a aplicação subir normalmente, mas em outra porta, e `http://localhost:8080` parece "fora do ar". Em `dev` e `test` a variável é ignorada, então o problema só aparece em produção.

---

## 4. Rodar via Docker

A imagem é **multi-stage**: um estágio compila com Maven, o outro entrega só o JAR sobre uma JRE, rodando como usuário sem privilégios. **Nenhuma credencial vai para dentro da imagem** — elas são injetadas na hora de rodar.

Antes de qualquer coisa, o Docker Desktop precisa estar **aberto e rodando** (`docker info` responde sem erro).

### 4.1. Build da imagem

```powershell
docker build -t sistema-de-pedidos .
```

O primeiro build baixa todas as dependências Maven e demora alguns minutos. Os seguintes reaproveitam a camada de dependências, desde que o `pom.xml` não mude.

### 4.2. Rodar em `dev` (H2, sem banco externo)

Jeito mais rápido de validar que a imagem ficou boa, sem tocar no banco real:

```powershell
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev sistema-de-pedidos
```

Acesse http://localhost:8080/users — devem aparecer os mesmos dados de exemplo do modo local.

### 4.3. Rodar em `prod` (Supabase)

A imagem já vem com `SPRING_PROFILES_ACTIVE=prod` definido, então basta passar as credenciais:

```powershell
docker run --rm -p 8080:8080 --env-file .env sistema-de-pedidos
```

Se preferir não usar arquivo, passe variável por variável:

```powershell
docker run --rm -p 8080:8080 `
  -e SPRING_PROFILES_ACTIVE=prod `
  -e DB_URL="jdbc:postgresql://aws-0-<regiao>.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0" `
  -e DB_USERNAME="postgres.<project-ref>" `
  -e DB_PASSWORD="<sua-senha>" `
  sistema-de-pedidos
```

> De dentro do container, use sempre o **Transaction Pooler (6543)**. A conexão direta (5432) é IPv6-only no plano gratuito do Supabase e costuma dar timeout no Docker Desktop do Windows.

### 4.4. Docker Compose

O `docker-compose.yml` sobe **só a API** — o banco é o Supabase, um serviço externo, então não existe container de PostgreSQL aqui.

```powershell
docker compose up --build      # constrói e sobe
docker compose logs -f         # acompanha os logs
docker compose down            # derruba
```

O Compose lê o `.env` automaticamente (`env_file`). A porta publicada vem de `APP_PORT` (padrão `8080`) e a porta interna, de `PORT`.

### 4.5. Comandos úteis do dia a dia

```powershell
docker ps                                  # containers rodando
docker logs -f sistema-de-pedidos          # logs de um container nomeado
docker stop sistema-de-pedidos             # parar
docker run --rm -p 9090:8080 sistema-de-pedidos   # publicar em outra porta do host
docker image rm sistema-de-pedidos         # remover a imagem
```

O `--rm` dos exemplos faz o container ser apagado ao parar — evita acumular container parado a cada teste.

---

## 5. Rodar os testes

```powershell
.\mvnw.cmd test                                             # tudo
.\mvnw.cmd test -Dtest=UserServiceTest                      # uma classe
.\mvnw.cmd test -Dtest=OrderTest#getTotalSomaOsSubtotais    # um método
```

A suíte está organizada em quatro camadas:

| Camada | Classes | O que cobre |
|---|---|---|
| **Domínio** | `OrderTest`, `OrderItemTest`, `OrderStatusTest` | total do pedido, subtotal do item e conversão do código de status — sem Spring, roda em milissegundos |
| **Services** (Mockito) | `UserServiceTest`, `OrderServiceTest`, `ProductServiceTest`, `CategoryServiceTest` | regras de negócio com o repositório mockado, incluindo a tradução de exceções em 404/400 |
| **Web** (`@WebMvcTest`) | `UserResourceTest`, `OrderResourceTest`, `ProductResourceTest`, `CategoryResourceTest` | rotas, códigos HTTP e formato do JSON, com o service mockado |
| **Integração** (`@SpringBootTest`) | `ApiIntegrationTest`, `PedidosApplicationTests` | aplicação inteira sobre o H2, batendo nos endpoints de verdade |

Nenhum teste depende de banco externo nem de Docker: os de integração usam H2 em memória, populado pelo `TestConfig`.

> ✅ **Os testes não dependem do `.env`.** As classes de integração fixam o perfil com `@ActiveProfiles("test")`, que tem prioridade sobre `SPRING_PROFILES_ACTIVE`. Mesmo com um `.env` apontando para `prod`, o `mvnw test` roda contra o H2 — nunca contra o banco real.

---

## 6. Problemas comuns

| Sintoma | Causa | Solução |
|---|---|---|
| `DotenvException: Malformed entry` no boot | `.env` salvo com BOM | regravar em UTF-8 sem BOM (`copy` do `.env.example`, ou salvar pelo editor) |
| `Could not resolve placeholder 'DB_URL'` | perfil `prod` ativo sem as variáveis definidas | preencher o `.env`, ou rodar em `dev` |
| Subiu, mas `localhost:8080` não responde | perfil `prod` com `PORT` diferente de 8080 | conferir o `PORT` no `.env` ou acessar a porta que aparece no log |
| Conectou no banco real sem querer | `.env` com `SPRING_PROFILES_ACTIVE=prod` | definir `SPRING_PROFILES_ACTIVE=dev` antes de rodar |
| `Web server failed to start. Port 8080 was already in use` | outra instância aberta | fechar o terminal antigo, ou `Get-Process java \| Stop-Process` |
| Timeout ao conectar no banco dentro do Docker | usando a conexão direta (5432, IPv6-only) | trocar para o Transaction Pooler (6543) com `prepareThreshold=0` |
| `failed to connect to the docker API` | Docker Desktop fechado | abrir o Docker Desktop e esperar ficar "Engine running" |
| `/h2-console` responde 404 | perfil `prod` ou `test` ativo | o console só existe em `dev` |
| Dados de exemplo sumiram | H2 é em memória | é esperado: o banco é recriado e repopulado a cada boot |

---

## 7. Resumo

```powershell
# Local, banco em memória — o caminho do dia a dia
$env:SPRING_PROFILES_ACTIVE = "dev"; .\mvnw.cmd spring-boot:run

# Local, banco real (precisa do .env preenchido)
$env:SPRING_PROFILES_ACTIVE = "prod"; .\mvnw.cmd spring-boot:run

# Container, banco em memória
docker build -t sistema-de-pedidos .
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev sistema-de-pedidos

# Container, banco real
docker run --rm -p 8080:8080 --env-file .env sistema-de-pedidos

# Container, via Compose
docker compose up --build
```
