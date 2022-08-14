# Inheritance 1

__Method Overloading__ In Java, methods in a class can have the same name, but different parameters. For example, a `Math` class can have an `add(int a, int b)` method and an `add(float a, float b)` method as well. 

__Interfaces__ We will use the keyword `interface` instead of `class` to create our List. More explicitly, we write:
```
public interface List<Item> { ... }
```
The key idea is that interfaces specify what this List can do, not how to do it. 

Now, we want to specify that an `AList` is a List. We will change our class declaration of `AList` to:
```
public AList<Item> implements List<Item> { ... }
```

__Overriding__ For each method in `AList` that we also defined in List, we will add an `@Override` right above the method signature. As an example:
```
@Override
public Item get(int i) { ... }
```

__Interface Inheritance__ Formally, we say that subclasses inherit from the superclass. Interfaces contain all the method signatures, and each subclass must implement every single signature; think of it as a contract. In addition, relationships can span multiple generations.

__Default Methods__ Interfaces can have default methods. We define this via:
```
default public void method() { ... }
```
We can actually implement these methods inside the interface. Note that there are no instance variables to use, but we can freely use the methods that are defined in the interface, without worrying about the implementation. Default methods should work for any type of object that implements the interface! The subclasses do not have to re-implement the default method anywhere; they can simply call it for free. However, we can still override default methods, and re-define the method in our subclass.

__Static vs. Dynamic Type__ Every variable in Java has a static type. This is the type specified when the variable is declared, and is checked at compile time. Every variable also has a dynamic type; this type is specified when the variable is instantiated, and is checked at runtime. As an example:
```
Thing a;
a = new Fox();

Animal b = (Animal) a;

Fox c = (Fox) b;

a = new Squid()
```

__Dynamic Method Selection__ The rule is, if we have a static type X, and a dynamic type Y, then if Y overrides the method from X, then on runtime, we use the method in Y instead.

__Overloading and Dynamic Method Selection__ Dynamic method selection plays __no__ role when it comes to overloaded methods. Consider the following piece of code, where Fox extends Animal.
```
1  Fox f = new Fox();
2  Animal a = f;
3  define(f);
4  define(a);
```
Let’s assume we have the following overloaded methods in the same class:
```
public static void define(Fox f) { ... }
public static void define(Animal a) { ... }
```
Line 3 will execute `define(Fox f)`, while line 4 will execute `define(Animal a)`. Dynamic method selection only applies when we have overridden methods. There is no overriding here, and therefore dynamic method selection does not apply.

