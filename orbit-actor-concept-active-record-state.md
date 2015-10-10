---
layout : page
title : "Orbit : Actor Concept - Active Record State"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Actors](orbit-actors.html) / [Actor Concepts](orbit-actor-concepts.html) / [Actor Concept - Persistent State](orbit-actor-concept-persistent-state.html)"
next : "orbit-actor-concept-event-sourced-state.html"
previous: "orbit-actor-concept-persistent-state.html"
---
{% include JB/setup %}



-  [Overview](#ActorConcept-ActiveRecordState-Overview)
-  [Working with Active Record](#ActorConcept-ActiveRecordState-WorkingwithActiveRecord)
    -  [Adding State](#ActorConcept-ActiveRecordState-AddingState)
    -  [Accessing State](#ActorConcept-ActiveRecordState-AccessingState)
    -  [Retrieving State](#ActorConcept-ActiveRecordState-RetrievingState)
    -  [Writing State](#ActorConcept-ActiveRecordState-WritingState)
    -  [Clearing State](#ActorConcept-ActiveRecordState-ClearingState)
    -  [Writing State On Deactivation](#ActorConcept-ActiveRecordState-WritingStateOnDeactivation)



Overview {#ActorConcept-ActiveRecordState-Overview}
----------


Active Record state persistence stores the entire current state of the Actor.


State changes overwrite the existing state.


Working with Active Record {#ActorConcept-ActiveRecordState-WorkingwithActiveRecord}
----------


###Adding State {#ActorConcept-ActiveRecordState-AddingState}


Adding active record state to an actor in Orbit is simple. When extending AbstractActor the developer simply passes a state object as a generic parameter.


The state object must be serializeable.

**Active Record Actor** 
{% highlight java %}
public class StatefulActor extends AbstractActor<StatefulActor.State> implements Some
{
    public static class State
    {
        String lastMessage;
    }
}
{% endhighlight %}

 


###Accessing State {#ActorConcept-ActiveRecordState-AccessingState}


Accessing active record state in a stateful actor is simple. The state methods provides access to the current state.

**Accessing State** 
{% highlight java %}
public Task doSomeState()
{
    System.out.println(state().lastMessage);
    state().lastMessage = "Meep";
    return Task.done();
}
{% endhighlight %}

 


###Retrieving State {#ActorConcept-ActiveRecordState-RetrievingState}


Active record state is automatically retrieved when an actor is activated.


Developers can also manually re-retrieve the state using the readState method.

**Retrieving State** 
{% highlight java %}
public Task doReadState()
{
    await(readState());
    // New state is accessible here	
    return Task.done();
}
{% endhighlight %}

 


###Writing State {#ActorConcept-ActiveRecordState-WritingState}


The writing of active record state is determined only by developers, Orbit will not automatically write state.

**Writing State** 
{% highlight java %}
public Task doWriteState()
{
    return writeState();
}
{% endhighlight %}

 


###Clearing State {#ActorConcept-ActiveRecordState-ClearingState}


While actors are never created or destroyed in Orbit, developers can choose to clear an actors state if they wish.

**Clearing State** 
{% highlight java %}
public Task doClearState()
{
    return clearState();
}
{% endhighlight %}

 


###Writing State On Deactivation {#ActorConcept-ActiveRecordState-WritingStateOnDeactivation}


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
