# Sistema de Pedidos

API REST em Spring Boot para gerenciamento de pedidos de lojas: cadastro de clientes, catálogo de produtos organizado por categorias, pedidos com itens e pagamento, e um painel administrativo simples em HTML/JS servido pela própria aplicação.

## 📌 Visão Geral

O sistema modela o ciclo de uma venda:

1. **Produtos** são cadastrados e associados a uma ou mais **categorias**.
2. Um **usuário** (cliente) realiza um **pedido**, que nasce com o status `WAITING_PAYMENT`.
3. Cada **item do pedido** guarda o produto, a quantidade e o **preço no momento da compra** — assim, alterar o preço do produto depois não distorce pedidos antigos.
4. O valor total do pedido é **calculado em tempo real** a partir dos itens, e não armazenado no banco.
5. Um **pagamento** pode ser associado ao pedido, movendo-o para `PAID` e adiante no fluxo de status.

## 🛠 Tecnologias

- Java 17
- Spring Boot 3.5.3 (Web, Data JPA)
- H2 Database (em memória, desenvolvimento)
- PostgreSQL via [Supabase](https://supabase.com) (produção)
- springdoc-openapi 2.8.9 (Swagger UI)
- Maven 3.9.10 (via wrapper `mvnw` / `mvnw.cmd`)
- Docker (imagem multi-stage) e Docker Compose
- Frontend: HTML, CSS e JavaScript puro com axios (sem build)

## 🏗 Arquitetura

Aplicação em três camadas, no pacote `com.alvaropaiva.SistemaDePedidos`:

```
resources/     → Controllers REST (@RestController), expõem os endpoints
services/      → Regras de negócio (@Service)
repositories/  → Acesso a dados (interfaces JpaRepository)
entities/      → Entidades JPA, mapeadas para as tabelas tb_*
config/        → TestConfig: popula o banco no perfil de teste
```

As entidades são serializadas diretamente em JSON (o projeto não usa DTOs). Para evitar recursão infinita, os lados inversos dos relacionamentos são marcados com `@JsonIgnore`.

### Entidades principais

| Entidade    | Tabela            | Descrição                                             |
|-------------|-------------------|-------------------------------------------------------|
| `User`      | `tb_user`         | Clientes do sistema (senha nunca é exposta na API)     |
| `Category`  | `tb_category`     | Categorias de produtos                                |
| `Product`   | `tb_products`     | Itens para venda                                      |
| `Order`     | `tb_order`        | Pedidos dos usuários                                  |
| `OrderItem` | `tb_order_item`   | Itens dentro de um pedido (chave composta pedido+produto) |
| `Payment`   | `tb_payment`      | Pagamento associado a um pedido                       |

### Relacionamentos

- `User` **1 → N** `Order` (um cliente tem vários pedidos)
- `Order` **1 → N** `OrderItem`, com chave primária composta (`OrderItemPK` = pedido + produto)
- `Product` **N ↔ N** `Category`, via tabela de junção `tb_product_category`
- `Order` **1 ↔ 1** `Payment`, compartilhando a mesma chave primária (`@MapsId`)

### Status do pedido

O status é gravado como código numérico (enum `OrderStatus`):

| Código | Status            |
|--------|-------------------|
| 1      | `WAITING_PAYMENT` |
| 2      | `PAID`            |
| 3      | `SHIPPED`         |
| 4      | `DELIVERED`       |
| 5      | `CANCELED`        |

## 🔌 Endpoints

| Método   | Rota               | Descrição                                    |
|----------|--------------------|----------------------------------------------|
| `GET`    | `/users`           | Lista todos os clientes                      |
| `GET`    | `/users/{id}`      | Busca cliente por ID                         |
| `POST`   | `/users`           | Cria um cliente                              |
| `PUT`    | `/users/{id}`      | Atualiza nome, e-mail e/ou telefone          |
| `DELETE` | `/users/{id}`      | Remove um cliente                            |
| `GET`    | `/orders`          | Lista todos os pedidos                       |
| `GET`    | `/orders/{id}`     | Busca pedido por ID (com itens e pagamento)  |
| `GET`    | `/products`        | Lista todos os produtos                      |
| `GET`    | `/products/{id}`   | Busca produto por ID (com categorias)        |
| `GET`    | `/categories`      | Lista todas as categorias                    |
| `GET`    | `/categories/{id}` | Busca categoria por ID                       |

Hoje apenas `/users` possui CRUD completo; as demais rotas são somente leitura.

### Tratamento de erros

Erros são padronizados pelo `ResourceExceptionHandler` (`@ControllerAdvice`) e devolvidos como JSON:

```json
{
  "timestamp": "2023-01-01T12:00:00Z",
  "status": 404,
  "error": "Resource not found",
  "message": "Resource not found. ID 5",
  "path": "/users/5"
}
```

- `ResourceNotFoundException` → **404 Not Found**
- `DataBaseException` → **400 Bad Request** (ex.: excluir um cliente que possui pedidos)

## ▶ Como Executar

Em desenvolvimento não é preciso configurar nada: o perfil padrão é o `dev`, que usa H2 em memória.

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Com a aplicação no ar:

- **API / painel**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Console H2**: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:devdb`, usuário `sa`, senha em branco)

Rodar os testes:

```bash
.\mvnw.cmd test
```

## 🧪 Perfis

| Perfil | Banco                    | Uso                                                        |
|--------|--------------------------|------------------------------------------------------------|
| `dev`  | H2 em memória (padrão)   | Desenvolvimento local; dados mockados recriados a cada boot |
| `test` | H2 em memória            | Testes automatizados (sem console e sem log de SQL)         |
| `prod` | PostgreSQL / Supabase    | Produção; credenciais vindas do ambiente                    |

Nos perfis `dev` e `test`, a classe `TestConfig` popula o banco automaticamente com 3 categorias, 5 produtos, 2 usuários, 3 pedidos, 4 itens de pedido e 1 pagamento — útil para explorar a API sem cadastrar nada à mão. Ela nunca roda em `prod`.

### Configuração do perfil `prod` (Supabase)

Nenhuma credencial fica no código. O perfil `prod` lê tudo do ambiente:

| Variável           | Padrão   | Obrigatória |
|--------------------|----------|-------------|
| `DB_URL`           | —        | **sim**     |
| `DB_USERNAME`      | —        | **sim**     |
| `DB_PASSWORD`      | —        | **sim**     |
| `DB_POOL_SIZE`     | `5`      | não         |
| `DB_POOL_MIN_IDLE` | `1`      | não         |
| `DDL_AUTO`         | `update` | não         |
| `PORT`             | `8080`   | não         |

Sem essas variáveis a aplicação falha na inicialização — de propósito, para que nenhuma credencial padrão acabe versionada.

Para desenvolvimento, copie `.env.example` para `.env` e preencha; o `.env` é lido automaticamente (via `spring-dotenv`) e está no `.gitignore` — só o `.env.example` (sem valores reais) é versionado. Em produção, prefira definir as variáveis diretamente no ambiente do container ou da plataforma de deploy.

```bash
cp .env.example .env
```

Os dados de conexão estão no Supabase em **Project Settings → Database → Connection string**. Para containers, use o **Transaction Pooler** (porta 6543) — a conexão direta é IPv6-only no plano gratuito. Com o pooler em modo transaction, é obrigatório incluir `prepareThreshold=0` na URL:

```
DB_URL=jdbc:postgresql://aws-0-<regiao>.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0
DB_USERNAME=postgres.<project-ref>
```

> ⚠️ **Windows**: salve o `.env` em UTF-8 **sem BOM**. O `Out-File -Encoding utf8` do PowerShell 5.1 grava BOM, e o leitor do `.env` rejeita a primeira linha com `Malformed entry`, derrubando a aplicação.

## 🐳 Docker

A imagem é multi-stage: compila com Maven e entrega só o JAR sobre uma JRE, rodando como usuário sem privilégios. **Nenhuma credencial vai para a imagem** — elas são injetadas em tempo de execução.

```bash
# Build
docker build -t sistema-de-pedidos .

# Run (variáveis do arquivo .env local)
docker run --rm -p 8080:8080 --env-file .env sistema-de-pedidos

# Ou, com Docker Compose
docker compose up --build
```

O container já sobe com `SPRING_PROFILES_ACTIVE=prod`. Para rodar a imagem com H2, sobrescreva o perfil:

```bash
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev sistema-de-pedidos
```

## 🖥 Frontend

Painel administrativo estático em `src/main/resources/static`, servido pelo próprio Spring Boot:

- `index.html` — dashboard
- `html/pedidos.html` — listagem de pedidos
- `html/clientes.html` — listagem de clientes

O script `js/app.js` consome a API via axios e decide o que carregar conforme a página aberta. O mapeamento dos campos ainda espera um formato diferente do que a API devolve hoje (`status`/`date`/`clientName` em vez de `orderStatus`/`moment`/`client`), então as tabelas ainda não são preenchidas corretamente.

## 💭 Futuramente

1. Frontend integrado à API
2. CRUD completo para pedidos, produtos e categorias
3. Camada de DTOs, validação de entrada e autenticação
4. Testes automatizados de serviços e endpoints
5. Migrations com Flyway, para substituir o `ddl-auto=update` em produção
6. Deploy da imagem Docker
