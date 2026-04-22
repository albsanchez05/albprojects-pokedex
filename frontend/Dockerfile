# --- STAGE 1: Build ---
# Use official Node image to install dependencies and build the Angular app
FROM node:20-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy package files first to leverage Docker layer caching
COPY package.json package-lock.json ./

# Install all npm dependencies
RUN npm ci

# Copy the rest of the source code
COPY . .

# Build the Angular app for production (outputs to dist/frontend/browser)
RUN npm run build -- --configuration production


# --- STAGE 2: Serve ---
# Use a lightweight nginx image to serve the static build output
FROM nginx:1.27-alpine

# Remove the default nginx welcome page
RUN rm -rf /usr/share/nginx/html/*

# Copy the Angular build output from the build stage
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Copy our custom nginx config (includes SPA fallback + API reverse proxy)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose the default HTTP port
EXPOSE 80

# nginx runs in foreground by default in the official image
CMD ["nginx", "-g", "daemon off;"]
