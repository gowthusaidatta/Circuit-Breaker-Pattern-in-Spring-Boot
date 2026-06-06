# Circuit Breaker Pattern in Spring Boot

This project demonstrates the Circuit Breaker pattern in Spring Boot using Resilience4j. The application exposes a user API, a circuit-breaker state endpoint, a metrics endpoint, and a controllable mock external backend used to simulate failures.

## Build

Use Maven to compile and package the application:

```bash
mvn clean package
```

The runnable jar is created in `target/`.

## Run

Start the application with Spring Boot:

```bash
mvn spring-boot:run
```

Or run the packaged jar:

```bash
java -jar target/circuit-breaker-pattern-0.0.1-SNAPSHOT.jar
```

The application listens on `http://localhost:8080` by default.

## Endpoints

- `GET /api/users/{id}` returns user data protected by the `userService` circuit breaker.
- `GET /api/circuit-breaker/state` returns the current state as plain text.
- `GET /api/circuit-breaker/metrics` returns a simplified metrics JSON payload.
- `GET /actuator/circuitbreakers` exposes the Resilience4j actuator view.
- `POST /mock/external/mode/fail` switches the mock backend into failing mode.
- `POST /mock/external/mode/healthy` switches the mock backend back to success mode.

## Test

Use the provided script to demonstrate the breaker opening, falling back, and closing again:

```bash
chmod +x test-circuit-breaker.sh
./test-circuit-breaker.sh
```

You can also point it at another host if needed:

```bash
./test-circuit-breaker.sh http://localhost:8080
```

Expected behavior:

1. The mock backend is switched to failing mode.
2. Repeated calls to `GET /api/users/1` open the circuit breaker.
3. Calls while open return the fallback `UserDTO` with HTTP 200.
4. After the open-state wait duration, the backend is switched back to healthy mode.
5. Successful probe calls move the breaker through `HALF_OPEN` and back to `CLOSED`.

## Configuration

Circuit breaker settings are defined in `src/main/resources/application.yml`.

Key values for `userService`:

- failure-rate-threshold: `50`
- minimum-number-of-calls: `5`
- sliding-window-type: `COUNT_BASED`
- sliding-window-size: `10`
- wait-duration-in-open-state: `10s`

## Notes

- The application uses a local mock backend instead of an external public API so failure scenarios are deterministic.
- Logs are emitted when the `userService` circuit breaker changes state.