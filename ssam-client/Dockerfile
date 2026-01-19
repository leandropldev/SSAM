FROM node:20-alpine

WORKDIR /app

# Copia package.json e package-lock.json primeiro para aproveitar cache
COPY package*.json ./

RUN npm install

# Copia o restante do projeto
COPY . .

EXPOSE 4200

# Rodar o Angular em modo dev
CMD ["npm", "run", "start", "--", "--host", "0.0.0.0", "--port", "4200"]