#!/usr/bin/env bash
set -euo pipefail

# 一键停止（开发用）：停止 Jetty（若存在）+ 停止 MySQL 容器（可选删除）
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

MYSQL_CONTAINER_NAME="hotelssm-mysql"
JETTY_PID_FILE="${ROOT_DIR}/.tools/jetty.pid"

STOP_MYSQL="${STOP_MYSQL:-0}"
REMOVE_MYSQL="${REMOVE_MYSQL:-0}"

stop_jetty() {
  if [[ ! -f "${JETTY_PID_FILE}" ]]; then
    # 兼容：pid 文件丢失时，尝试按端口 8080 查找由脚本启动的 Jetty 进程并停止
    local pid=""
    pid="$(ss -lntp 2>/dev/null | awk '/:8080/ {print $NF}' | head -n 1 | sed -n 's/.*pid=\\([0-9]\\+\\).*/\\1/p')"
    if [[ -n "${pid}" ]] && ps -p "${pid}" -o cmd= 2>/dev/null | grep -q "jetty:run" && ps -p "${pid}" -o cmd= 2>/dev/null | grep -q "${ROOT_DIR}"; then
      echo "未发现 Jetty PID 文件，按端口检测到 Jetty（pid=${pid}），执行停止..."
      kill "${pid}" 2>/dev/null || true
      sleep 2
      if ps -p "${pid}" >/dev/null 2>&1; then
        kill -9 "${pid}" 2>/dev/null || true
      fi
      return 0
    fi

    echo "未发现 Jetty PID 文件：${JETTY_PID_FILE}（可能未启动或已手动停止）"
    return 0
  fi

  local pid
  pid="$(cat "${JETTY_PID_FILE}" 2>/dev/null || true)"
  if [[ -z "${pid}" ]]; then
    echo "Jetty PID 文件为空：${JETTY_PID_FILE}"
    return 0
  fi

  if ps -p "${pid}" >/dev/null 2>&1; then
    echo "停止 Jetty（pid=${pid}）..."
    kill "${pid}" 2>/dev/null || true
    sleep 2
    if ps -p "${pid}" >/dev/null 2>&1; then
      echo "Jetty 未退出，强制结束（pid=${pid}）..."
      kill -9 "${pid}" 2>/dev/null || true
    fi
  else
    echo "Jetty 进程不存在（pid=${pid}），清理 pid 文件"
  fi

  rm -f "${JETTY_PID_FILE}" || true
}

stop_mysql() {
  if ! command -v docker >/dev/null 2>&1; then
    echo "docker 不存在，跳过 MySQL 容器处理"
    return 0
  fi

  if [[ "${STOP_MYSQL}" != "1" && "${REMOVE_MYSQL}" != "1" ]]; then
    echo "默认不停止 MySQL 容器（如需停止请执行：STOP_MYSQL=1 scripts/dev-stop.sh；如需删除请执行：REMOVE_MYSQL=1 scripts/dev-stop.sh）"
    return 0
  fi

  if docker ps -a --format '{{.Names}}' | grep -qx "${MYSQL_CONTAINER_NAME}"; then
    if [[ "${REMOVE_MYSQL}" == "1" ]]; then
      echo "删除 MySQL 容器：${MYSQL_CONTAINER_NAME}"
      docker rm -f "${MYSQL_CONTAINER_NAME}" >/dev/null
    else
      echo "停止 MySQL 容器：${MYSQL_CONTAINER_NAME}（如需删除请执行：REMOVE_MYSQL=1 scripts/dev-stop.sh）"
      docker stop "${MYSQL_CONTAINER_NAME}" >/dev/null
    fi
  else
    echo "未发现 MySQL 容器：${MYSQL_CONTAINER_NAME}"
  fi
}

stop_jetty
stop_mysql
