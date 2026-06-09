FROM node:20-alpine AS build
WORKDIR /app
RUN npm install -g @quasar/cli
COPY package*.json ./
RUN npm install
COPY . .
RUN quasar build

FROM nginx:alpine
COPY --from=build /app/dist/spa /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
