---
layout : page
title : "Orbit : Async Overview"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Async](orbit-async.html)"
next : "orbit-async-guide.html"
previous: "orbit-async.html"
---
{% include JB/setup %}



-  [Brief](#AsyncOverview-Brief)
-  [Dependencies](#AsyncOverview-Dependencies)
-  [FAQs](#AsyncOverview-FAQs)
    -  [Why not use another solution?](#AsyncOverview-Whynotuseanothersolution_)



Brief {#AsyncOverview-Brief}
----------


Orbit Async implements async-await methods in the JVM. It allows programmers to write asynchronous code in a sequential fashion.


It leverages features introduces in JVM8 to offer a lightweight async-await featureset that is easy to understand and make use of.


Orbit Async is heavily inspired by the [asynchronous programming](https://msdn.microsoft.com/en-us/library/hh191443.aspx) features offered by the .NET CLR.


Dependencies {#AsyncOverview-Dependencies}
----------


The Orbit Async module can be used independently of other orbit modules. It supports methods which return an Orbit Task and Java CompletableFuture, there is no requirement for your application to be structured in a specific way to leverage async.


FAQs {#AsyncOverview-FAQs}
----------


####Why not use another solution? {#AsyncOverview-Whynotuseanothersolution_}


Unlike other solutions, Orbit Async aims to be as lightweight as possible and provides only async-await methods without any additional complexity or features. 



