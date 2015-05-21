---
layout : page
title : "Orbit : Advanced Topic - Lifetime Extensions"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Actors](orbit-actors.html) / [Actor Concepts](orbit-actor-concepts.html) / [Actor Concept - Advanced Topics](orbit-actor-concept-advanced-topics.html)"
next : "orbit-actor-tutorials.html"
previous: "orbit-advanced-topic-cluster-configuration.html"
---
{% include JB/setup %}



-  [Overview](#AdvancedTopic-LifetimeExtensions-Overview)
-  [Working With Lifetime Extensions](#AdvancedTopic-LifetimeExtensions-WorkingWithLifetimeExtensions)
    -  [Lifetime Extensions Interface](#AdvancedTopic-LifetimeExtensions-LifetimeExtensionsInterface)
    -  [Registering The Extension](#AdvancedTopic-LifetimeExtensions-RegisteringTheExtension)



Overview {#AdvancedTopic-LifetimeExtensions-Overview}
----------


When integrating Orbit with other frameworks it is often useful to get notifications about lifetime events happening within the framework.


To meet this need, Orbit allows developers to implement a Lifetime Extension. Lifetime extensions allow a developer to implement an interface and get notified about framework events.


This is particularly useful for Dependency Injection frameworks as developers get an opportunity to wire an actor into the framework before it is used by Orbit.


 


Working With Lifetime Extensions {#AdvancedTopic-LifetimeExtensions-WorkingWithLifetimeExtensions}
----------


###Lifetime Extensions Interface {#AdvancedTopic-LifetimeExtensions-LifetimeExtensionsInterface}


The lifetime extension interface is very simple.

**Lifetime Extension** 
{% highlight java %}
Task preActivation(AbstractActor actor);
Task postActivation(AbstractActor actor);
Task preDeactivation(AbstractActor actor);
Task postDeactivation(AbstractActor actor);
{% endhighlight %}

 


###Registering The Extension {#AdvancedTopic-LifetimeExtensions-RegisteringTheExtension}


The quickest and easiest way to register a extension is to add it to the Orbit Stage before startup.

**Register Extension** 
{% highlight java %}
Stage stage = new Stage();
stage.setClusterName(clusterId);
 
stage.addExtension(new LifetimeExtension() {
    @Override
    public Task preActivation(AbstractActor orbitActor) {
        return Task.done();
    }
    @Override
    public Task postActivation(AbstractActor orbitActor) {
        return Task.done();
    }
    @Override
    public Task preDeactivation(AbstractActor orbitActor) {
        return Task.done();
    }
    @Override
    public Task postDeactivation(AbstractActor orbitActor) {
        return Task.done();
    }
});
 
stage.start().join();
{% endhighlight %}
