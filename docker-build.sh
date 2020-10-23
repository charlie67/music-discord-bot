./gradlew jar
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker buildx build --platform linux/amd64,linux/arm64 --tag $DOCKER_USERNAME/discord-music-bot:test --push .
