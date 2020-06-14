# Cornerstone distributions

Karaf bills itself as a "polymorphic runtime", to indicate that a particular application can be assembled for deployment in various ways.
The following assemblies are included in Cornerstone.


## Manual assembly

Deploy the Cornerstone application components into a pre-existing Karaf application container.

This style of deployment gives the finest control to the developer, but with great power comes great complexity.

1. Build Cornerstone on the local machine to install its components into the local Maven repository.

2. Start an Apache Karaf server, version 4.3 or later.
   The Karaf runtime can be downloaded from [the Apache web site](http://karaf.apache.org/download.html).
   Unpack the archive, enter the top-level directory, and execute `bin/karaf` (or `bin\karaf.bat` on Windows).

3. From the command line of the server, execute the following commands:
  - `feature:repo-add mvn:au.id.raboczi.cornerstone/dist-features/0.1-SNAPSHOT/xml`
  - `feature:install cornerstone`


## Dynamic assembly

A pre-built executable including the Karaf application container and all the Cornerstone application components.
The components are linked when the server starts (late binding).

This style of deployment is easy and still allows components to be modified at runtime.
It includes OSGi conditional permissions-based security to make sure nobody modifies components they shouldn't.

1. Start the server by executing `dist/dynamic/target/assembly/bin/karaf`


## Static assembly

A pre-built executable including the Karaf application container and all the Cornerstone application components.
The components are pre-linked at compile time (early binding).

This style of deployment is the quickest to start and leanest, since it leaves out the dynamic linker.
Although components cannot be dynamically added or updated, this does eliminate such updates as a security issue.
It particularly suits containerized deployment, since the container system can assume the dynamic deployment duties.

1. Start the server by executing `dist/static/target/assembly/bin/karaf`
