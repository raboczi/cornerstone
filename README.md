# Cornerstone

A sample application demonstrating how to integrate the following technologies:

- Apache Karaf as the application runtime
- ZK as a web user interface toolkit


## Building

This software is known to work on MacOS 10.15 "Catalina" and Raspbian 4.19.
Theoretically it should run anywhere JDK 8+ can.

1. Ensure that the following software is present:
  - Java Development Kit 8 or later
  - Apache Maven 3
  - [zkless-engine](https://github.com/zkoss/zkless-engine) ("use locally" installation)
  - <del>Mozilla Firefox and [Geckodriver](https://github.com/mozilla/geckodriver)</del>

2. Obtain the source code, e.g. via `git clone https://github.com/raboczi/cornerstone.git`

3. Initialize the cryptographic keystore by executing `mvn -f crypto/pom.xml` from inside the `cornerstone` directory.
   You should only do this once.  See the [cryptography README](crypto/README.md) for details.

4. Build the project by executing `mvn` from inside the `cornerstone` directory.
   - The majority of build time is taken by integration testing; this can be skipped by executing `mvn -DskipITs` instead.
   - Parallel builds using e.g. `mvn -T4` are not currently supported.
   - Release builds using `mvn -Prelease` will perform additional validation and include javadoc and source attachments, but require that `mvn versions:set` has been used to set a non-snapshot version.

5. <del>Optionally, generate [Javadoc](target/site/apidocs/index.html) by executing `mvn javadoc:aggregate`</del>


## Running

Start the server by executing `dist/dynamic/target/assembly/bin/karaf`.
This will start the Karaf shell momentarily.
The server can be stopped by typing `shutdown` or Ctrl-D.

For alternative ways to start the server, see the [distributions README](dist/README.md).


## Using

Cornerstone defaults to sharing Karaf's default login credentials, which are username "karaf", password "karaf".

A web interface is available at [http://localhost:8181/zk](http://localhost:8181/zk).

The Karaf console is available at [http://localhost:8181/system/console](http://localhost:8181/system/console).

From the Karaf command line, the test service can be accessed using `test:get` and `test:set`.

The REST endpoint can be accessed using (for example) the following:

`curl --user karaf http://localhost:8181/test/value`
`curl --user karaf --header "Content-Type: application/json" --data "Lorem ipsum dolor" http://localhost:8181/test/value`

The websocket can be accessed using:

``

The server log is `dist/dynamic/target/assembly/data/log/karaf.log`.
