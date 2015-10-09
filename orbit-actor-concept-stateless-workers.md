---
layout : page
title : "Orbit : Actor Concept - Stateless Workers"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Actors](orbit-actors.html) / [Actor Concepts](orbit-actor-concepts.html)"
next : "orbit-actor-concept-timers.html"
previous: "orbit-actor-concept-distributed-transactions.html"
---
{% include JB/setup %}



-  [Overview](#ActorConcept-StatelessWorkers-Overview)
    -  [Standard Behavior](#ActorConcept-StatelessWorkers-StandardBehavior)
    -  [Stateless Workers](#ActorConcept-StatelessWorkers-StatelessWorkers)
-  [Creating A Stateless Worker](#ActorConcept-StatelessWorkers-CreatingAStatelessWorker)



Overview {#ActorConcept-StatelessWorkers-Overview}
----------


###Standard Behavior {#ActorConcept-StatelessWorkers-StandardBehavior}


Orbit's standard [runtime guarantees](orbit-actor-concept-actors.html) ensure that more than one activation of an actor with a given identity can not exist in an Orbit cluster at any given time.  This is typically desired behavior. It ensures that an actors internal state is always consistent, this is desirable even for actors that do not have persistent state as the internal transient state is usually important (non-persisted chat room for example).


###Stateless Workers {#ActorConcept-StatelessWorkers-StatelessWorkers}


In some specific scenarios this strict runtime behavior is not desired. As a consequence, Orbit’s standard behavior adds unnecessary latency to Actors that do not maintain an internal state.


For these scenarios, Orbit offers the concept of Stateless Workers.


-  More than one stateless worker actor with a given identity can exist at once
-  Orbit will create stateless workers based on demand
-  Stateless workers must not rely on any state existing between calls

Creating A Stateless Worker {#ActorConcept-StatelessWorkers-CreatingAStatelessWorker}
----------


Creating a stateless worker simply requires adding an annotation to your Actor Interface.

**Stateless Worker** 
{% highlight java %}
import com.ea.orbit.annotation.StatelessWorker;
import com.ea.orbit.actors.Actor;
import com.ea.orbit.concurrent.Task;

@StatelessWorker
public interface SomeStatelessWorker extends Actor
{
    Task<String> someMethod(String message);
}
{% endhighlight %}

-  The @StatelessWorker annotation on the actor interface makes this actor a Stateless Worker.

 

