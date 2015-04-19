---
layout : page
title : "Orbit : Async"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html)"
next : "orbit-async-overview.html"
previous: "orbit-actor-tutorial-cross-actor-communication.html"
---
{% include JB/setup %}

[Async Overview](orbit-async-overview.html) {#Async-AsyncOverviewAsync-Overview_99900938_html}
----------


 

**Orbit Task Example** 
{% highlight java %}
import com.ea.orbit.async.Await;
import static com.ea.orbit.async.Await.await;
 
public class Page
{
    // has to be done at least once, usually in the main class.
    static { Await.init(); }
 
    public Task<Integer> getPageLength(URL url)
    {
        Task<String> pageTask = getPage(url);

        // this will never block, it will return a promise
        String page = await(pageTask);

        return Task.fromValue(page.length());
    }
}

Task<Integer> lenTask = getPageLength(new URL("http://example.com"));
System.out.println(lenTask.join());
{% endhighlight %}
**CompletableFuture Example** 
{% highlight java %}
import com.ea.orbit.async.Async;
import com.ea.orbit.async.Await;
import static com.ea.orbit.async.Await.await;

public class Page
{
    // has to be done at least once, usually in the main class.
    static { Await.init(); }

    // must mark CompletableFuture methods with @Async
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
