---
layout: default
title: Orbit Framework
---
{% include JB/setup %}

![Orbit Framework](tpl/orbit-logo-black.png)

[![Release](https://img.shields.io/github/tag/electronicarts/orbit.svg?label=release)](https://github.com/electronicarts/orbit/releases)
[![Maven Central](https://img.shields.io/maven-central/v/com.ea.orbit/orbit-parent.svg)](https://repo1.maven.org/maven2/com/ea/orbit/)
[![Javadocs](https://img.shields.io/maven-central/v/com.ea.orbit/orbit-parent.svg?label=Javadocs)](http://www.javadoc.io/doc/com.ea.orbit/orbit-all-docs)
[![Build Status](https://img.shields.io/travis/electronicarts/orbit.svg)](https://travis-ci.org/electronicarts/orbit)
[![Gitter](https://img.shields.io/badge/style-Join_Chat-ff69b4.svg?style=flat&label=gitter)](https://gitter.im/electronicarts/orbit?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

Orbit is a modern framework for JVM languages that makes it easier to build and maintain distributed and scalable online services.
It was developed by [BioWare](http://www.bioware.com), a division of [Electronic Arts](http://www.ea.com). For the latest news, follow us on [Twitter](https://twitter.com/OrbitFramework). 

You can grab the code on [GitHub](https://github.com/electronicarts/orbit) or [get started](http://orbit.bioware.com/orbit-getting-started.html) now. 

Orbit is primarily made up of the following components:

-  [Orbit Actors](orbit-actors.html), a framework to write distributed systems using virtual actors.
-  [Orbit Async](orbit-async.html), async-await methods for the JVM.
-  [Orbit Container](orbit-container.html), a minimal inversion of control container for building online services.
-  [Orbit Utils](orbit-utils.html), a set of utils to help simplify various tasks on the JVM.
-  Orbit Web, a lightweight HTTP and Websockets container for Orbit, powered by Jetty.
-  Orbit Commons, various common utilities used by Orbit.

<br />

**Actors - Java Example** 
{% highlight java %}
public interface Hello extends Actor
{
    Task<String> sayHello(String greeting);
}
 
public class HelloActor extends AbstractActor implements Hello
{
    public Task<String> sayHello(String greeting)
    {
        getLogger().info("Here: " + greeting);
        return Task.fromValue("Hello There");
    }
}
 
Actor.getReference(Hello.class, "0").sayHello("Meep Meep");
{% endhighlight %}

**Actors - Scala Example** 
{% highlight scala %}
trait Hello extends Actor {
  def sayHello(greeting: String): Task[String]
}

class HelloActor extends AbstractActor[AnyRef] with Hello {
  def sayHello(greeting: String): Task[String] = {
    getLogger.info("Here: " + greeting)
    Task.fromValue("Hello There")
  }
}

Actor.getReference(classOf[Hello], "0").sayHello("Meep Meep")
{% endhighlight %}
