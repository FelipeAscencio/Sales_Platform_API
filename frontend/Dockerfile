# Stage 1: Build the React app
FROM node:22-alpine AS build

# Set build arguments
ARG VITE_API_URL
ENV VITE_API_URL=${VITE_API_URL}

# Set working directory
WORKDIR /app

# Copy dependency files
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install

# Copy the application code
COPY . .

# Build the React application
RUN npm run build

# Stage 2: Serve the React app using Caddy
FROM caddy:2.7.4-alpine

# Set Caddy's default root path to serve static files
WORKDIR /srv

# Copy the build output from the first stage
COPY --from=build /app/dist .

# Copy the Caddyfile into the image
COPY Caddyfile /etc/caddy/Caddyfile

# Expose port 80
EXPOSE 80

# Command to run Caddy using the provided Caddyfile
CMD ["caddy", "run", "--config", "/etc/caddy/Caddyfile", "--adapter", "caddyfile"]
