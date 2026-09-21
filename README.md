# Store Application

The Store application manages customers, orders and products using a PostgreSQL database.

## Assumptions

This README assumes you're using a POSIX environment. On Windows, use `gradlew.bat` instead of `./gradlew`.

The application expects PostgreSQL 16.2 to be running on `localhost:5433`, with the username and password `admin:admin` and a database named `store`.

## Prerequisites

You will need Java 17 and Docker to run the application using the instructions below.

Start PostgreSQL using Docker:

```bash
docker run -d \
  --name postgres \
  --restart always \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=store \
  -v postgres:/var/lib/postgresql/data \
  -p 5433:5432 \
  postgres:16.2 \
  postgres -c wal_level=logical
```

## Running the application

To run the application locally:

```bash
./gradlew bootRun
```

Liquibase handles database migrations when the application starts. Some sample data is included. More data can be generated using the instructions in `utils/README.md`.

## Running with Docker

The application image is available from GitHub Container Registry.

```bash
docker pull ghcr.io/kailan1001101/securitease-assessment:latest
```

If you're using the PostgreSQL container above, create a network and connect the database:

```bash
docker network create securitease-network
docker network connect securitease-network postgres
```

Then start the application:

```bash
docker run --name securitease-app \
  --network securitease-network \
  -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://postgres:5432/store \
  -e DB_USERNAME=admin \
  -e DB_PASSWORD=admin \
  ghcr.io/kailan1001101/securitease-assessment:latest
```

The application will be available at `http://localhost:8080`.

The database runs separately from the application container.

## Data model

A customer has an ID and a name, and can have multiple orders.

An order has an ID and description, belongs to a customer, and can contain multiple products.

A product has an ID and description, and can be associated with multiple orders.

## API

### Customers

| Method | Endpoint | Description |
|---|---|---|
| GET | `/customer` | Get customers |
| GET | `/customer?query=John` | Search customers by name |
| POST | `/customer` | Create a customer |

The search matches part of a customer's name and is case-insensitive.

### Orders

| Method | Endpoint | Description |
|---|---|---|
| GET | `/order` | Get orders |
| GET | `/order/{id}` | Get an order by ID |
| POST | `/order` | Create an order |

Order responses include the customer and products associated with the order.

### Products

| Method | Endpoint | Description |
|---|---|---|
| GET | `/products` | Get all products |
| GET | `/products/{id}` | Get a product by ID |
| POST | `/products` | Create a product |

Product responses include the IDs of the orders that contain the product.

See `OpenAPI.yaml` for more details about the API.

## Performance investigation

I investigated the slow GET endpoints by looking at Hibernate SQL logs and PostgreSQL query execution plans.

This helped identify opportunities to reduce database queries. I configured Hibernate batch fetching to reduce the number of database round trips.

Further improvements could be considered based on the production workload, especially given the latency between the application and database servers.

## CI pipeline and Docker image

A GitHub Actions workflow runs the tests and builds the application and Docker image.

When changes are pushed to `main`, the workflow also publishes the image to GitHub Container Registry. Pull requests run the validation steps without publishing an image.

Docker image:

`ghcr.io/kailan1001101/securitease-assessment:latest`
