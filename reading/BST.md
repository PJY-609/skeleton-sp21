# BST

## Search
```
static BST find(BST T, Key sk) {
   if (T == null)
      return null;
   if (sk.equals(T.key))
      return T;
   else if (sk ≺ T.key)
      return find(T.left, sk);
   else
      return find(T.right, sk);
}
```

## Insert
```
static BST insert(BST T, Key ik) {
  if (T == null)
    return new BST(ik);
  if (ik ≺ T.key)
    T.left = insert(T.left, ik);
  else if (ik ≻ T.key)
    T.right = insert(T.right, ik);
  return T;
}
```

## Delete
__No children__
If the node has no children, it is a leaf, and we can just delete its parent pointer and the node will eventually be swept away by the garbage collector.

__One child__
If the node only has one child, we know that the child maintains the BST property with the parent of the node because the property is recursive to the right and left subtrees. Therefore, we can just reassign the parent's child pointer to the node's child and the node will eventually be garbage collected.

__Two children__
If the node has two children, the process becomes a little more complicated because we can't just assign one of the children to be the new root. This might break the BST property.

Instead, we choose a new node to replace the deleted one.

To find these nodes, you can just take __the right-most node in the left subtree__ or __the left-most node in the right subtree__.

This is called __Hibbard deletion__, and it gloriously maintains the BST property amidst a deletion.


## BST for Sets and Maps
We can use a BST to implement the Set ADT! But its even better because in an ArraySet, we have worst-case $O(n)$ runtime to run contains because we need to search the entire set. However, if we use a BST, we can decrease this runtime to $\log (n)$ because of the BST property which enables us to use binary search!

We can also make a binary tree into a map by having each BST node hold `(key,value)` pairs instead of singular values. We will compare each element's key in order to determine where to place it within our tree.