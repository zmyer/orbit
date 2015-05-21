---
layout : page
title : "Orbit : Actors"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html)"
next : "orbit-actor-overview.html"
previous: "orbit-building-orbit.html"
---
{% include JB/setup %}

[Actor Overview](orbit-actor-overview.html) {#Actors-ActorOverviewActor-Overview_97848934_html}
----------


[Actor Concepts](orbit-actor-concepts.html) {#Actors-ActorConceptsActor-Concepts_96535010_html}
----------


[Actor Tutorials](orbit-actor-tutorials.html) {#Actors-ActorTutorialsActor-Tutorials_95848531_html}
----------


 

**Java Example** 
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
**Scala Example** 
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
