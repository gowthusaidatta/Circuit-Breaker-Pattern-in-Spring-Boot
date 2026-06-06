#!/usr/bin/env bash

set -euo pipefail

BASE_URL="${1:-http://localhost:8080}"

echo "Switching mock backend to failing mode"
curl -sS -X POST "${BASE_URL}/mock/external/mode/fail" >/dev/null

echo "Initial circuit breaker state: $(curl -sS "${BASE_URL}/api/circuit-breaker/state")"
echo
echo "Triggering failures to open the circuit breaker"

for attempt in 1 2 3 4 5 6; do
  echo "Request ${attempt}:"
  curl -sS "${BASE_URL}/api/users/1"
  echo
done

echo "State after failures: $(curl -sS "${BASE_URL}/api/circuit-breaker/state")"

echo "Waiting for the circuit breaker to move from OPEN to HALF_OPEN..."
sleep 11

echo "Switching mock backend back to healthy mode"
curl -sS -X POST "${BASE_URL}/mock/external/mode/healthy" >/dev/null

echo "Half-open probe 1:"
curl -sS "${BASE_URL}/api/users/1"
echo
echo "State after probe 1: $(curl -sS "${BASE_URL}/api/circuit-breaker/state")"

echo "Half-open probe 2:"
curl -sS "${BASE_URL}/api/users/1"
echo
echo "Final circuit breaker state: $(curl -sS "${BASE_URL}/api/circuit-breaker/state")"

echo "Actuator circuit breaker overview:"
curl -sS "${BASE_URL}/actuator/circuitbreakers"
echo