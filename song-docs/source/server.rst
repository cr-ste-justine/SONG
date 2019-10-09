.. _server_ref:

=======================================
Deploying a SONG Server in Production
=======================================

The following section describes how to install, configure and run the SONG server in production.


.. _server_prereq:

Prerequisites
==================

The following software dependencies are required in order to run the server

- Docker
- Postgres database

.. note::
    Only a postgres database can be used, since postgres-specific features are used in the SONG server

.. _server_official_releases:

Official Releases
==================

Official SONG release notes can be found `here <https://github.com/overture-stack/SONG/releases>`_.
The release note labels follow the `symantic versioning specification <https://semver.org/>`_ and contain notes with a description of the bug fixes, new features or enhancements, breaking changes, change logs and Docker image tag names. 
All official SONG release notes are tagged in the format ``$COMPONENT-$VERSION``, where the ``$COMPONENT`` portion follows the regex ``^[a-z-]+$`` and the ``$VERSION`` component follows ``^\d+\.\d+\.\d+$`` . 
Sing the SONG server is packaged into a docker image, the officially released docker images can be found at https://cloud.docker.com/u/overture/repository/docker/overture/song-server/tags and are tagged in the format ``$VERSION`` where ``$VERSION`` follows ``^\d+\.\d+\.\d+$``.


Installation
===============================

Once the desired release tag and therefore ``$VERSION`` are known, the corresponding docker image can be pulled from dockerhub:

.. code-block:: bash

   docker pull overture/song-server:$VERSION


Configuration
===============================

Server
---------------
The SONG server can be configured by defining specific environment variables which must be passed at container creation. The following environment variables are available:

.. code-block:: bash

   ################################
   #     SONG Server Config       #
   ################################

   # Ensure a secure production server
   SPRING_PROFILES_ACTIVE: prod,secure,default
   SERVER_PORT: 8080
   MANAGEMENT_SERVER_PORT: 8081

   ################################
   #     OAuth2 Server Config     #
   ################################

   # Endpoint to validate OAuth2 tokens
   AUTH_SERVER_URL:  <auth-server-url>/api/o/check_token/
   AUTH_SERVER_CLIENTID: <clientId>
   AUTH_SERVER_CLIENTSECRET: <clientSecret>

   # System-level scope prefix and suffix
   # For example, using the configuration below, the User-Agent's
   # access token would need to have song.WRITE scope in order to
   # complete an authorized request
   AUTH_SERVER_SCOPE_SYSTEM: song.WRITE

   # Study-level scope prefix and suffix
   # For example, using the configurations below, the User-Agent's
   # access token would need to have song.<studyId>.WRITE scope in order to
   # complete an authorized request. In general the format of the scope is:
   # <prefix>STUDY_ID<suffix>
   AUTH_SERVER_SCOPE_STUDY_PREFIX: song-
   AUTH_SERVER_SCOPE_STUDY_SUFFIX: .WRITE

   ################################
   #       ID Server Config       #
   ################################

   # URL of the ID server
   ID_IDURL: <id-server-url>

   # Application level access token used to interact with the ID server. 
   # The access token must have id.create scope
   ID_AUTHTOKEN: <id-server-access-token>

   # Enabled to use an ID server. If false, will use
   # and in-memory id server (use only for testing)
   ID_REALIDS: true

   ################################
   #   Postgres Database Config   #
   ################################
   SPRING_DATASOURCE_URL: jdbc:postgresql://<db-url>/song?stringtype=unspecified
   SPRING_DATASOURCE_USERNAME: postgres
   SPRING_DATASOURCE_PASSWORD: password

   # Enable flyway to manage database migrations automatically
   SPRING_FLYWAY_ENABLED: true
   SPRING_FLYWAY_LOCATIONS: classpath:db/migration

   ################################
   # SCORE Server Config          #
   ################################

   # URL used to ensure files exist in the score server
   SCORE_URL: <score-server-url>

   # Application level access token used internally by the SONG server to download
   # additional file metadata from the SCORE server. This access token must have the 
   # correct download scope inorder to download from SCORE,
   SCORE_ACCESSTOKEN: <score-access-token-with-download-scope>


The example file above configures the server to use a read id service, an OAuth2 authorization service, a SCORE service and a Postgres database.

Scope Security Configuration
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
SONG has 2 types of security: **system-level** and **study-level**. **System-level** security is required for any non-study related request, and can be defined via the ``auth.server.scope.system`` property as any string. **Study-level** security is required for any request on a particular study resource and can be defined via the ``auth.server.scope.study.prefix`` and ``auth.server.scope.study.suffix`` properties. For example, by setting the study prefix to ``PROGRAMDATA-`` and the suffix to ``.WRITE``, the required scope for a request associated with the studyId ``ABC123-CA`` would be ``PROGRAMDATA-ABC123-CA.WRITE``.

Database Migration
----------------
If the user chooses to host their own song server database, it can easily be initialized with a few commands. As of ``song-1.5.0``, SONG server database migrations are managed by `flyway <https://flywaydb.org/getstarted>`_. 
When upgrading the SONG server version, a flyway migration must be run. 

The following steps show how to create an empty database, and migrate a new or exising database using flyway.

Creating an empty database
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

If the database doesnt exist yet, a flyway migration can easily be run on a newly created postgres database 
by simply running the SONG server with ``spring.flyway.enabled`` property or ``SPRING_FLYWAY_ENABLED`` environment variable set to ``true``. Upon boot, the server will initialize the empty database.

For example, a database can be created with the user ``postgres``, password ``password``, database name ``song`` and database url ``http://localhost:8082``:

.. code-block:: bash

   # Create an empty database called "song" with user "postgres"
   sudo -u postgres psql -c "createdb song"

   # Create the password "myNewPassword" for the user "postgres"
   sudo -u postgres psql postgres -c ‘ALTER USER postgres WITH PASSWORD ‘myNewPassword’;

If the database already exists, but the SONG server was started with ``spring.flyway.enabled`` set to ``false``, refer to the following step for manually running a flyway migration

Manually running a flyway migration 
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This step should be run on an unmigrated or empty database. When upgrading the SONG server version, this step can also be used to manually migrate the database, however it is suggested to just boot the server with the ``spring.flyway.enabled`` property set to ``true`` and let the server automatically run the migration. In either case, the following should be executed:

.. code-block:: bash

   # Clone the SONG repository for version "song-X.X.X"
   git clone --branch song-X.X.X https://github.com/overture-stack/song

   # Run the migration on the database "song" for version "song-X.X.X"
   cd song
   ./mvnw -pl song-server flyway:migrate \
      -Dflyway.url=jdbc:postgresql://localhost:8082/song?stringtype=unspecified \
      -Dflyway.user=postgres \
      -Dflyway.password=password \
      -Dflyway.locations=db/migration

Running the server
===============================

Using docker
-------------------
A SONG server can be run manually using the ``docker run`` command with an ``always`` restart policy. Below is an example:

.. code-block:: bash

   $ docker run -d --rm  \
      -p "8080:8080" \
      --restart always \
      --name song-server-X.X.X \
      -e "SPRING_PROFILES_ACTIVE=prod,secure,default" \
      -e "SERVER_PORT=8080" \
      -e "MANAGEMENT_SERVER_PORT=8081" \
      -e "AUTH_SERVER_URL=<auth-server-url>/api/o/check_token/" \
      -e "AUTH_SERVER_CLIENTID=<clientId>" \
      -e "AUTH_SERVER_CLIENTSECRET=<clientSecret>" \
      -e "AUTH_SERVER_SCOPE_SYSTEM=song.WRITE" \
      -e "AUTH_SERVER_SCOPE_STUDY_PREFIX=song-" \
      -e "AUTH_SERVER_SCOPE_STUDY_SUFFIX=.WRITE" \
      -e "ID_IDURL=<id-server-url>" \
      -e "ID_AUTHTOKEN=<id-server-access-token>" \
      -e "ID_REALIDS=true" \
      -e "SPRING_DATASOURCE_URL=jdbc:postgresql://<db-url>/song?stringtype=unspecified" \
      -e "SPRING_DATASOURCE_USERNAME=postgres" \
      -e "SPRING_DATASOURCE_PASSWORD=password" \
      -e "SPRING_FLYWAY_LOCATIONS=classpath:db/migration" \
      -e "SPRING_FLYWAY_ENABLED=true" \
      -e "SCORE_URL=<score-server-url>" \
      -e "SCORE_ACCESSTOKEN=<score-access-token-with-download-scope>" \
      overture/song-server:X.X.X

Alternatively, the above environment variables can be places in a file (below) and then that filepath can be used with the docker run command instead:

.. code-block:: bash

   $ cat env.list

   SPRING_PROFILES_ACTIVE=prod,secure,default
   SERVER_PORT=8080
   MANAGEMENT_SERVER_PORT=8081
   AUTH_SERVER_URL=<auth-server-url>/api/o/check_token/
   AUTH_SERVER_CLIENTID=<clientId>
   AUTH_SERVER_CLIENTSECRET=<clientSecret>
   AUTH_SERVER_SCOPE_SYSTEM=song.WRITE
   AUTH_SERVER_SCOPE_STUDY_PREFIX=song-
   AUTH_SERVER_SCOPE_STUDY_SUFFIX=.WRITE
   ID_IDURL=<id-server-url>
   ID_AUTHTOKEN=<id-server-access-token>
   ID_REALIDS=true
   SPRING_DATASOURCE_URL=jdbc:postgresql://<db-url>/song?stringtype=unspecified
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=password
   SPRING_FLYWAY_LOCATIONS=classpath:db/migration
   SPRING_FLYWAY_ENABLED=true
   SCORE_URL=<score-server-url>
   SCORE_ACCESSTOKEN=<score-access-token-with-download-scope>

   $ docker run -d --rm  \
      -p "8080:8080" \
      --restart always \
      --name song-server-X.X.X \
      --env-file env.list \
      overture/song-server:X.X.X

Using docker-compose
----------------------

The server can also be run using docker-compose. Below is an example ``docker-compose.yml`` with only the SONG service definition visible:

.. code-block:: yml

   version: '3.4'
   services:
      song-server:
         image: "overture/song-server:X.X.X"
         environment:
            SPRING_PROFILES_ACTIVE: prod,secure,default
            SERVER_PORT: 8080
            MANAGEMENT_SERVER_PORT: 8081
            AUTH_SERVER_URL: <auth-server-url>/api/o/check_token/
            AUTH_SERVER_CLIENTID: <clientId>
            AUTH_SERVER_CLIENTSECRET: <clientSecret>
            AUTH_SERVER_SCOPE_SYSTEM: song.WRITE
            AUTH_SERVER_SCOPE_STUDY_PREFIX: song-
            AUTH_SERVER_SCOPE_STUDY_SUFFIX: .WRITE
            ID_IDURL: <id-server-url>
            ID_AUTHTOKEN: <id-server-access-token>
            ID_REALIDS: true
            SPRING_DATASOURCE_URL: jdbc:postgresql://<db-url>/song?stringtype=unspecified
            SPRING_DATASOURCE_USERNAME: postgres
            SPRING_DATASOURCE_PASSWORD: password
            SPRING_FLYWAY_LOCATIONS: classpath:db/migration
            SPRING_FLYWAY_ENABLED: true
            SCORE_URL: <score-server-url>
            SCORE_ACCESSTOKEN: <score-access-token-with-download-scope>
         restart: always
         
The configured ``docker-compose.yml`` file can then be run with

.. code-block:: bash

   docker-compose -f ./docker-compose.yml up -d song-server

Using Kubernetes and helm charts
----------------------------------

For deployment onto a Kubernetes cluster, a `song helm chart <https://github.com/overture-stack/helm-charts/tree/master/song>`_ is available.
The `values.yml <https://github.com/overture-stack/helm-charts/blob/master/song/values.yaml>`_ file must be modified with the correct configurations before deploying.


