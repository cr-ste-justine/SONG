RESPONSE=$(curl http://localhost:8080/isAlive)
if [ "$RESPONSE" = "true" ]; then
    exit 0;
else
    exit 1;
fi