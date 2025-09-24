#!/usr/bin/env bash

set -euo pipefail

BASE_URL="http://localhost:8080"
ADMIN_USER="${ADMIN_USER:-admin}"
ADMIN_PASS="${ADMIN_PASS:-Admin@123}"
CSV_FILE="/tmp/customers.csv"

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

pass() { echo -e "${GREEN}PASS${NC} - $1"; }
fail() { echo -e "${RED}FAIL${NC} - $1"; exit 1; }

need_cmd() { command -v "$1" >/dev/null 2>&1 || fail "Required command missing: $1"; }

need_cmd curl
need_cmd jq

wait_for_up() {
  local url="$1"; local max=30; local i=0
  until curl -sS "$url/actuator/health" | jq -e '.status=="UP"' >/dev/null 2>&1; do
    i=$((i+1));
    if [ "$i" -ge "$max" ]; then
      echo "Actuator health not available or not UP, proceeding with base URL ping..." >&2
      break
    fi
    sleep 1
  done
  # Fallback check: base URL should respond something
  curl -sS "$url" >/dev/null || fail "Service not reachable at $url"
}

json() { jq -c .; }

step_bootstrap_admin() {
  local code
  code=$(curl -sS -o /dev/null -w '%{http_code}' -X POST "$BASE_URL/api/auth/bootstrap-admin" \
    -H 'Content-Type: application/json' \
    -d "{\"username\":\"$ADMIN_USER\",\"password\":\"$ADMIN_PASS\"}") || true
  # 200 or 400 (user exists) are acceptable
  if [ "$code" = "200" ]; then pass "Bootstrap admin"; else
    if [ "$code" = "400" ]; then pass "Bootstrap admin (already exists)"; else fail "Bootstrap admin ($code)"; fi
  fi
}

step_login_admin() {
  ADMIN_TOKEN=$(curl -sS -X POST "$BASE_URL/api/auth/login" \
    -H 'Content-Type: application/json' \
    -d "{\"username\":\"$ADMIN_USER\",\"password\":\"$ADMIN_PASS\"}" | jq -r .token)
  [ -n "${ADMIN_TOKEN:-}" ] && [ "$ADMIN_TOKEN" != "null" ] || fail "Admin login token missing"
  pass "Admin login"
}

step_create_customer() {
  CUSTOMER_ID=$(curl -sS -X POST "$BASE_URL/api/customers" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H 'Content-Type: application/json' \
    -d '{
      "fullName":"John Doe",
      "email":"john.doe@example.com",
      "phone":"+255700000001",
      "address":"Dar es Salaam",
      "dateOfBirth":"1990-01-15"
    }' | jq -r .id)
  [ -n "${CUSTOMER_ID:-}" ] && [ "$CUSTOMER_ID" != "null" ] || fail "Customer creation failed"
  pass "Create customer (id=$CUSTOMER_ID)"
}

step_list_customers() {
  local count
  count=$(curl -sS -X GET "$BASE_URL/api/customers" -H "Authorization: Bearer $ADMIN_TOKEN" | jq 'length')
  [ "${count:-0}" -ge 1 ] || fail "List customers returned empty"
  pass "List customers ($count)"
}

step_upload_csv() {
  cat > "$CSV_FILE" <<'CSV'
fullName,email,phone,address,dateOfBirth
Jane Doe,jane.doe@example.com,+255700000002,Dar,1992-03-02
Mark Joe,mark.joe@example.com,+255700000003,Arusha,1988-07-20
CSV
  local res
  res=$(curl -sS -X POST "$BASE_URL/api/customers/upload" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -F "file=@$CSV_FILE;type=text/csv")
  echo "$res" | grep -qi "Imported" || fail "CSV upload did not report Imported"
  pass "Bulk upload customers"
}

step_create_accounts() {
  # Create two accounts for transfer tests
  ACC1=$(curl -sS -X POST "$BASE_URL/api/accounts" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H 'Content-Type: application/json' \
    -d "{\"customerId\":$CUSTOMER_ID,\"type\":\"SAVINGS\"}" | jq -r .accountNumber)
  [ -n "${ACC1:-}" ] && [ "$ACC1" != "null" ] || fail "Account1 creation failed"

  ACC2=$(curl -sS -X POST "$BASE_URL/api/accounts" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H 'Content-Type: application/json' \
    -d "{\"customerId\":$CUSTOMER_ID,\"type\":\"CURRENT\"}" | jq -r .accountNumber)
  [ -n "${ACC2:-}" ] && [ "$ACC2" != "null" ] || fail "Account2 creation failed"

  pass "Create accounts ($ACC1, $ACC2)"
}

step_list_accounts() {
  local cnt
  cnt=$(curl -sS -X GET "$BASE_URL/api/accounts/by-customer/$CUSTOMER_ID" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq 'length')
  [ "${cnt:-0}" -ge 2 ] || fail "List accounts returned < 2"
  pass "List accounts ($cnt)"
}

step_credit() {
  local code
  code=$(curl -sS -o /dev/null -w '%{http_code}' -X POST "$BASE_URL/api/transactions/credit" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H 'Content-Type: application/json' \
    -d "{\"accountNumber\":\"$ACC1\",\"amount\":100000.00,\"description\":\"Initial deposit\"}")
  [ "$code" = "200" ] || fail "Credit returned $code"
  pass "Credit $ACC1"
}

step_debit() {
  local code
  code=$(curl -sS -o /dev/null -w '%{http_code}' -X POST "$BASE_URL/api/transactions/debit" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H 'Content-Type: application/json' \
    -d "{\"accountNumber\":\"$ACC1\",\"amount\":1000.00,\"description\":\"ATM withdrawal\"}")
  [ "$code" = "200" ] || fail "Debit returned $code"
  pass "Debit $ACC1"
}

step_transfer_request() {
  local code
  code=$(curl -sS -o /dev/null -w '%{http_code}' -X POST "$BASE_URL/api/transactions/transfer/request" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H 'Content-Type: application/json' \
    -H 'X-Requested-By: requesterUser' \
    -d "{\"fromAccount\":\"$ACC1\",\"toAccount\":\"$ACC2\",\"amount\":5000.00}")
  [ "$code" = "200" ] || fail "Transfer request returned $code"
  pass "Transfer requested"
}

step_transfer_approve() {
  local code
  code=$(curl -sS -o /dev/null -w '%{http_code}' -X POST "$BASE_URL/api/transactions/transfer/approve" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H 'Content-Type: application/json' \
    -H 'X-Approved-By: approverAdmin' \
    -d "{\"fromAccount\":\"$ACC1\",\"toAccount\":\"$ACC2\",\"amount\":5000.00}")
  [ "$code" = "200" ] || fail "Transfer approve returned $code"
  pass "Transfer approved"
}

step_negative_unauth() {
  local code
  code=$(curl -sS -o /dev/null -w '%{http_code}' "$BASE_URL/api/customers")
  [ "$code" = "401" ] || fail "Expected 401 without token, got $code"
  pass "401 without token"
}

main() {
  echo "== Waiting for service..."
  wait_for_up "$BASE_URL"

  echo "== Bootstrapping admin"
  step_bootstrap_admin

  echo "== Logging in admin"
  step_login_admin

  echo "== Creating customer"
  step_create_customer

  echo "== Listing customers"
  step_list_customers

  echo "== Bulk uploading customers"
  step_upload_csv

  echo "== Creating accounts"
  step_create_accounts

  echo "== Listing accounts"
  step_list_accounts

  echo "== Crediting account"
  step_credit

  echo "== Debiting account"
  step_debit

  echo "== Transfer request"
  step_transfer_request

  echo "== Transfer approve"
  step_transfer_approve

  echo "== Negative tests"
  step_negative_unauth

  echo -e "${GREEN}ALL TESTS PASSED${NC}"
}

main "$@" 