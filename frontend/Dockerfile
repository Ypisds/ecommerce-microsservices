# =========================================
# Stage 1: Base & Dependencies
# =========================================
ARG NODE_VERSION=24.14.1-alpine
FROM node:${NODE_VERSION} AS base

WORKDIR /app

# Copia arquivos de dependência
COPY package.json *package-lock.json* ./

# Instala dependências usando cache do Docker
RUN --mount=type=cache,target=/root/.npm npm ci

# =========================================
# Stage 2: Development (Live Reload / HMR)
# =========================================
FROM base AS development

# Copia o código da aplicação
COPY . .

EXPOSE 4200

# Executa o servidor do Angular liberando acesso externo e polling para o Docker
CMD ["npx", "ng", "serve", "--host", "0.0.0.0", "--port", "4200", "--poll", "1000"]

# =========================================
# Stage 3: Production Build
# =========================================
FROM base AS builder

COPY . .
RUN npm run build

# =========================================
# Stage 4: Serve Static Files (Production)
# =========================================
FROM node:${NODE_VERSION} AS runner

WORKDIR /app
RUN npm install -g serve
COPY --from=builder /app/dist/*/browser ./public

EXPOSE 4200
CMD ["serve", "-s", "public", "-l", "4200"]