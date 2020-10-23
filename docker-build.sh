./gradlew jar
echo '{"experimental":true}' | sudo tee /etc/docker/daemon.json
docker buildx build --platform linux/amd64,linux/arm64 -t $DOCKER_USERNAME/discord-music-bot:$TRAVIS_BRANCH .
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker push $DOCKER_USERNAME/discord-music-bot:$TRAVIS_BRANCH
