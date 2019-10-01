
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
PREFIX := SCORE_URL=$(SCORE_URL) \
			SCORE_ACCESS_TOKEN=$(SCORE_ACCESS_TOKEN) \
			EGO_SERVER_URL=$(EGO_SERVER_URL) \
			SONG_ACCESS_TOKEN=$(SONG_ACCESS_TOKEN) \
			SONG_AUTH_CLIENTID=$(SONG_AUTH_CLIENTID) \
			SONG_AUTH_CLIENTSECRET=$(SONG_AUTH_CLIENTSECRET)

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
rosi-build:
	@$(PREFIX) docker-compose -f docker-compose.rosi.yml build

rosi-run:
	@$(PREFIX) docker-compose -f docker-compose.rosi.yml up -d

rosi-annihilate:
	@$(PREFIX) docker-compose -f docker-compose.rosi.yml down -v


