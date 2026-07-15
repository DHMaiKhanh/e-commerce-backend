# E-commerce Backend (Java)

Spring Boot 3.3 / Java 17 backend scaffold following an enterprise-grade layered architecture.

## Stack

- Spring Boot 3.3.5, Java 17, Maven
- Spring Web, Validation, Data JPA, Security (JWT), Data Redis, Cache
- MapStruct, Lombok
- Flyway (MySQL), Hibernate
- springdoc-openapi (Swagger UI)
- Micrometer + Prometheus, Actuator
- AOP (logging/perf), WebClient (external HTTP)
- Testcontainers for integration tests

## Project structure

```
com.yourdomain.ecommerce
├── config/         Spring configuration (Web, OpenAPI, Async, Jackson, AppProperties)
├── constants/      Project-wide constants
├── controller/     REST controllers (versioned under /v1)
├── dto/
│   ├── request/    Inbound DTOs (with bean validation)
│   └── response/   Outbound DTOs
├── entity/         JPA entities (extend BaseEntity for audit)
├── enums/          Shared enums
├── exception/      ErrorCode, BusinessException, GlobalExceptionHandler
├── mapper/         MapStruct mappers (central config in CentralMapperConfig)
├── repository/     Spring Data JPA repositories
├── service/
│   ├── impl/       Service implementations
│   └── helper/     Small helpers for service-layer logic
├── security/       JWT provider/filter, SecurityConfig, UserDetailsService
├── specification/  JPA Specifications for dynamic filtering
├── utils/          Stateless utility classes
├── validation/     Custom Bean Validation annotations
├── event/          ApplicationEvents + listeners (async)
│   └── listener/
├── scheduler/      @Scheduled cron jobs
├── cache/          Redis cache config + cache name constants
├── integration/    Third-party service abstractions (e.g. payment)
├── client/         Outbound HTTP clients (WebClient)
├── audit/          AuditorAware for createdBy/updatedBy
├── logging/        Request logging filter, MDC setup
├── aspect/         AOP: logging, performance
└── common/         BaseEntity, ApiResponse, PageResponse
```

## Running locally

### Option A: Docker Compose (mysql + redis + app)

```bash
docker compose up --build
```

App: http://localhost:8080/api  ·  Swagger: http://localhost:8080/api/swagger-ui.html

### Option B: Run app from IDE, infra in Docker

```bash
docker compose up -d mysql redis
./mvnw spring-boot:run
```

### Required env vars (prod)

| Variable          | Description                                      |
|-------------------|--------------------------------------------------|
| `DB_URL`          | JDBC URL                                         |
| `DB_USERNAME`     | DB user                                          |
| `DB_PASSWORD`     | DB password                                      |
| `REDIS_HOST`      | Redis host                                       |
| `REDIS_PORT`      | Redis port (default 6379)                        |
| `REDIS_PASSWORD`  | Redis password                                   |
| `JWT_SECRET`      | HMAC secret, **≥ 32 bytes** (256 bits)           |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins             |

## Conventions

- **Layering:** controller → service (interface + impl) → repository → entity. DTO only crosses the controller boundary.
- **Mapping:** entity ⇄ DTO via MapStruct mappers in `mapper/`.
- **Errors:** throw `BusinessException(ErrorCode.X)`; `GlobalExceptionHandler` produces consistent `ApiResponse` payloads.
- **Pagination:** controllers accept `Pageable`, services return `Page<T>`, controllers wrap with `PageResponse.of(...)`.
- **Audit:** all persistent entities extend `BaseEntity` (createdAt/updatedAt/createdBy/updatedBy).
- **Auth:** `/v1/auth/login` → access + refresh JWT; protected endpoints require `Bearer <token>`.
- **Versioning:** all REST under `/v1` via `AppConstants.API_V1`.
- **Migrations:** add `V{n}__description.sql` under `src/main/resources/db/migration`.

## Useful endpoints

- `GET /api/actuator/health`
- `GET /api/actuator/prometheus`
- `GET /api/swagger-ui.html`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh?refreshToken=...`

## CI/CD (GitHub Actions)

Pipeline: `.github/workflows/ci-cd.yml`. Triggers:

- **PR → main**: chỉ chạy job `test` (build + Maven verify).
- **Push → main**: chạy `test` → build & push Docker image lên GHCR → SSH deploy lên VPS → health check.

Image được publish ở `ghcr.io/<owner>/<repo>` với tags: `latest`, `sha-<shortsha>`, `<YYYYMMDD-HHmmss>`.

### Required GitHub Secrets

Vào **Settings → Secrets and variables → Actions** thêm các secret sau:

| Secret | Mục đích |
|---|---|
| `GHCR_PAT` | Personal Access Token (scope `read:packages`) để VPS pull image private từ GHCR |
| `GHCR_USER` | GitHub username sở hữu PAT |
| `DEPLOY_HOST` | IP / domain VPS |
| `DEPLOY_USER` | SSH user (vd: `deploy`, `ubuntu`) |
| `DEPLOY_SSH_KEY` | Private key SSH (toàn bộ nội dung `id_ed25519`) |
| `DEPLOY_PORT` | SSH port (thường `22`) |
| `DEPLOY_PATH` | Thư mục deploy trên server, vd: `/opt/ecommerce` |
| `DB_URL` | `jdbc:mysql://...` |
| `DB_USERNAME`, `DB_PASSWORD` | Credentials DB |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD` | Redis |
| `JWT_SECRET` | HMAC secret ≥ 32 bytes |
| `CORS_ALLOWED_ORIGINS` | Origins cho phép (comma-separated) |
| `HEALTHCHECK_URL` | URL `/api/actuator/health` công khai để smoke test |

### Setup VPS lần đầu

```bash
# Trên VPS
sudo apt update && sudo apt install -y docker.io docker-compose-plugin
sudo usermod -aG docker $USER   # logout/login lại sau lệnh này
mkdir -p /opt/ecommerce
```

Đảm bảo MySQL + Redis chạy sẵn (managed service hoặc compose riêng). File `docker-compose.prod.yml` ở repo này **chỉ chạy app**, không bao gồm DB/Redis cho production.

### Migrate lên Kubernetes sau này

Image vẫn ở GHCR — chỉ cần thay job `deploy` trong workflow bằng bước `kubectl`:

```yaml
- uses: azure/k8s-set-context@v4
  with:
    kubeconfig: ${{ secrets.KUBE_CONFIG }}
- run: |
    kubectl set image deployment/ecommerce app=ghcr.io/${{ github.repository }}:sha-${{ github.sha }}
    kubectl rollout status deployment/ecommerce --timeout=180s
```

Các file Maven/Dockerfile/code không thay đổi.
