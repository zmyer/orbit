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



Source Structure {#ProjectStructure-SourceStructure}
----------


Orbit is organized into a set of folders which contain the high level systems.


<table>
<tr><th> Component </th><th> Path </th><th> Purpose </th></tr>
<tr><td> Actors </td><td> /actors </td><td> A framework to write distributed systems using virtual actors. </td></tr>
<tr><td> Async </td><td> /async </td><td> async-await methods for the JVM. </td></tr>
<tr><td> Container </td><td> /container </td><td>

A minimal inversion of control container for building online services.

 </td></tr>
<tr><td> Utils </td><td> /utils </td><td> A set of utils to help simplify various tasks on the JVM </td></tr>
<tr><td> Web </td><td> /web </td><td> A lightweight HTTP and Websockets container for Orbit, powered by Jetty. </td></tr>
<tr><td> Commons </td><td> /commons </td><td> Various common utilities used by Orbit. </td></tr>
<tr><td> Samples </td><td> /samples </td><td> Samples which demonstrate the use of the Orbit stack. </td></tr>
</table>


 


Maven Structure {#ProjectStructure-MavenStructure}
----------


Orbit is also hosted on the maven central repository.


###Core {#ProjectStructure-Core}


<table>
<tr><th> Group </th><th> Artifact </th><th> Purpose </th></tr>
<tr><td> com.ea.orbit </td><td> orbit-actors-all </td><td> Contains the full actor framework </td></tr>
<tr><td> com.ea.orbit </td><td> orbit-actors-* </td><td> Contains the various individual actor systems </td></tr>
<tr><td> com.ea.orbit </td><td> orbit-async </td><td> Contains the async-await system </td></tr>
<tr><td> com.ea.orbit </td><td> orbit-container </td><td> Contains the container system </td></tr>
<tr><td> com.ea.orbit </td><td> orbit-web </td><td> Contains the web system </td></tr>
<tr><td> com.ea.orbit </td><td> orbit-commons </td><td> Contains the common utils </td></tr>
<tr><td> com.ea.orbit.samples </td><td> orbit-* </td><td> Contains the samples </td></tr>
</table>

