---
layout : page
title : "Orbit : Coding Standards"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Policies](orbit-policies.html)"
next : "orbit-acknowledgements.html"
previous: "orbit-policies.html"
---
{% include JB/setup %}



-  [Overview](#CodingStandards-Overview)
-  [Naming Conventions](#CodingStandards-NamingConventions)
    -  [Classes / Interfaces](#CodingStandards-Classes)
    -  [Enums](#CodingStandards-Enums)
    -  [Methods](#CodingStandards-Methods)
    -  [Member Variables](#CodingStandards-MemberVariables)
    -  [Local Variables and Arguments](#CodingStandards-LocalVariablesandArguments)
    -  [Constants](#CodingStandards-Constants)
    -  [Package Names](#CodingStandards-PackageNames)
-  [Code Style](#CodingStandards-CodeStyle)
    -  [Braces](#CodingStandards-Braces)
    -  [Assignment / Comparison](#CodingStandards-Assignment_Comparison)
    -  [Brackets](#CodingStandards-Brackets)
    -  [Method/Class Annotations](#CodingStandards-Method_ClassAnnotations)
    -  [Member Variable/Method Argument Annotations](#CodingStandards-MemberVariable_MethodArgumentAnnotations)



Overview {#CodingStandards-Overview}
----------


Orbit follows the coding standards laid out in this document.


We realize that standards are often contentious and believe that having any standard (even where not everyone agrees) is the best course of action to ensure consistent and readable code across the project.


Naming Conventions {#CodingStandards-NamingConventions}
----------


###Classes {#CodingStandards-Classes}

**Use PascalCase** 
{% highlight java %}
public interface User extends Actor
{
}

public class UserActor extends AbstractActor implements User
{
}
{% endhighlight %}

###Enums {#CodingStandards-Enums}

**Use PascalCase for name, UPPER_CASE for constants** 
{% highlight java %}
public enum StageMode
{
    FRONT_END, 
    HOST
}
{% endhighlight %}

###Methods {#CodingStandards-Methods}

**Use camelCase** 
{% highlight java %}
public void doSomeStuff()
{
}
{% endhighlight %}

###Member Variables {#CodingStandards-MemberVariables}

**Use camelCase** 
{% highlight java %}
public class SecurityFilter
{
    private int userId;
}
{% endhighlight %}

###Local Variables and Arguments {#CodingStandards-LocalVariablesandArguments}

**Use camelCase** 
{% highlight java %}
public static int sum(int leftHandSide, int rightHandSide)
{
    int totalSum = leftHandSide + rightHandSide;
    return totalSum;
}
{% endhighlight %}

###Constants {#CodingStandards-Constants}

**Use UPPER_CASE** 
{% highlight java %}
public static final byte NORMAL_MESSAGE = 0;
{% endhighlight %}

###Package Names {#CodingStandards-PackageNames}

**Use lowercase** 
{% highlight java %}
package com.ea.orbit.actors;
{% endhighlight %}

 


Code Style {#CodingStandards-CodeStyle}
----------


###Braces {#CodingStandards-Braces}


Opening braces should always be on a new line, always align the opening and closing of a block.


{% highlight java %}
if(someValue.equals("Ferrets"))
{
    if(someOtherValue.equals("Penguins"))
    {
    }
}
{% endhighlight %}

###Assignment / Comparison {#CodingStandards-Assignment_Comparison}


Always include a space before and after an assignment or comparison.


{% highlight java %}
someValue += 1;
if(someValue == 1)
{
}
{% endhighlight %}

###Brackets {#CodingStandards-Brackets}


Never include a space after an opening bracket or before a closing bracket, optionally include one before an opening bracket.


{% highlight java %}
if (someValue.equals("Ferrets"))
if(someValue.equals("Ferrets"))
{% endhighlight %}

###Method/Class Annotations {#CodingStandards-Method_ClassAnnotations}


Always annotate on a separate line above.


{% highlight java %}
@GET 
@PermitAll 
@Path("/healthCheck")
public HealthCheckDto getHealthCheck()
{
}
{% endhighlight %}

###Member Variable/Method Argument Annotations {#CodingStandards-MemberVariable_MethodArgumentAnnotations}


Always annotate on a separate line above for member variables. Always annotate on the same line for method arguments.


{% highlight java %}
@Inject
StorageManager storageManager;
 
@Inject 
OrbitPropertiesProxy propertiesProxy;
 
public String getSomeValue(@Context SessionProxy sessionProxy)
{
}
{% endhighlight %}


