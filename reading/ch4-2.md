# Inheritance 2

__What is Inherited?__ We have a powerful tool in Inheritance now; however, we will define a few rules. For now, we will say that we can inherit:
- instance and static variables
- all methods
- all nested classes This changes a little bit with the introduction of private variables but don’t worry about that right now. The one item that is not inherited is a class’s constructor.

__The Special Case of the Constructor?__ Even though constructor’s are not inherited, we still use them. We can call the constructor explicitly by using the keyword `super()`. At the start of every constructor, there is already an implicit call to its super class's constructor.

__Is A.__ When a class inherits from another, we know that it must have all the qualities of it. Every single class is a descendent on the Object class, meaning they are all Objects.

__Casting__ In Java, every object has a static type (defined at compile-time) and a dynamic type (defined at run-time). Our code may rely on the fact that some variable may be a more specific type than the static type. 
For example if we had the below definitions:
```
Poodle frank  = new Poodle("Frank", 5);
Poodle frankJr = new Poodle("Frank Jr.", 15);
```
This statement would be valid
```
Dog largerDog = maxDog(frank, frankJr);
```
But this one would not be
```
Poodle largerPoodle = maxDog(frank, frankJr);
```
Instead of being happy with just having a generic Dog, we can be a bit risky and use a technique called casting. Casting allows us to force the static type of a variable, basically tricking the compiler into letting us force the static type of am expression. To make `largerPoodle` into a static type `Poodle` we will use the following:
```
Poodle largerPoodle = (Poodle) maxDog(frank, frankJr);
```
Note that we are not changing the actual dynamic type of `maxDog`- we are just telling the compiler what is coming out of `maxDog` will be a `Poodle`. This means that any reference to `largerPoodle` will have a static type of `Poodle` associated with it.

Casting, while powerful is also quite dangerous. You need to ensure that what you are casting to can and will actually happen. There are a few rules that can be used:
- You can always cast __up__ (to a more generic version of a class) without fear of ruining anything because we know the more specific version is a version of the generic class. For example you can always cast a Poodle to a Dog because all Poodles are Dog’s.
- You can also cast __down__ (to a more specific version of a class) with caution as you need to make sure that, during runtime, nothing is passed in that violates your cast. For example, sometimes Dog’s are Poodle’s but not always.
- Finally, you cannot ever cast to a class that is neither above or below the class being cast. For an example, you cannot cast a Dog to a Monkey because a Monkey is not in the direct lineage of a Dog - it is a child of Animal so a bit more distant. You can think of this as “__side__ casting” and it will result in a compile time error since the compiler knows this cast cannot possibly work.