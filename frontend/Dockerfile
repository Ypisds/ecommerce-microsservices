# =========================================
# Stage 1: Build the Angular Application
# =========================================
ARG NODE_VERSION=24.14.1-alpine
ARG NGINX_VERSION=alpine3.22

# Use a lightweight Node.js image for building (customizable via ARG)
FROM node:${NODE_VERSION} AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy package-related files first to leverage Docker's caching mechanism
COPY package.json *package-lock.json* ./

# Install project dependencies using npm ci (ensures a clean, reproducible install)
RUN --mount=type=cache,target=/root/.npm npm ci

# Copy the rest of the application source code into the container
COPY . .

# Build the Angular application
RUN npm run build

# =========================================
# Stage 2: Serve Static Files
# =========================================
FROM node:${NODE_VERSION} AS runner

WORKDIR /app

# Instala um servidor HTTP minimalista
RUN npm install -g serve

# Copia apenas os arquivos estáticos gerados no build
COPY --from=builder /app/dist/*/browser ./public

# Expose a porta interna do serviço frontend
EXPOSE 4200

# Executa o 'serve' apontando para a pasta dos arquivos estáticos com suporte a SPA (-s)
CMD ["serve", "-s", "public", "-l", "4200"]