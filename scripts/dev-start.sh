#!/usr/bin/env bash
set -euo pipefail

# 一键启动（开发用）：MySQL（Docker）+ Jetty（Maven）
# - 默认端口：MySQL 3306、Web 8080
# - 默认容器名：hotelssm-mysql
# - 默认数据库：db_hotel_ssm
# - 默认 MySQL root 密码：123456

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

MYSQL_CONTAINER_NAME="hotelssm-mysql"
MYSQL_IMAGE="mysql:5.7"
MYSQL_ROOT_PASSWORD="123456"
MYSQL_DATABASE="db_hotel_ssm"

WEB_PORT="${WEB_PORT:-8080}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
RESET_MYSQL="${RESET_MYSQL:-0}"

JETTY_PID_FILE="${ROOT_DIR}/.tools/jetty.pid"
JETTY_LOG_FILE="${ROOT_DIR}/.tools/jetty.log"

echo "项目根目录：${ROOT_DIR}"

ensure_tools() {
  if command -v java >/dev/null 2>&1 && command -v mvn >/dev/null 2>&1; then
    return 0
  fi

  if [[ -d "${ROOT_DIR}/.tools/jdk-17.0.17+10" && -d "${ROOT_DIR}/.tools/apache-maven-3.9.6" ]]; then
    export JAVA_HOME="${ROOT_DIR}/.tools/jdk-17.0.17+10"
    export PATH="${ROOT_DIR}/.tools/apache-maven-3.9.6/bin:${JAVA_HOME}/bin:${PATH}"
    return 0
  fi

  echo "错误：未找到可用的 java/mvn，也未检测到 ${ROOT_DIR}/.tools 下的自带工具链。" >&2
  echo "请安装 JDK+Maven，或在 ${ROOT_DIR}/.tools 放置 jdk-17.0.17+10 与 apache-maven-3.9.6。" >&2
  exit 1
}

ensure_port_free() {
  local port="$1"
  if ss -lnt 2>/dev/null | awk '{print $4}' | grep -q ":${port}$"; then
    echo "错误：端口 ${port} 已被占用，请先释放端口或设置环境变量 WEB_PORT/MYSQL_PORT 更换端口。" >&2
    ss -lntp 2>/dev/null | grep ":${port}" || true
    exit 1
  fi
}

start_mysql() {
  if docker ps --format '{{.Names}}' | grep -qx "${MYSQL_CONTAINER_NAME}"; then
    echo "MySQL 容器已在运行：${MYSQL_CONTAINER_NAME}"
  else
    if docker ps -a --format '{{.Names}}' | grep -qx "${MYSQL_CONTAINER_NAME}"; then
      echo "启动已存在的 MySQL 容器：${MYSQL_CONTAINER_NAME}"
      if ! docker start "${MYSQL_CONTAINER_NAME}" >/dev/null; then
        echo "错误：MySQL 容器启动失败（${MYSQL_CONTAINER_NAME}）。" >&2
        echo "建议处理：" >&2
        echo "1) 删除并重建容器（会清空容器内数据）：" >&2
        echo "   RESET_MYSQL=1 ./scripts/dev-start.sh" >&2
        echo "2) 或手动删除后重试：" >&2
        echo "   REMOVE_MYSQL=1 ./scripts/dev-stop.sh && ./scripts/dev-start.sh" >&2

        if [[ "${RESET_MYSQL}" == "1" ]]; then
          echo "执行重建：删除并重新创建 MySQL 容器（${MYSQL_CONTAINER_NAME}）"
          docker rm -f "${MYSQL_CONTAINER_NAME}" >/dev/null || true
          echo "创建并启动 MySQL 容器：${MYSQL_CONTAINER_NAME}"
          docker run --name "${MYSQL_CONTAINER_NAME}" \
            -e "MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}" \
            -p "${MYSQL_PORT}:3306" \
            -d "${MYSQL_IMAGE}" \
            --character-set-server=utf8 --collation-server=utf8_general_ci >/dev/null
        else
          exit 1
        fi
      fi
    else
      ensure_port_free "${MYSQL_PORT}"
      echo "创建并启动 MySQL 容器：${MYSQL_CONTAINER_NAME}"
      docker run --name "${MYSQL_CONTAINER_NAME}" \
        -e "MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}" \
        -p "${MYSQL_PORT}:3306" \
        -d "${MYSQL_IMAGE}" \
        --character-set-server=utf8 --collation-server=utf8_general_ci >/dev/null
    fi
  fi

  echo "等待 MySQL 就绪..."
  for _ in $(seq 1 90); do
    if docker exec "${MYSQL_CONTAINER_NAME}" mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" -e "SELECT 1" >/dev/null 2>&1; then
      echo "MySQL 已就绪"
      break
    fi
    sleep 2
  done

  if ! docker exec "${MYSQL_CONTAINER_NAME}" mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" -e "SELECT 1" >/dev/null 2>&1; then
    echo "错误：MySQL 未就绪或无法连接，请检查容器日志：" >&2
    echo "docker logs \"${MYSQL_CONTAINER_NAME}\" --tail 80" >&2
    exit 1
  fi

  echo "确保数据库存在：${MYSQL_DATABASE}"
  docker exec "${MYSQL_CONTAINER_NAME}" mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" \
    -e "CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE} DEFAULT CHARACTER SET utf8;" >/dev/null

  local table_count
  table_count="$(docker exec "${MYSQL_CONTAINER_NAME}" mysql -N -s -uroot -p"${MYSQL_ROOT_PASSWORD}" \
    -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='${MYSQL_DATABASE}';" 2>/dev/null || echo "0")"

  if [[ "${table_count}" == "0" ]]; then
    echo "检测到库为空，导入初始化脚本：db_hotel_ssm.sql（UTF-8）"
    docker exec -i "${MYSQL_CONTAINER_NAME}" mysql --default-character-set=utf8 -uroot -p"${MYSQL_ROOT_PASSWORD}" \
      "${MYSQL_DATABASE}" < "${ROOT_DIR}/db_hotel_ssm.sql"
  else
    echo "检测到库已有表（${table_count}），跳过脚本导入"
  fi
}

start_jetty() {
  mkdir -p "${ROOT_DIR}/.tools"

  # 若端口被占用，尝试识别“由本项目脚本启动的 jetty:run”并自动停止
  local port_pid=""
  port_pid="$(ss -lntp 2>/dev/null | awk -v p=":${WEB_PORT}" '$4 ~ p {print $NF}' | head -n 1 | sed -n 's/.*pid=\\([0-9]\\+\\).*/\\1/p')"
  if [[ -n "${port_pid}" ]] && ps -p "${port_pid}" -o cmd= 2>/dev/null | grep -q "jetty:run" && ps -p "${port_pid}" -o cmd= 2>/dev/null | grep -q "${ROOT_DIR}"; then
    echo "检测到端口 ${WEB_PORT} 被旧 Jetty 占用（pid=${port_pid}），尝试停止..."
    kill "${port_pid}" 2>/dev/null || true
    sleep 2
    if ps -p "${port_pid}" >/dev/null 2>&1; then
      kill -9 "${port_pid}" 2>/dev/null || true
    fi
    rm -f "${JETTY_PID_FILE}" || true
  fi

  if [[ -f "${JETTY_PID_FILE}" ]]; then
    local old_pid
    old_pid="$(cat "${JETTY_PID_FILE}" 2>/dev/null || true)"
    if [[ -n "${old_pid}" ]] && ps -p "${old_pid}" >/dev/null 2>&1; then
      echo "检测到 Jetty 已在运行（pid=${old_pid}），如需重启请先执行 scripts/dev-stop.sh"
      return 0
    fi
  fi

  ensure_port_free "${WEB_PORT}"

  echo "构建项目（跳过测试）..."
  (cd "${ROOT_DIR}" && mvn -DskipTests -q package)

  echo "启动 Jetty（后台运行，日志：${JETTY_LOG_FILE}）..."
  rm -f "${JETTY_LOG_FILE}" "${JETTY_PID_FILE}" || true
  (cd "${ROOT_DIR}" && nohup mvn -DskipTests jetty:run > "${JETTY_LOG_FILE}" 2>&1 & echo $! > "${JETTY_PID_FILE}")

  local pid
  pid="$(cat "${JETTY_PID_FILE}")"
  echo "Jetty PID：${pid}"

  echo "等待 Web 就绪..."
  for _ in $(seq 1 60); do
    if [[ "$(curl -sS -o /dev/null -w "%{http_code}" "http://127.0.0.1:${WEB_PORT}/home/index" || true)" == "200" ]]; then
      echo "启动成功："
      echo "- 前台：http://127.0.0.1:${WEB_PORT}/home/index"
      echo "- 后台：http://127.0.0.1:${WEB_PORT}/system/login"
      return 0
    fi
    sleep 1
  done

  echo "错误：Web 启动超时，请查看日志：${JETTY_LOG_FILE}" >&2
  tail -n 80 "${JETTY_LOG_FILE}" || true
  exit 1
}

if ! command -v docker >/dev/null 2>&1; then
  echo "错误：未安装 docker 或 docker 不在 PATH 中。" >&2
  exit 1
fi

ensure_tools

start_mysql
start_jetty
