---
layout : page
title : "Orbit : Actor Concept - Distributed Transactions"
breadCrumb : "[Orbit](index.html) / [Public Documentation](orbit-public-documentation.html) / [Actors](orbit-actors.html) / [Actor Concepts](orbit-actor-concepts.html)"
next : "orbit-actor-concept-stateless-workers.html"
previous: "orbit-actor-concept-event-sourced-state.html"
---
{% include JB/setup %}



-  [Overview](#ActorConcept-DistributedTransactions-Overview)
-  [Using Distributed Transactions](#ActorConcept-DistributedTransactions-UsingDistributedTransactions)
    -  [Support](#ActorConcept-DistributedTransactions-Support)
    -  [Basic Transaction](#ActorConcept-DistributedTransactions-BasicTransaction)
    -  [Complex Transaction](#ActorConcept-DistributedTransactions-ComplexTransaction)



Overview {#ActorConcept-DistributedTransactions-Overview}
----------


Distributed Transactions in Orbit allow developers coordinate actions between various Orbit actors with the ability to rollback those actions in the event of a failure.


For instance, if a developer wishes to deduct currency from a user and then grant an item and these actions happens in different actors, distributed transactions would allow the developer to rollback to currency deduction if the item grant failed.


 


Using Distributed Transactions {#ActorConcept-DistributedTransactions-UsingDistributedTransactions}
----------


###Support {#ActorConcept-DistributedTransactions-Support}


Distributed Transactions are currently only supported by actors which implement Transactional. 


Out of the box, Orbit only supports Distributed Transactions when using the[ Event Sourcing](orbit-actor-concept-event-sourced-state.html) persistence model without any further work required from the application developer. Developers are free to implement support for transactions in actors manually by implementing Transactional manually.


 


###Basic Transaction {#ActorConcept-DistributedTransactions-BasicTransaction}


It is very simple to add a very basic transaction, interactions with the event source state are simply wrapped in a transaction. 

**Basic Transaction** 
{% highlight java %}
public class BankActor extends EventSourcedActor<BankActor.State> implements Bank
{
    public static class State extends TransactionalState
    {
        int balance;
 
        @TransactionalEvent
        void incrementBalance(int amount)
        {
             balance += amount;
        }
    }
    
    public Task<int> creditBalance(int amount)
    {
        return transaction(() ->
        {
            state().incrementBalance(amount);
            await(writeState());
            return Task.fromValue(state().balance);
        }
    }
}
{% endhighlight %}

###Complex Transaction {#ActorConcept-DistributedTransactions-ComplexTransaction}


Complex transactions rely on each individual transaction to be wrapped in in a higher level transaction.


Transactions must wait for nested transactions to complete, this can be achieved using await. A more efficient implementation would send both messages in parallel and then await the response of both. 

**Complex Transaction** 
{% highlight java %}
public class BankActor extends EventSourcedActor<BankActor.State> implements Bank
{
    public static class State extends TransactionalState
    {
        int balance;
 
        @TransactionalEvent
        void incrementBalance(int amount)
        {
             balance += amount;
        }
 
        @TransactionalEvent
        void decrementBalance(int amount)
        {
             balance -= amount;
        }
    }
    
    public Task<int> creditBalance(int amount)
    {
        return transaction(() ->
        {
            state().incrementBalance(amount);
            await(writeState());
            return Task.fromValue(state().balance);
        }
    }
 
    public Task<int> debitBalance(int amount)
    {
        return transaction(() ->
        {
            state().decrementBalance(amount);
            await(writeState());
            return Task.fromValue(state().balance);
        }
    }
}
 
public class TradingActor extends AbstractActor implements Trading
{
    public Task tradeMoney(Bank in, Bank out, int amount)
    {
        return transaction(() -> 
        {
          await(out.debitBalance(amount));
          await(in.creditBalance(amount));
          return Task.done();
        }
     }
}
{% endhighlight %}
