---
layout : page
title : "Orbit : Actor Concept - Useful Annotations"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Actors](orbit-actors.html) / [Actor Concepts](orbit-actor-concepts.html)"
next : "orbit-actor-concept-actors-and-container.html"
previous: "orbit-actor-concept-observers.html"
---
{% include JB/setup %}



-  [Brief](#ActorConcept-UsefulAnnotations-Brief)
-  [Actor Interface Annotations](#ActorConcept-UsefulAnnotations-ActorInterfaceAnnotations)
    -  [StatelessWorker](#ActorConcept-UsefulAnnotations-StatelessWorker)
    -  [NoIdentity](#ActorConcept-UsefulAnnotations-NoIdentity)
    -  [PreferLocalPlacement](#ActorConcept-UsefulAnnotations-PreferLocalPlacement)
-  [Message Annotations](#ActorConcept-UsefulAnnotations-MessageAnnotations)
    -  [OneWay](#ActorConcept-UsefulAnnotations-OneWay)
    -  [OnlyIfActivated](#ActorConcept-UsefulAnnotations-OnlyIfActivated)
    -  [CacheResponse](#ActorConcept-UsefulAnnotations-CacheResponse)



Brief {#ActorConcept-UsefulAnnotations-Brief}
----------


Orbit offers several useful annotations for customizing the behavior of actors or messages.


Useful annotations are listed below.


 


Actor Interface Annotations {#ActorConcept-UsefulAnnotations-ActorInterfaceAnnotations}
----------


###StatelessWorker {#ActorConcept-UsefulAnnotations-StatelessWorker}


{% highlight java %}
@StatelessWorker
public interface MyActor extends Actor {}
{% endhighlight %}

Causes your actor to be a stateless worker. See [stateless workers](orbit-actor-concept-stateless-workers.html).


###NoIdentity {#ActorConcept-UsefulAnnotations-NoIdentity}


{% highlight java %}
@NoIdentity
public interface MyActor extends Actor {}
{% endhighlight %}

Denotes that this actor does not have an identity and acts as a singleton. Actor is accessed using getReference() instead of getReference(id).


###PreferLocalPlacement {#ActorConcept-UsefulAnnotations-PreferLocalPlacement}


{% highlight java %}
@PreferLocalPlacement(percentile=100)
public interface MyActor extends Actor {}
{% endhighlight %}

Denotes that this actor should prefer to be placed locally if not already activated and the local node is capable of hosting it. Optional percentile value allows developers to define the likelihood of preferring local placement (default 100). 


Message Annotations {#ActorConcept-UsefulAnnotations-MessageAnnotations}
----------


###OneWay {#ActorConcept-UsefulAnnotations-OneWay}


{% highlight java %}
@OneWay
public Task someMessage() { return Task.done(); }
{% endhighlight %}

This message is OneWay.  No result (value or status) will be returned, no guarantee that message will be processed.


The Task might contain an exception if there was a problem locating the target object or serializing the message.


###OnlyIfActivated {#ActorConcept-UsefulAnnotations-OnlyIfActivated}


{% highlight java %}
@OnlyIfActivated
public Task someMessage() { return Task.done(); }
{% endhighlight %}

This message is only executed if the actor has already been activated.  Unlike normal messages, this will not cause an actor to activate.


###CacheResponse {#ActorConcept-UsefulAnnotations-CacheResponse}


{% highlight java %}
@CacheResponse(maxEntries = 1000, ttlDuration = 5, ttlUnit = TimeUnit.SECONDS)
Task<String> getAccountName(int id);
{% endhighlight %}

This message caches its result, on a per actor, per parameter-set basis.  The data persists for the given duration, or until at least the specified number of entries has been reached (whichever constraint is encountered first).  Cached values are not guaranteed to immediately evict once maxEntries has been reached.


 Caches can be force-flushed using ExecutionCacheFlushController.

