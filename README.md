# Url Shortner

Tech Used: ZIO, Redis, Akka-http, Circe, Cats, Scalatest

### Pre-requisite

As the project is using Redis database. Therefore, the db needs to be installed and
run before running the project

Command: `docker run --name my-redis -p 6379:6379 -d redis`

After that go to the project path and open sbt console by hitting
### `sbt`

### Running the app

Command: `run` in the sbt console

It will start the backend server pointing to 9000 port

Alternatively if the project is opened in some IDE(like intellij) then 
`UrlShortnerApp` can be run directly

After that the postman script(inside the postman folder) can be imported
in the postman and the endpoints can be tested. That contains sample requests
for the endpoints defined in the application.

### Running the tests

Command: `test` in the sbt console

It will run the tests

Alternatively if the project is opened in some IDE(like intellij) then
the tests inside the `test` folder can be run directly