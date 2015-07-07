---
layout : page
title : "Orbit : Agent Loader"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Utils](orbit-utils.html)"
next : "orbit-samples.html"
previous: "orbit-rest-client.html"
---
{% include JB/setup %}



-  [Overview](#AgentLoader-Overview)
-  [Example](#AgentLoader-Example)



Overview {#AgentLoader-Overview}
----------


Orbit Agent Loader is a collection of utilities for [java agent](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/package-summary.html) developers. It allows programmers to write and test their java agents using dynamic agent loading (without using the -javaavent jvm parameter).




Example {#AgentLoader-Example}
----------


{% highlight java %}
public class HelloAgentWorld
{
    public static class HelloAgent
    {
        public static void agentmain(String agentArgs, Instrumentation inst)
        {
            System.out.println(agentArgs);
            System.out.println("Hi from the agent!");
            System.out.println("I've got instrumentation!: " + inst);
        }
    }

    public static void main(String[] args)
    {
        AgentLoader.loadAgentClass(HelloAgent.class.getName(), "Hello!");
    }
}
{% endhighlight %}


