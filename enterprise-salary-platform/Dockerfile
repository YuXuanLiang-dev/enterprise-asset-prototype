# Guide 要求 Node 18+，我们用 20 LTS
FROM node:20 AS builder
WORKDIR /app
# 设置淘宝源防止卡死
RUN npm config set registry https://registry.npmmirror.com
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Nginx 负责反向代理
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
