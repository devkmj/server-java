#!/usr/bin/env bash
set -euo pipefail

COMPOSE_FILE=docker-compose.redis-cluster.yml

echo "🧹 Removing old containers..."
docker-compose -f "$COMPOSE_FILE" down --remove-orphans

echo "🧼 Cleaning out old data & configs..."
for i in {0..5}; do
  rm -rf "./redis-cluster/node$i/data"
  rm -f  "./redis-cluster/node$i/redis.conf"
done

echo "🛠 Generating redis.conf for each node..."
for i in {0..5}; do
  mkdir -p "./redis-cluster/node$i/data"

  # internal container ports
  internal_port="700$i"
  internal_bus="1700$i"

  # host-mapped announce ports (node0 → 7100/17000)
  # redis-node-$i
  if [ "$i" -eq 0 ]; then
    announce_port="7100"
    announce_bus="17000"
  else
    announce_port="$internal_port"
    announce_bus="$internal_bus"
  fi

  cat > "./redis-cluster/node$i/redis.conf" <<EOF
port $internal_port
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
protected-mode no

# advertise the Docker-DNS hostname, which all nodes can resolve
cluster-announce-ip redis-node-$i
cluster-announce-port $announce_port
cluster-announce-bus-port $announce_bus

bind 0.0.0.0
EOF

done

echo "🚀 Starting all Redis containers..."
docker-compose -f "$COMPOSE_FILE" up -d

wait_for_redis() {
  local name=$1
  local prt=$2
  echo "⏳ Waiting for $name on port $prt"
  until docker exec "$name" redis-cli -p "$prt" ping >/dev/null 2>&1; do
    sleep 0.3
  done
  echo "✅ $name is ready."
}

echo "⏳ Waiting for all Redis nodes to start…"
for i in {0..5}; do
  wait_for_redis "redis-node-$i" "700$i"
done

echo "🔍 Gathering container IPs for the cluster create command…"
NODE_ADDRS=()
for i in {0..5}; do
  ip=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' "redis-node-$i")
  NODE_ADDRS+=("${ip}:700$i")
  echo "- redis-node-$i → ${ip}:700$i"
done

echo "🔧 Creating the Redis Cluster…"
docker exec redis-node-0 \
  redis-cli --cluster create \
    "${NODE_ADDRS[@]}" \
    --cluster-replicas 1 \
    --cluster-yes

echo "✅ Redis Cluster setup complete!"