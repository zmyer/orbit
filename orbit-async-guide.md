---
layout : page
title : "Orbit : Async Guide"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Async](orbit-async.html)"
next : "orbit-container.html"
previous: "orbit-async-overview.html"
---
{% include JB/setup %}



-  [Implementing Async](#AsyncGuide-ImplementingAsync)
    -  [Option 1 - Runtime](#AsyncGuide-Option1-Runtime)
    -  [Option 2 - JVM Parameter](#AsyncGuide-Option2-JVMParameter)
    -  [Option 3 - Compile Time Instrumentation (Maven)](#AsyncGuide-Option3-CompileTimeInstrumentation_Maven_)
-  [Using Async](#AsyncGuide-UsingAsync)
    -  [Orbit Tasks](#AsyncGuide-OrbitTasks)
    -  [CompletableFuture](#AsyncGuide-CompletableFuture)



Implementing Async {#AsyncGuide-ImplementingAsync}
----------


Before you can leverage the features in Orbit Async your code must be instrumented.


Note: If your application leverages Orbit Actors or Orbit Container there is no need to instrument async manually (though you are free to do so), Orbit will initialize it during bootstrap using the runtime bytecode weaving.


There are 3 options for instrumenting your code. Please choose the most appropriate for your use case.


###Option 1 - Runtime {#AsyncGuide-Option1-Runtime}


On your main class or as early as possible, call at least once:


{% highlight java %}
Await.init();
{% endhighlight %}

Provided that your JVM has the capability enabled, this will start a runtime instrumentation agent.


This is the prefered solution for testing and development, it has the least amount of configuration. If you forget to invoke this function, the first call to await will initialize the system (and print a warning).


###Option 2 - JVM Parameter {#AsyncGuide-Option2-JVMParameter}


Start your application with an extra JVM parameter: -javaagent:orbit-async-VERSION.jar


{% highlight xml %}
java -javaagent:orbit-async-VERSION.jar -cp your_claspath YourMainClass args...
{% endhighlight %}

###Option 3 - Compile Time Instrumentation (Maven) {#AsyncGuide-Option3-CompileTimeInstrumentation_Maven_}


Use the [orbit-async-maven-plugin](https://github.com/electronicarts/orbit/blob/master/async/maven-plugin). It will instrument your classes in compile time and remove all references to await.


This is the best option for libraries.


{% highlight xml %}
<build>
    <plugins>
        <plugin>
            <groupId>com.ea.orbit</groupId>
            <artifactId>orbit-async-maven-plugin</artifactId>
            <version>${orbit.version}</version>
            <executions>
                <execution>
                    <goals>
                        <goal>instrument</goal>
                        <goal>instrument-test</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
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
