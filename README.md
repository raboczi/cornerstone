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
   - This will create the subdirectory `.au.id.raboczi.cornerstone` in your home directory, containing the private key used to sign
     bundles and the public certificate that can be used to trust the signed bundles.
   - You only need to do this once, unless you delete the `.au.id.raboczi.cornerstone` directory.

4. Build the project by executing `mvn` from inside the `cornerstone` directory.
   - The majority of build time is taken by integration testing; this can be skipped by executing `mvn -DskipITs` instead.
   - Parallel builds using e.g. `mvn -T4` are not currently supported.
   - Release builds using `mvn -Prelease` will perform additional validation and include javadoc and source attachments, but require that `mvn versions:set` has been used to set a non-snapshot version.

5. <del>Optionally, generate [Javadoc](target/site/apidocs/index.html) by executing `mvn javadoc:aggregate`</del>


## Running

There are several different styles of deployment.
Karaf's default login credentials are username "karaf", password "karaf".

### Manual

This style of deployment gives the finest control to the developer.
A command line shell allows components to be interactively modified at runtime.

1. Start an Apache Karaf server, version 4.3 or later.

2. From the command line of the server, execute the following commands:
  - `feature:repo-add mvn:au.id.raboczi.cornerstone/dist-features/0.1-SNAPSHOT/xml`
  - `feature:install cornerstone`

3. Browse [http://localhost:8181/zk](http://localhost:8181/zk).

4. Shut down the server from the command line by pressing Ctrl-D.

### Dynamic

This style of deployment is easy and still allows components to be modified at runtime.

1. Start the server by executing `dist/dynamic/target/assembly/bin/start`

2. Browse [http://localhost:8181/zk](http://localhost:8181/zk).

3. Shut down the server by executing `dist/dynamic/target/assembly/bin/stop`

### Static

This style of deployment is the quickest to start, leanest, and the most secure.
Components cannot be dynamically added or updated.
It is suited for production.

1. Start the server by executing `dist/static/target/assembly/bin/start`

2. Browse [http://localhost:8181/zk](http://localhost:8181/zk).

3. Shut down the server by executing `dist/static/target/assembly/bin/stop`


## Notes

The Karaf console is available at [http://localhost:8181/system/console](http://localhost:8181/system/console).

From the Karaf command line, the test service can be accessed using `test:get` and `test:set`.

The REST endpoint can be accessed using (for example) the following:

`curl --user karaf http://localhost:8181/test/value`
`curl --user karaf --header "Content-Type: application/json" --data "Lorem ipsum dolor" http://localhost:8181/test/value`

