# Aplicação com RabbitMQ

## Guia de uso

### Apenas clone esse repositório e dê um docker compose up na pasta em que está o docker-compose.yml.

### Envie mensagens utilizando o endereço http://localhost:8080/ seguido da mensagem que deseja enviar.

### Acesse o console de gerenciamento do RabbitMQ em http://localhost:15672.

### Você verá a fila de deadletter crescer a medida em que erros propositais e eventuais de processamento da mensagem acontecerão no lado da aplicação consumidora de mensagens.
