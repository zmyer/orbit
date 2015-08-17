---
layout : page
title : "Orbit : Actor Tutorial - Observers"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Actors](orbit-actors.html) / [Actor Tutorials](orbit-actor-tutorials.html)"
next : "orbit-actor-tutorial-cross-actor-communication.html"
previous: "orbit-actor-tutorial-using-state.html"
---
{% include JB/setup %}



-  [Overview](#ActorTutorial-Observers-Overview)
-  [Observer Interface](#ActorTutorial-Observers-ObserverInterface)
-  [Actor Interface](#ActorTutorial-Observers-ActorInterface)
-  [Actor Implementation](#ActorTutorial-Observers-ActorImplementation)
-  [Using The Actor](#ActorTutorial-Observers-UsingTheActor)
-  [Running](#ActorTutorial-Observers-Running)



Overview {#ActorTutorial-Observers-Overview}
----------


So far, whenever we've interacted with actors it has always been in a scenario where our external code (Main in the Hello World example) sends a message to an actor and expects a response.


In this example we'll look at another approach to working with actors, the concept of Observers. Observers allow code which is external to the actor system to observe actions which are happening within a specific actor. This allows actors to notify observers when certain actions occur without the external system sending a message to the actor.


As with other samples, we'll be working from the base Hello World Example.


 


Observer Interface {#ActorTutorial-Observers-ObserverInterface}
----------


Like actors, the first step required to create an Observer is the creation of an interface.

**HelloObserver.java** 
{% highlight java %}
package com.example.orbit.hello;

import com.ea.orbit.actors.ActorObserver;
import com.ea.orbit.annotation.OneWay;
import com.ea.orbit.concurrent.Task;

public interface HelloObserver extends ActorObserver
{
    @OneWay
    Task saidHello(String message);
}
{% endhighlight %}

Important Notes:


-  Observers must extend ActorObserver
-  Like actor interfaces, every method must return an Orbit Task.

 


Actor Interface {#ActorTutorial-Observers-ActorInterface}
----------


Next we'll just create an actor interface similar to our previous examples

**Hello.java** 
{% highlight java %}
package com.example.orbit.hello;

import com.ea.orbit.actors.Actor;
import com.ea.orbit.concurrent.Task;
 
public interface Hello extends Actor
{
    Task<String> sayHello(String greeting);
    Task registerObserver(HelloObserver observer);
}
{% endhighlight %}

Important Notes:


-  The only change is the addition of the registerObserver method.

 


Actor Implementation {#ActorTutorial-Observers-ActorImplementation}
----------


Next we'll adapt the actor implementation to use the observer system

**HelloActor.java** 
{% highlight java %}
package com.example.orbit.hello;

import com.ea.orbit.actors.runtime.AbstractActor;
import com.ea.orbit.actors.ObserverManager;
import com.ea.orbit.concurrent.Task;
 
public class HelloActor extends AbstractActor implements Hello
{
	private ObserverManager<HelloObserver> observers = new ObserverManager<>();
 
    public Task<String> sayHello(String greeting)
    {
        getLogger().info("Here: " + greeting);
        String message = "You said: '" + greeting + "', I say: Hello from " + System.identityHashCode(this) + " !";
        observers.notifyObservers(o -> o.saidHello(message));
        return Task.fromValue(message);
    }
 
    public Task registerObserver(IHelloObserver observer)
    {
        observers.addObserver(observer);
        return Task.done();
    }
}

{% endhighlight %}

Important Notes:


-  We have introduced an ObserverManager
-  The sayHello method now uses the ObserverManager to notifyObservers
-  We have implemented the registerObserver which adds an observer to our ObserverManager

Using The Actor {#ActorTutorial-Observers-UsingTheActor}
----------


We'll now use our actor with an observer

**Main.java** 
{% highlight java %}
package com.example.orbit.hello;

import com.ea.orbit.actors.Stage;
import com.ea.orbit.concurrent.Task;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        final String clusterName = "helloWorldCluster." + System.currentTimeMillis();
        Stage stage1 = initStage(clusterName, "stage1");
        Stage stage2 = initStage(clusterName, "stage2");
 
        stage1.bind();
        Hello helloFrom1 = Actor.getReference(Hello.class, "0");
        stage2.bind();
        Hello helloFrom2 = Actor.getReference(Hello.class, "0");
 
        HelloObserver observer = new HelloObserver()
        {
            @Override
            public Task saidHello(String message)
            {
                System.out.println("Observer: " + message);
                return Task.done();
            }
        };
 
        helloFrom1.registerObserver(observer).join();
        System.out.println(helloFrom2.sayHello("Hi from 02").get());
    }
 
    public static Stage initStage(String clusterId, String stageId) throws Exception
    {
        Stage stage = new Stage();
        stage.setClusterName(clusterId);
        stage.start().join();
        return stage;
    }
}
{% endhighlight %}

Important notes:


-  We have introduced an implementation of the HelloObserver
-  We register the observer with the Hello actor by calling registerObserver and wait for the result

 


Running {#ActorTutorial-Observers-Running}
----------


If everything has gone well, you should see output similar to the following:


{% highlight xml %}
-------------------------------------------------------------------
GMS: address=helloWorldCluster.1424809950235, cluster=ISPN, physical address=10.0.11.51:58155
-------------------------------------------------------------------
-------------------------------------------------------------------
GMS: address=helloWorldCluster.1424809950235, cluster=ISPN, physical address=10.0.11.51:58156
-------------------------------------------------------------------
You said: 'Hi from 02', I say: Hello from 1098118387 !
Observer: You said: 'Hi from 02', I say: Hello from 1098118387 !
{% endhighlight %}
