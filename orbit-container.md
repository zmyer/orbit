---
layout : page
title : "Orbit : Container"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html)"
next : "orbit-container-overview.html"
previous: "orbit-async.html"
---
{% include JB/setup %}

[Container Overview](orbit-container-overview.html) {#Container-ContainerOverviewContainer-Overview_97848940_html}
----------


[Container Guide](orbit-container-guide.html) {#Container-ContainerGuideContainer-Guide_97859542_html}
----------


 

**Container Config Example** 
{% highlight java %}
# classpath:conf/config.yaml
 
house.pets:
  - Dougie
  - Puma
 
club.name: Summer

----
 
@Singleton
public class Club {
    @Config("club.name");
    public String name = "noname";
}
 
public class House {
    @Config("house.pets")
    private List<String> pets;
 
    @Inject 
    private Club club;
 
    public void print() {
        System.out.println("Club: " + club.name + " pets: " + pets);
    }
} 

@Singleton
public class Party implements Startable {
    @Inject 
    private House house;
    
    public Task start() {
         System.out.println("Party started at: ");
         house.print();
         return Task.done();
    } 
 
    public Task stop() {
         System.out.println("See you soon!");
         return Task.done();
    }  
}
 
Container container = new Container();
container.add(Party.class);
container.start().join();
container.stop().join();


{% endhighlight %}
