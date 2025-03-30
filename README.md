# Library Book Reservation Service

## Architecture
![Architecture](docs/images/architecture.png)

### Reservation-api
The reservation-api microservice handles HTTP REST requests and enforces core business rules. It interacts with the database for data persistence, uses Redis for caching, and communicates with RabbitMQ to asynchronously send information to the notification service.

### Reservation-notification
The reservation-notification microservice receives messages via RabbitMQ and simulates the sending of notifications through multiple channels, such as email and SMS, with the flexibility to support additional channels if needed. It's main responsibility is to inform users about changes in the reservation.

### Reservation-batch
The reservation-batch microservice periodically runs batch executions to update the status of expired reservations, ensuring data consistency and proper reservation lifecycle management.

## Languages and tools
- Backend: Java 21, Spring Boot
- Database: PostgreSQL
- Queue: RabbitMQ
- Cache: Redis
- Build and run: Maven, Docker, scripts `.sh` (unix) and `.bat` (windows)

## How to run

#### Requirements: Java 21, Docker, Docker Compose

Steps:
- If an unix machine execute `./build-and-run.sh`. You may have to give it permission: `chmod +x build-and-run.sh`
- If an windows machine execute `build-and-run.bat`

The script will build all projects and then call docker-compose to build the images and execute all the services.

## Endpoints (with examples)

Create a reservation
```
curl --request POST \
  --url http://localhost:8080/reservations \
  --header 'Content-Type: application/json' \
  --data '{
	"userId": 1,
	"books": [
		{
			"bookId": 1,
			"quantity": 1
		}
	]
}'
```

Find specific reservation
```
curl --request GET \
  --url http://localhost:8080/reservations/1
```

Find all reservation from an user
```
curl --request GET \
  --url 'http://localhost:8080/reservations/user/1'
```

Pick up a reservation
```
curl --request GET \
  --url http://localhost:8080/reservations/pickup/1
```

Cancel a reservation
```
curl --request DELETE \
  --url http://localhost:8080/reservations/1
```

## Important info
1. The database is generated already with two books and two users to make it easy to start to use :)
2. Unit tests were implemented for the services, along with integration tests for the database. However, due to time constraints, only unit tests were added for cache and queue communications. Ideally, integration tests should also be included for these components using technologies like [Testcontainers](https://testcontainers.com).
3. I decided not to use Lombok in the project by my own choice, but it's quite common to use this type of library nowadays.
4. There are many other features that could have been added to this project, such as [Flyway](https://www.red-gate.com/products/flyway/), additional microservices like Catalog (using ELK, MongoDB, or another data source), Authentication, User, Payment, and even an API Gateway. However, I believe these go beyond the scope of the challenge and would require significantly more time to implement.