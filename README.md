# Campus Equipment Booking Service

Java 17 / Spring Boot REST API for a CI/CD lab assessment.

## Requirements

- Java 17
- Maven 3.10+
- Docker Desktop
- Jenkins
- kubectl
- Minikube

## Build and Test

Run the unit and API tests:

```powershell
mvn clean test
```

Package the application:

```powershell
mvn clean package
```

## Run Locally

Start the application:

```powershell
mvn spring-boot:run
```

The application listens on port `8080` by default.

Health endpoint:

```text
http://localhost:8080/actuator/health
```

## REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/equipment` | List available equipment |
| GET | `/api/equipment/{id}` | Get equipment by ID |
| POST | `/api/bookings` | Create a booking |
| GET | `/api/bookings/{id}` | Get a booking by ID |
| POST | `/api/bookings/{id}/cancel` | Cancel a booking |
| GET | `/actuator/health` | Check application health |

Booking quantities must be positive. Requests with zero or negative quantities are rejected with HTTP 400 Bad Request.

Inventory is held in memory and resets when the application restarts. No database is required.

## Docker

Build the application JAR and Docker image:

```powershell
mvn clean package
docker build -t campus-equipment-booking:set-e .
```

Run the container on host port `8081` because Jenkins uses port `8080`:

```powershell
docker run -d --name campus-booking-app -p 8081:8080 campus-equipment-booking:set-e
```

Check application health:

```powershell
Invoke-RestMethod http://localhost:8081/actuator/health
```

Expected status: `UP`.

To stop and remove the test container:

```powershell
docker stop campus-booking-app
docker rm campus-booking-app
```

## Jenkins CI Pipeline

The Jenkins job is named `Campus-Equipment-Booking-CI`.

The pipeline:
1. Verifies Java, Maven, Git and Docker.
2. Runs `mvn clean test`.
3. Packages the application.
4. Builds the Docker image tagged `campus-equipment-booking:set-e`.

Run the Jenkins job and review its Console Output for a successful build.

## Kubernetes with Minikube

Start Minikube:

```powershell
minikube start --driver=docker
```

Load the Docker image into Minikube:

```powershell
minikube image load campus-equipment-booking:set-e
```

Apply the Deployment and Service:

```powershell
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

Check the deployment and pods:

```powershell
kubectl get deployment campus-equipment-booking
kubectl get pods
kubectl get services
```

The Deployment is configured for one replica initially. Scale it to two replicas:

```powershell
kubectl scale deployment campus-equipment-booking --replicas=2
kubectl get deployment campus-equipment-booking
```

Wait until the Deployment reports `2/2` ready and `2` available replicas.

Access the Service URL:

```powershell
minikube service campus-equipment-booking --url
```

Append `/actuator/health` to the returned URL to verify the application health. The Service uses NodePort `30081` because NodePort `30080` was already allocated to the existing Nginx Service.

## Change Request and Fixes

- Corrected the booking inventory capacity check so a booking cannot exceed available inventory.
- Added tests for booking exactly the available quantity and rejecting overbooking without changing inventory or booking state.
- Verified that zero and negative booking quantities are rejected at the service and API levels.
- Added API tests verifying successful bookings return HTTP 201 Created and invalid quantities return HTTP 400 Bad Request.
- Added a Java 17 Docker image build and Kubernetes Deployment and Service configurations.
