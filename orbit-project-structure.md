---
layout : page
title : "Orbit : Project Structure"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Getting Started](orbit-getting-started.html)"
next : "orbit-building-orbit.html"
previous: "orbit-prerequisites.html"
---
{% include JB/setup %}



-  [Source Structure](#ProjectStructure-SourceStructure)
-  [Maven Structure](#ProjectStructure-MavenStructure)
    -  [Core](#ProjectStructure-Core)
    -  [Providers](#ProjectStructure-Providers)



Source Structure {#ProjectStructure-SourceStructure}
----------


Orbit is organized into a set of folders which contain the high level systems.


<table>
<tr><th> Component </th><th> Path </th><th> Purpose </th></tr>
<tr><td> Actors </td><td> /actors </td><td> Orbit Actors is a framework to write distributed systems using virtual actors. It abstracts much of the work that programmers are usually required to perform in order to work with distributed actors such state management, actor addressability and actor lifetime.  </td></tr>
<tr><td> Container </td><td> /container </td><td>

Orbit Container is a minimal inversion of control container designed to make writing and managing applications easier by simplifying object injection, service location, application configuration and dependency management. It abstracts the implementation details of the underlying technology away from programmers and operations engineers who are able to develop and maintain different technologies with a unified interface. 

 </td></tr>
<tr><td> Web </td><td> /web </td><td> Orbit Web is a basic implementation of a web service container for Orbit Applications, it uses Jetty and Jersey and offers HTTP and WebSocket endpoints. </td></tr>
<tr><td> Commons </td><td> /commons </td><td> Orbit Commons contains common helper and utility classes which are used across multiple Orbit modules. </td></tr>
<tr><td> Samples </td><td> /samples </td><td> Samples contains the samples which demonstrate the use of the Orbit stack. </td></tr>
</table>


 


Maven Structure {#ProjectStructure-MavenStructure}
----------


Orbit is also hosted on the maven central repository.


###Core {#ProjectStructure-Core}


| Group | Artifact | Purpose |
|-------|----------|---------|
| com.ea.orbit | orbit-actors-all | Contains the full actor framework |
| com.ea.orbit | orbit-container | Contains the container system |
| com.ea.orbit | orbit-web | Contains the web system |
| com.ea.orbit | orbit-commons | Contains the common utils |
| com.ea.orbit | orbit-actors-* | Contains the various individual actor systems |
| com.ea.orbit.samples | orbit-* | Contains the samples |


 


###Providers {#ProjectStructure-Providers}


| Group | Artifact | Purpose |
|-------|----------|---------|
| com.ea.orbit | orbit-actors-mongodb | Contains the actors MongoDB storage system |
| com.ea.orbit | orbit-actors-postgresql | Contains the actors PostgreSQL storage system |
| com.ea.orbit | orbit-actors-redis | Contains the actors Redis storage system |
| com.ea.orbit | orbit-actors-spring | Contains the actors Spring project integration |

