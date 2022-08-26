# Lists 2

__Naked Data Structures__ `IntLists` are hard to use. In order to use an IntList correctly, the programmer must understand and utilize recursion even for simple list related tasks.

__Adding Clothes First__, we will turn the `IntList` class into an `IntNode` class. Then, we will delete any methods in the `IntNode` class. Next, we will create a new class called `SLList`, which contains the instance variable first, and this variable should be of type `IntNode`. In essence, we have “wrapped” our `IntNode` with an `SLList`.

__Using `SLList`__ As a user, to create a list, I call the constructor for `SLList`, and pass in the number I wish to fill my list with. The `SLList` constructor will then call the IntList constructor with that number, and set first to point to the IntList it just created.

__Improvement__ Notice that when creating a list with one value, we wrote `SLList list = new SLList(1)`. We did not have to worry about passing in a null value like we did with our `IntList` Essentially, the `SLList` class acts as a middleman between the list user and the naked IntList.

__Public vs. Private__ We want users to modify our list via `SLList` methods only, and not by directly modifying first. We can prevent other users from doing so by setting our variable access to private. Writing private `IntNode` first; prevents code in other classes from accessing and modifying first (while the code inside the class can still do so).

__Nested Classes__ We can also move classes into classes to make nested classes! You can also declare the nested classes to be private as well; this way, other classes can never use this nested class.

__Static Nested Classes__ If the `IntNode` class never uses any variable or method of the `SLList` class, we can turn this class static by adding the “static” keyword.

__Recursive Helper Methods__ If we want to write a recursive method in `SLList`, how would we go about doing that? After all, the `SLList` is not a naturally recursive data structure like the `IntNode`. A common idea is to write an outer method that users can call. This method calls a private helper method that takes `IntNode` as a parameter. This helper method will then perform the recursion, and return the answer back to the outer method.

__Caching__ Previously, we calculated the size of our `IntList` recursively by returning 1 + the size of the rest of our list. This becomes really slow if our list becomes really big, and we repeatedly call our size method. Now that we have an `SLList`, lets simply cache the size of our list as an instance variable! Note that we could not do this before with out `IntList`.

__Empty Lists__ With an `SLList`, we can now represent an empty list. We simply set first to null and size to 0. However, we have introduced some bugs; namely, because first is now null, any method that tries to access a property of `first` (like `first.item`) will return a `NullPointerException`. Of course, we can fix this bug by writing code that handles this special case. But there may be many special cases. Is there a better solution?

__Sentinel Nodes__ Lets make all `SLList` objects, even empty lists, the same. To do this, lets give each `SLList` a sentinel node, a node that is always there. Actual elements go after the sentinel node, and all of our methods should respect the idea that sentinel is always the first element in our list.

__Invariants__ An invariant is a fact about a data structure that is guaranteed to be true (assuming there are no bugs in your code). This gives us a convenient checklist every time we add a feature to our data structure. Users are also guaranteed certain properties that they trust will be maintained. For example, an `SLList` with a sentinel node has at least the following invariants:
- The sentinel reference always points to a sentinel node.
- The front item (if it exists), is always at `sentinel.next.item`.
- The size variable is always the total number of items that have been added.