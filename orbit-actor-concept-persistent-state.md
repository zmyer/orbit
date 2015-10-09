---
layout : page
title : "Orbit : Actor Concept - Persistent State"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Actors](orbit-actors.html) / [Actor Concepts](orbit-actor-concepts.html)"
next : "orbit-actor-concept-active-record-state.html"
previous: "orbit-actor-concept-stages.html"
---
{% include JB/setup %}



-  [Overview](#ActorConcept-PersistentState-Overview)
-  [Working With State](#ActorConcept-PersistentState-WorkingWithState)
    -  [Active Record](#ActorConcept-PersistentState-ActiveRecord)
    -  [Event Sourcing](#ActorConcept-PersistentState-EventSourcing)
-  [Storage Extensions](#ActorConcept-PersistentState-StorageExtensions)
    -  [Official Extensions](#ActorConcept-PersistentState-OfficialExtensions)
    -  [Contributed Extensions](#ActorConcept-PersistentState-ContributedExtensions)



 


Overview {#ActorConcept-PersistentState-Overview}
----------


In Orbit actor state is typically handled as part of the system itself rather than storage strategies being entirely defined by the developer.


State is automatically retrieved when an actor is activated. Writing state is developer defined.


Interaction with most state methods will result in a standard Orbit Task being returned.


 


Working With State {#ActorConcept-PersistentState-WorkingWithState}
----------


There are two methods for interacting with state in Orbit.


 


###Active Record {#ActorConcept-PersistentState-ActiveRecord}


Active Record state stores the entire current state of the Actor.


State changes overwrite the existing state.


Learn more about active record [here](orbit-actor-concept-active-record-state.html).


 


###Event Sourcing {#ActorConcept-PersistentState-EventSourcing}


Event Sourced state stores the history of an Actor's state by recording Events which change the Actor's state.


Events can be replayed to recover the current valid state for the Actor.


Learn more about event sourcing [here](orbit-actor-concept-event-sourced-state.html).


 


Storage Extensions {#ActorConcept-PersistentState-StorageExtensions}
----------


The underlying storage mechanism for state in Orbit is determined by the Storage Extensions.


There are multiple extensions available for developers. Developers are also free to add storage extensions for any storage system they wish (Databases etc).


 


###Official Extensions {#ActorConcept-PersistentState-OfficialExtensions}

**MongoDB** 
{% highlight xml %}
Source Path: actors/extensions/mongodb
Group ID: com.ea.orbit
Artifact ID: orbit-actors-mongodb
Test Profile: withMongoTests
{% endhighlight %}
**DynamoDB** 
{% highlight xml %}
Source Path: actors/extensions/dynamodb
Group ID: com.ea.orbit
Artifact ID: orbit-actors-dynamodb
Test Profile: withDynamoDBTests
{% endhighlight %}

 


###Contributed Extensions {#ActorConcept-PersistentState-ContributedExtensions}

**Redis** 
{% highlight xml %}
Source Path: actors/extensions/redis
Group ID: com.ea.orbit
Artifact ID: orbit-actors-redis
Test Profile: withRedisTests
{% endhighlight %}
**PostgreSQL** 
{% highlight xml %}
Source Path: actors/extensions/postgresql
Group ID: com.ea.orbit
Artifact ID: orbit-actors-postgresql
Test Profile: withPostgresTests
{% endhighlight %}
**Memcached** 
{% highlight xml %}
Source Path: actors/extensions/memcached
Group ID: com.ea.orbit
Artifact ID: orbit-actors-memcached
Test Profile: withMemcachedTests
{% endhighlight %}
**JPA** 
{% highlight xml %}
Source Path: actors/extensions/jpa
Group ID: com.ea.orbit
Artifact ID: orbit-actors-jpa
Test Profile: withJpaTests
{% endhighlight %}
**LDAP** 
{% highlight xml %}
Source Path: actors/extensions/ldap
Group ID: com.ea.orbit
Artifact ID: orbit-actors-ldap
Test Profile: withLDAPTests
{% endhighlight %}
