
start-db:
	@docker-compose up -d --no-deps db

fresh-db: start-db
	@docker-compose exec db psql -U postgres postgres -c 'drop database song;'
	@docker-compose exec db psql -U postgres postgres -c 'create database song;'

run-flyway-migration: 
	@mvn package -DskipTests
	@cd song-server && mvn flyway:migrate  -Dflyway.url=jdbc:postgresql://localhost:8082/song?stringtype=unspecified -Dflyway.user=postgres -Dflyway.password=password -Dflyway.locations=classpath:db/migration

login-psql:
	@PGPASSWORD=password psql -h localhost -p 8082  -U postgres song

format:
	@mvn fmt:format

build-server:
	@mvn package -DskipTests -pl song-server -am 

build-core:
	@mvn package -DskipTests -pl song-core -am 
	
build-client:
	@mvn package -DskipTests -pl song-client -am 

analyze:
	@mvn dependency:analyze-report

package-client:
	@mvn package -pl song-client -am 

package-server:
	@mvn package -pl song-server -am 


build-sdk:
	@mvn package -DskipTests -pl song-java-sdk -am 

clean:
	@mvn clean

##########################################################################################
#   Rosi Config - START
##########################################################################################
# Score config
SCORE_URL := https://score.org

#Note: must have score.WRITE scope
SCORE_ACCESS_TOKEN := 1234234-234234-234234-234234

# ego config
EGO_SERVER_URL := https://ego.org

# song config
#Note: must have song.WRITE scope
SONG_ACCESS_TOKEN := 1234234-234234-234234-234234
SONG_AUTH_CLIENTID:= song-app-client-id
SONG_AUTH_CLIENTSECRET := song-app-client-secret
##########################################################################################
#   Rosi Config - END
##########################################################################################


# Dont touch!
DOCKER_COMPOSE_EXE := SCORE_URL=$(SCORE_URL) \
			SCORE_ACCESS_TOKEN=$(SCORE_ACCESS_TOKEN) \
			EGO_SERVER_URL=$(EGO_SERVER_URL) \
			SONG_ACCESS_TOKEN=$(SONG_ACCESS_TOKEN) \
			SONG_AUTH_CLIENTID=$(SONG_AUTH_CLIENTID) \
			SONG_AUTH_CLIENTSECRET=$(SONG_AUTH_CLIENTSECRET) \
			docker-compose -f docker-compose.rosi.yml

help:
	@echo
	@echo "**************************************************************"
	@echo "                  Rosi Help"
	@echo "**************************************************************"
	@echo "To dry-execute a target run: make -n <target> "
	@echo
	@echo "Available Targets: "
	@grep '^[A-Za-z][A-Za-z0-9_-]\+:.*' ./Makefile | sed 's/:.*//' | sed 's/^/\t/' | grep "rosi"
	@echo

rosi-help: help

# NOTE: just noticed this annoyance. Must be fixed in next client version
rosi-ping:
	@echo "curl --header 'Authorization: Bearer <HIDDEN_ACCESS_TOKEN>" http://localhost:8080/isAlive
	@curl --header 'Authorization: Bearer $(SONG_ACCESS_TOKEN)' http://localhost:8080/isAlive

rosi-build:
	@$(DOCKER_COMPOSE_EXE) build

rosi-run:
	@$(DOCKER_COMPOSE_EXE) up -d
	@sudo chown -R ${USER}:${USER} ./song-docker-demo/data

rosi-ps:
	@$(DOCKER_COMPOSE_EXE) ps

rosi-logs:
	@$(DOCKER_COMPOSE_EXE) logs server

rosi-annihilate:
	@$(DOCKER_COMPOSE_EXE) down -v


