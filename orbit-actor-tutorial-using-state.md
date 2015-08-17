---
layout : page
title : "Orbit : Actor Tutorial - Using State"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Actors](orbit-actors.html) / [Actor Tutorials](orbit-actor-tutorials.html)"
next : "orbit-actor-tutorial-observers.html"
previous: "orbit-actor-tutorial-structuring-your-project.html"
---
{% include JB/setup %}



-  [Overview](#ActorTutorial-UsingState-Overview)
-  [Choosing A Storage Extension](#ActorTutorial-UsingState-ChoosingAStorageExtension)
-  [Actor Interface](#ActorTutorial-UsingState-ActorInterface)
-  [Actor Implementation](#ActorTutorial-UsingState-ActorImplementation)
-  [Using The Actor](#ActorTutorial-UsingState-UsingTheActor)
-  [Running](#ActorTutorial-UsingState-Running)



Overview {#ActorTutorial-UsingState-Overview}
----------


In this guide, we'll show how to create stateful actors which persist state to storage. We'll be adapting the original [Hello World](orbit-actor-tutorial-hello-world.html) example.


We'll store the last message that was sent, and allow that to be retrieved.




Choosing A Storage Extension {#ActorTutorial-UsingState-ChoosingAStorageExtension}
----------


Orbit supports different storage extensions and you are able to create one manually.


In this example we are going to use the primary storage extension provided by the Orbit team, MongoDB.


In order to use the MongoDB extension, you'll need to add the following to your Maven dependencies:


{% highlight xml %}
<dependency>
    <groupId>com.ea.orbit</groupId>
    <artifactId>orbit-actors-mongodb</artifactId>
    <version>[ORBIT-VERSION]</version>
</dependency>
{% endhighlight %}



Actor Interface {#ActorTutorial-UsingState-ActorInterface}
----------


First we'll make a small change to the actor example to support getting the last message.

**Hello.java** 
{% highlight java %}
package com.example.orbit.hello;

import com.ea.orbit.actors.Actor;
import com.ea.orbit.concurrent.Task;
 
public interface Hello extends Actor
{
    Task<String> sayHello(String greeting);
    Task<String> getLastHello();
}
{% endhighlight %}

No other changes are required to the interface to support state. An actor interface does not know whether an actor is stateful or not.




Actor Implementation {#ActorTutorial-UsingState-ActorImplementation}
----------


Next we'll adapt the actor implementation to support state

**HelloActor.java** 
{% highlight java %}
package com.example.orbit.hello;

import com.ea.orbit.actors.runtime.AbstractActor;
import com.ea.orbit.concurrent.Task;
import com.ea.orbit.async.Await;
import static com.ea.orbit.async.Await.await;
 
public class HelloActor extends AbstractActor<HelloActor.State> implements Hello
{
    public static class State
    {
        String lastMessage;
    }
 
    public Task<String> sayHello(String greeting)
    {
        getLogger().info("Here: " + greeting);
        String message = "You said: '" + greeting + "', I say: Hello from " + System.identityHashCode(this) + " !";
        state().lastMessage = message;
        await(writeState());
        return message;
    }
 
    public Task<String> getLastHello()
    {
        return Task.fromValue(state().lastMessage);
    }
}


{% endhighlight %}

Important notes:


-  Notice that in this example we're passing HelloActor.State as a generic into AbstractActor. This is what makes an actor stateful.
-  State can be accessed using the state() method
-  State will automatically be retrieved on actor activation, so there is no need to read the state manually
-  The return value is then chained so the Task will only be complete once the writeState has taken place.

 


Using The Actor {#ActorTutorial-UsingState-UsingTheActor}
----------


The final step to get a working example is for us to actually use the actor.

**Main.java** 
{% highlight java %}
package com.example.orbit.hello;

import com.ea.orbit.actors.Stage;
import com.ea.orbit.actors.extensions.mongodb.MongoDBStorageExtension;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        final String clusterName = "helloWorldCluster." + System.currentTimeMillis();
               
        OrbitStage stage1 = initStage(clusterName, "stage1");
        OrbitStage stage2 = initStage(clusterName, "stage2");

        final MongoDBStorageExtension storageExtension = new MongoDBStorageExtension();
        storageExtension.setDatabase("database_name");

        stage1.addExtension(storageExtension);
        stage2.addExtension(storageExtension);

        stage1.bind();
        Hello helloFrom1 = Actor.getReference(Hello.class, "0");
        stage2.bind();
        Hello helloFrom2 = Actor.getReference(Hello.class, "0");

        System.out.println(helloFrom1.sayHello("Hi from 01").get());
        System.out.println("Last From 2: " + helloFrom2.getLastHello().get());
        System.out.println(helloFrom2.sayHello("Hi from 02").get());
        System.out.println("Last From 1: " + helloFrom1.getLastHello().get());
    }
 
    public static OrbitStage initStage(String clusterId, String stageId) throws Exception
    {
        Stagestage = new Stage();
        stage.setClusterName(clusterId);
        stage.start().join();
        return stage;
    }
}
{% endhighlight %}

Important Notes:


-  The default storage extension is created as MongoDB, you can initialize any storage extension you like
-  Calls to getLastHello have been introduced

 


Running {#ActorTutorial-UsingState-Running}
----------


If all has gone well, you should get output similar to the following:


{% highlight xml %}
-------------------------------------------------------------------
GMS: address=helloWorldCluster.1425669031908, cluster=ISPN, physical address=10.0.11.51:62166
-------------------------------------------------------------------
-------------------------------------------------------------------
GMS: address=helloWorldCluster.1425669031908, cluster=ISPN, physical address=10.0.11.51:59722
-------------------------------------------------------------------
You said: 'Hi from 01', I say: Hello from 179642099 !
Last From 2: You said: 'Hi from 01', I say: Hello from 179642099 !
You said: 'Hi from 02', I say: Hello from 179642099 !
Last From 1: You said: 'Hi from 02', I say: Hello from 179642099 !
{% endhighlight %}


