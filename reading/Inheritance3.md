# Inheritance3

__Review: Typing Rules__
- Compiler allows the memory box to hold any subtype.
- Compiler allows calls based on static type.
- Overriden non-static methods are selected at runtime based on dynamic type.
- For overloaded methods, the method is selected at compile time.

__Subtype Polymorphism__ Consider a variable of static type `Deque`. The behavior of calling `deque.method()` depends on the dynamic type. Thus, we could have many subclasses the implement the `Deque` interface, all of which will be able to call `deque.method()`.

