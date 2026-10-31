# Bonobús Digital

Proyecto de portfolio backend inspirado en el autobús urbano de Lorca: un bonobús digital
que el usuario puede consultar desde el móvil sin depender de la tarjeta física.

> Proyecto personal de aprendizaje. No está afiliado a ninguna empresa de transporte.

## Funcionalidad (v1)

- Crear un bono (empieza con 0 viajes)
- Recargar viajes
- Registrar un viaje (simulado mediante un endpoint)
- Consultar el bono y su historial de viajes y recargas
- Alertas: "viaje descontado" y "bono agotado"

## Arquitectura

| Servicio | Descripción | Estado |
|---|---|---|
| `bonos-service` | API REST de bonos. Spring Boot + PostgreSQL + Flyway | ✅ |
| `notificaciones-service` | Consume eventos de Kafka y genera las alertas | 🔜 |

## Stack

Java 21 · Spring Boot 4 · PostgreSQL 18 · Flyway · Docker · Kafka (próximamente)

## Cómo arrancarlo

```bash
cd bonos-service
docker compose up -d
./mvnw spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui.html
