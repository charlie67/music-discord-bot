./gradlew jar
echo $2 | docker login -u $1 --password-stdin
docker buildx build --platform linux/amd64 --tag $1/discord-music-bot:${GITHUB_REF##*/} --push .
docker buildx build --platform linux/arm/v7 --tag $1/discord-music-bot:${GITHUB_REF##*/}-arm --push -f Dockerfile-arm .
