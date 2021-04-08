./gradlew jar
echo $2 | docker login -u $1 --password-stdin
docker buildx build --platform linux/amd64 --tag $DOCKER_USERNAME/discord-music-bot:$TRAVIS_BRANCH --push .
docker buildx build --platform linux/arm/v7 --tag $DOCKER_USERNAME/discord-music-bot:${TRAVIS_BRANCH}-arm --push -f Dockerfile-arm .
