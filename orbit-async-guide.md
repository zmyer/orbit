---
layout : page
title : "Orbit : Async Guide"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Async](orbit-async.html)"
next : "orbit-container.html"
previous: "orbit-async-overview.html"
---
{% include JB/setup %}



-  [Starting Async](#AsyncGuide-StartingAsync)
-  [Using Async](#AsyncGuide-UsingAsync)
    -  [Orbit Tasks](#AsyncGuide-OrbitTasks)
    -  [CompletableFuture](#AsyncGuide-CompletableFuture)



Starting Async {#AsyncGuide-StartingAsync}
----------


Before you can leverage the features in Orbit Async it must be started. Only a single method call is required to initialize the system, this is usually called during your application startup sequence.


Note: If your application leverages Orbit Actors or Orbit Container there is no need to initialize async manually, Orbit will initialize it during internal bootstrap.


{% highlight java %}
import com.ea.orbit.async.Await;
 
class ApplicationBootstrap
{
    public static void main(String[] args) 
    {
        Await.init(); 
        // App logic
    }
}
{% endhighlight %}

Using Async {#AsyncGuide-UsingAsync}
----------


Orbit Async supports async-await behavior for any method which returns an Orbit Task or Java CompletableFuture.


 


###Orbit Tasks {#AsyncGuide-OrbitTasks}


The easiest way to use Orbit Async is with Orbit tasks. No special configuration is required in order for it to function.

**Orbit Task Example** 
{% highlight java %}
import com.ea.orbit.async.Await;
import static com.ea.orbit.async.Await.await;
 
public class Page
{
    public Task<Integer> getPageLength(URL url)
    {
        Task<String> pageTask = getPage(url);
        String page = await(pageTask);
        return Task.fromValue(page.length());
    }
}

Task<Integer> lenTask = getPageLength(new URL("http://example.com"));
System.out.println(lenTask.join());
{% endhighlight %}

 


###CompletableFuture {#AsyncGuide-CompletableFuture}


Orbit Async also supports methods which return a CompletableFuture. This is similar to Orbit Tasks but the @Async method annotation is required.

**CompletableFuture Example** 
{% highlight java %}
import com.ea.orbit.async.Async;
import com.ea.orbit.async.Await;
import static com.ea.orbit.async.Await.await;

public class Page
{
    @Async
    public CompletableFuture<Integer> getPageLength(URL url)
    {
        CompletableFuture<String> pageTask = getPage(url);
        String page = await(pageTask);
        return CompletableFuture.completedFuture(page.length());
    }
 }

CompletableFuture<Integer> lenTask = getPageLength(new URL("http://example.com"));
System.out.println(lenTask.join());
{% endhighlight %}
