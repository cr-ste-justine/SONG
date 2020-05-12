export VERSION=0.4

docker build --no-cache -t chusj/overture-song:$VERSION --target server .;
docker push chusj/overture-song:$VERSION;