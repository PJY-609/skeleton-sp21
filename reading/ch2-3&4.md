# Lists 3

__SLList Drawbacks__ `addLast()` is slow! We can’t add to the middle of our list. In addition, if our list is really large, we have to start at the front, and loop all the way to the back of our list before adding our element.

__A Naive Solution__ Recall that we cached the size of our list as an instance variable of SLList. What if we cached the last element in our list as well? All of a sudden, `addLast()` is fast again; we access the last element immediately, then add our element in. But `removeLast()` is still slow. In `removeLast()`, we have to know what our second-to-last element is, so we can point our cached last variable to it. We could then cache a second-to-last variable, but now if I ever want to remove the second-to-last element, I need to know where our third-to-last element is. How to solve this problem?

__DLList__ The solution is to give each `IntNode` a prev pointer, pointing to the previous item. This creates a doubly-linked list, or `DLList`. With this modification, adding and removing from the front and back of our list becomes fast (although adding/removing from the middle remains slow).

__Incorporating the Sentinel__ Recall that we added a sentinel node to our `SLList`. For `DLList`, we can either have two sentinels (one for the front, and one for the back), or we can use a circular sentinel. A `DLList` using a circular sentinel has one sentinel. The sentinel points to the first element of the list with next and the last element of the list with prev. In addition, the last element of the list’s next points to the sentinel and the first element of the list’s prev points to the sentinel. For an empty list, the sentinel points to itself in both directions.

__Generic DLList__ How can we modify our `DLList` so that it can be a list of whatever objects we choose? Recall that our class definition looks like this:
```
public class DLList { ... }
```
We will change this to
```
public class DLList<T> { ... }
```
where `T` is a placeholder object type. Notice the angle bracket syntax. Also note that we don’t have to use `T`; any variable name is fine. In our `DLList`, our item is now of type `T`, and our methods now take `T` instances as parameters. We can also rename our `IntNode` class to `TNode` for accuracy.

__Using Generic DLList__ Recall that to create a `DLList`, we typed:
```
DLList list = new DLList(10);
```
If we now want to create a `DLList` holding `String` objects, then we must say:
```
DLList<String> list = new DLList<>("bone");
```
On list creation, the compiler replaces all instances of `T` with `String`! We will cover generic typing in more detail in later lectures.

__Arrays__ Recall that variables are just boxes of bits.

__Instantiating Arrays__ There are three valid notations for creating arrays. The first way specifies the size of the array, and fills the array with default values:
```
int[] y = new int[3];
```
The second and third ways fill up the array with specific values.
```
int[] x = new int[]{1, 2, 3, 4, 5};
int[] w = {1, 2, 3, 4, 5};
```
We can set a value in an array by using array indexing. For example, we can say `A[3] = 4;`.

__Arraycopy__ In order to make a copy of an array, we can use `System.arraycopy`. 

__2D Arrays__ We can declare multidimensional arrays. For 2D integer arrays, we use the syntax:
```
int[][] array = new int[4][];
int[][] array = new int[4][4];
int[][] array = new int[][]{{1}, {1, 2}, {1, 2, 3}}
```

