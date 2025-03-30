#!/bin/bash

echo "🧪 Running STAG automated test..."

# 啟動伺服器在背景
./mvnw compile exec:java@stag > server.log &
SERVER_PID=$!
sleep 1  # 等伺服器啟動

# 一行一行送出 commands.txt 裡的指令
while IFS= read -r line
do
  echo "👤 Sending command: $line"
  echo "$line" | nc localhost 8888
  echo "--------------------------------------"
done < commands.txt

# 關閉伺服器
kill $SERVER_PID