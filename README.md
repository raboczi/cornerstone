# Cornerstone

A sample application demonstrating how to integrate the following technologies:

- Apache Karaf as the application runtime
- ZK as a web user interface toolkit


## Building

This software is known to work on MacOS 10.15 "Catalina", but theoretically should run anywhere JDK 8 can.

1. Ensure that the following software is present:
  - Java Development Kit 8
  - Apache Maven 3

2. Obtain the source code, e.g. via `git clone https://github.com/raboczi/cornerstone.git`

3. Build the project by executing `mvn` from inside the `cornerstone` directory.
   Parallel builds using e.g. `mvn -T4` are also supported.


## Running

There are several different styles of deployment.

### Manual

This style of deployment gives the finest control to the developer.
A command line shell allows components to be interactively modified at runtime.

1. Start an Apache Karaf server, version 4.2.7 or later.

2. From the command line of the server, execute the following commands:
  - `feature:repo-add mvn:au.id.raboczi.cornerstone/test-war/0.1-SNAPSHOT/xml/features`
  - `feature:install cornerstone-test-war`

3. Browse [http://localhost:8181/cornerstone-test-war](http://localhost:8181/cornerstone-test-war).

### Dynamic

This style of deployment is easy and still allows components to be modified at runtime.

1. Start the server by executing `dist/dist-dynamic/target/assembly/bin/start`

2. Browse [http://localhost:8181/cornerstone-test-war](http://localhost:8181/cornerstone-test-war).

3. Shut down the server by executing `dist/dist-dynamic/target/assembly/bin/stop`

### Static

This style of deployment is the quickest to start and the most secure.
Components cannot be dynamically added or updated.
It is suited for production.

1. Start the server by executing `dist/dist-static/target/assembly/bin/start`

2. Browse [http://localhost:8181/cornerstone](http://localhost:8181/cornerstone-test-war).

3. Shut down the server by executing `dist/dist-static/target/assembly/bin/stop`
