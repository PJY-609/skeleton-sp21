# Heap / Priority Queue

## Min-Heap structure
We will define our binary min-heap as being complete and obeying min-heap property:
- Min-heap: Every node is less than or equal to both of its children
- Complete: Missing items only at the bottom level (if any), all nodes are as far __left__ as possible.

## Heap Operations

The three methods we care about for the `PriorityQueue` ADT are `add`, `getSmallest`, and `removeSmallest`. We will start by conceptually describing how these methods can be implemented given our given schema of a heap.
- `add`: Add to the end of heap temporarily. Swim up the hierarchy to the proper place.
	- Swimming involves swapping nodes if child < parent
- `getSmallest`: Return the root of the heap (This is guaranteed to be the minimum by our min-heap property
- `removeSmallest`: Swap the last item in the heap into the root. Sink down the hierarchy to the proper place.
	- Sinking involves swapping nodes if parent > child. Swap with the smallest child to preserve min-heap property.

## Tree Representation

**Approach 1**

- In approach Tree1A, we consider creating pointers to our children and storing the value inside of the node object. These are hardwired links that give us __fixed-width__ nodes. We can observe the code:

```
public class Tree1A<Key> {
  Key k;
  Tree1A left;
  Tree1A middle;
  Tree1A right;
  ...
}
```

- Alternatively, in Tree1B, we explore the use of arrays as representing the mapping between children and nodes. This would give us __variable-width__ nodes, but also awkward traversals and performance will be worse.

```
public class Tree1B<Key> {
  Key k;
  Tree1B[] children;
  ...
}
```

- Lastly, we can use the approach for Tree1C. This will be slightly different from the usual approaches that we've seen. Instead of only representing a node's children, we say that nodes can also maintain a reference to their siblings.
```
public class Tree1C<Key> {
  Key k;
  Tree1C favoredChild;
  Tree1C sibling;
  ...
}
```

**Approach 2**
For representing a tree, we can store the keys array as well as a parents array. The keys array represent which index maps to which key, and the parents array represents which key is a child of another key.

```
public class Tree2<Key> {
  Key[] keys;
  int[] parents;
  ...
}
```

**Approach 3**

In this approach, we assume that our tree is **complete**. This is to ensure that there are no "gaps" inside of our array representation. Thus, we will take this complex 2D structure of the tree and flatten it into an array.
```
public class TreeC<Key> {
  Key[] keys;
  ...
}
```

## Swim
Given this implementation, we define the following code for the "swim" described in the Heap Operations section.
```
public void swim(int k) {
    if (keys[parent(k)] ≻ keys[k]) {
       swap(k, parent(k));
       swim(parent(k));              
    }
}
```
What does the parent method do? It returns the parent of the given k using the representation in Approach 3.

## Implementation

we will leave one empty spot at the beginning of the array to simplify computation.
- `leftChild(k)` = $k∗2$
- `rightChild(k)` = $k∗2+1$
- `parent(k)` = $k/2$

## Comparing to alternative implementations

| Methods	| Ordered Array | Bushy BST | Hash Table | Heap |
| --------- | ------------- | --------- | ---------- | ---- |
| `add`	    | Θ(N)          | Θ(logN)   |	Θ(1)     | Θ(logN) |
| `getSmallest` |	Θ(1)    | Θ(logN)   |	Θ(N)     |	Θ(1)   |
| `removeSmallest` | Θ(N)   | Θ(logN)	| Θ(N)       |	Θ(logN) |