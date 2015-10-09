---
layout : page
title : "Orbit : Actor Concept - Event Sourced State"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Actors](orbit-actors.html) / [Actor Concepts](orbit-actor-concepts.html) / [Actor Concept - Persistent State](orbit-actor-concept-persistent-state.html)"
next : "orbit-actor-concept-distributed-transactions.html"
previous: "orbit-actor-concept-active-record-state.html"
---
{% include JB/setup %}


-  [](#ActorConcept-EventSourcedState-)
-  [Overview](#ActorConcept-EventSourcedState-Overview)
-  [Working with Event Sourcing](#ActorConcept-EventSourcedState-WorkingwithEventSourcing)
    -  [Adding State](#ActorConcept-EventSourcedState-AddingState)
    -  [Adding Events](#ActorConcept-EventSourcedState-AddingEvents)
    -  [Accessing State](#ActorConcept-EventSourcedState-AccessingState)
    -  [Executing an Event](#ActorConcept-EventSourcedState-ExecutinganEvent)
    -  [Writing State](#ActorConcept-EventSourcedState-WritingState)
    -  [Clearing State](#ActorConcept-EventSourcedState-ClearingState)
    -  [Writing State On Deactivation](#ActorConcept-EventSourcedState-WritingStateOnDeactivation)
 {#ActorConcept-EventSourcedState-_____CDATA___div_rbtoc1444424827385_padding_0px__div_rbtoc1444424827385ul_list-style_disc_margin-left_0px__div_rbtoc1444424827385li_margin-left_0px_padding-left_0px__________ActorConcept-EventSourcedState-Overview_ActorConce}
----------


Overview {#ActorConcept-EventSourcedState-Overview}
----------


Event Sourced state stores the history of an Actor's state by recording Events which change the Actor's state.


Events can be replayed to recover the current valid state for the Actor.


 


Working with Event Sourcing {#ActorConcept-EventSourcedState-WorkingwithEventSourcing}
----------


###Adding State {#ActorConcept-EventSourcedState-AddingState}


Adding Event Souced state to an actor in Orbit is simple. Developers simply extend EventSourceActor in place of AbstractrActor, passing a generic which extends TransactionalState as the state object.


The state object must be serializeable.

**Event Sourced Actor** 
{% highlight java %}
public class StatefulActor extends EventSourcedActor<StatefulActor.State> implements Some
{
    public static class State extends TransactionalState
    {
        int balance;
    }
}
{% endhighlight %}

### Adding Events {#ActorConcept-EventSourcedState-AddingEvents}


Unlike Active Record, state changes when using Event Sourced state must be made using Events.


Events are replayable actions which modify the internal state of the Actor.


Events must be annotated with TransactionalEvent, must be not be conditional and must always be able to succeed.

**Event Sourced Event** 
{% highlight java %}
public class StatefulActor extends EventSourcedActor<StatefulActor.State> implements Some
{
    public static class State
    {
        int balance;
 
        @TransactionalEvent
        void incrementBalance(int amount)
        {
             balance += amount;
        }
    }
}
{% endhighlight %}

 


###Accessing State {#ActorConcept-EventSourcedState-AccessingState}


Accessing Event Sourced state in a stateful actor is simple and works exactly the same as Active Record. The state method provides access to the current state.

**Accessing State** 
{% highlight java %}
public Task doSomeState()
{
    System.out.println(state().lastMessage);
    state().lastMessage = "Meep";
    return Task.done();
}
{% endhighlight %}

### Executing an Event {#ActorConcept-EventSourcedState-ExecutinganEvent}


In order to make changes to and Event Sourced Actor's state, you must execute an event.

**Executing An Event** 
{% highlight java %}
public Task incrementUserBalance(int amount)
{
    state().incrementBalance(amount);
    return Task.done();
}
{% endhighlight %}

### Writing State {#ActorConcept-EventSourcedState-WritingState}


The writing of Event Sourced state is identical to Active Record and is determined only by developers, Orbit will not automatically write state.

**Writing State** 
{% highlight java %}
public Task doWriteState()
{
    return writeState();
}
{% endhighlight %}

 


###Clearing State {#ActorConcept-EventSourcedState-ClearingState}


While actors are never created or destroyed in Orbit, developers can choose to clear an actors state if they wish.

**Clearing State** 
{% highlight java %}
public Task doClearState()
{
    return clearState();
}
{% endhighlight %}

 


###Writing State On Deactivation {#ActorConcept-EventSourcedState-WritingStateOnDeactivation}


Sometimes it is desirable to write state on actor deactivation, this ensures that the latest state is persisted once the actor has been deactivated.

**Writing State on Deactivation** 
{% highlight java %}
@Override
public Task deactivateAsync()
{
    await(writeState());
    return super.deactivateAsync();
}
{% endhighlight %}
