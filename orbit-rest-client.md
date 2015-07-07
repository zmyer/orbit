---
layout : page
title : "Orbit : REST Client"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Utils](orbit-utils.html)"
next : "orbit-agent-loader.html"
previous: "orbit-metrics.html"
---
{% include JB/setup %}



-  [Overview](#RESTClient-Overview)
-  [Examples](#RESTClient-Examples)



Overview {#RESTClient-Overview}
----------


The Orbit Async REST Client allows calling JAX-RS REST interfaces with methods returning [CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html) or Orbit [Task](http://orbit.bioware.com/apidocs/development/com/ea/orbit/concurrent/Task.html). This enables writing REST client code in a fluent asynchronous way.


The project orbit-rest-client depends only on the [JAX-RS standard](https://jax-rs-spec.java.net/) and it is in principle compatible with any client library that provides [javax.ws.rs.client.WebTarget](http://docs.oracle.com/javaee/7/api/javax/ws/rs/client/package-summary.html). It has been tested with [jersey-client](https://jersey.java.net/documentation/latest/modules-and-dependencies.html#client-jdk).


Examples {#RESTClient-Examples}
----------

**Using Orbit Tasks** 
{% highlight java %}
public interface Hello
{
    @GET
    @Path("/")
    Task<String> getHome();
}

public static void main(String args[])
{
    WebTarget webTarget = getWebTarget("http://example.com");

    Hello hello = new OrbitRestClient(webTarget).get(Hello.class);

    Task<String> response = hello.getHome();

    response.thenAccept(x -> System.out.println(x)); 
    response.join();
}

// use any jax-rs client library to get javax.ws.rs.client.WebTarget
private static WebTarget getWebTarget(String host)
{
    ClientConfig clientConfig = new ClientConfig();
    Client client = ClientBuilder.newClient(clientConfig);
    return client.target(host);
}
{% endhighlight %}
**Using CompletableFuture** 
{% highlight java %}
public interface Hello
{
    @GET
    @Path("/")
    CompletableFuture<String> getHome();
}

public static void main(String args[])
{
    WebTarget webTarget = getWebTarget("http://example.com");

    Hello hello = new OrbitRestClient(webTarget).get(Hello.class);

    CompletableFuture<String> response = hello.getHome();

    response.thenAccept(x -> System.out.println(x)); 
    response.join();
}

// use any jax-rs client library to get javax.ws.rs.client.WebTarget
private static WebTarget getWebTarget(String host)
{
    ClientConfig clientConfig = new ClientConfig();
    Client client = ClientBuilder.newClient(clientConfig);
    return client.target(host);
}
{% endhighlight %}
