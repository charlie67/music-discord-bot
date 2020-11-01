./gradlew jar
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker buildx build --platform linux/amd64 --tag $DOCKER_USERNAME/discord-music-bot:$TRAVIS_BRANCH --push .
docker buildx build --platform linux/arm32/v7 --tag $DOCKER_USERNAME/discord-music-bot-arm:$TRAVIS_BRANCH --push ./Dockerfile-arm
