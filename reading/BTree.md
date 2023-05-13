# B-Tree (2-3-4 / 2-3 trees)

## Intuition
Set a limit on the number of elements in a single node. Let's say 4. If we need to add a new element to a node when it already has 4 elements, we will split the node in half. by bumping up the middle left node.

By splitting nodes in the middle, we maintain perfect balance! These trees are called __B-trees__ or __2-3-4/2-3 Trees__. 2-3-4 and 2-3 refer to the number of children each node can have. So, a 2-3-4 tree can have 2, 3, or 4 children while a 2-3 tree can have 2 or 3. This means that 2-3-4 trees split nodes when they have 3 nodes and one more needs to be added. 2-3 trees split after they have 2 nodes and one more needs to be added.

## Insertion

The process of adding a node to a 2-3-4 tree is:
- We still always inserting into a leaf node, so take the node you want to insert and traverse down the tree with it, going left and right according to whether or not the node to be inserted is greater than or smaller than the items in each node.
- After adding the node to the leaf node, if the new node has 4 nodes, then pop up the middle left node and re-arrange the children accordingly.
- If this results in the parent node having 4 nodes, then pop up the middle left node again, rearranging the children accordingly.
- Repeat this process until the parent node can accommodate or you get to the root.

For a 2-3 tree, go through the same process except push up the middle node in a 3-element node.

## B-Tree invariants
Yes, depending on the order you insert nodes the height of a B-tree may change. However, the tree will always be __bushy__.

A B-tree has the following helpful invariants:
- All leaves must be the same distance from the source.
- A non-leaf node with k items must have exactly k+1 children.

In tandem, these invariants cause the tree to always be bushy.

## Runtime analysis
The worst-case runtime situation for search in a B-tree would be if each node had the maximum number of elements in it and we had to traverse all the way to the bottom. We will use L to denote the number of elements in each node. This means would would need to explore logN nodes (since the max height is logN due to the bushiness invariant) and at each node we would need to explore L elements. In total, we would need to run LlogN operations. However, we know L is a constant, so our total runtime is O(logN).

## Summary
B-Trees are a modification of the binary search tree that avoids Θ(N) worst case.

- Nodes may contain between 1 and L items.
- contains works almost exactly like a normal BST.
- add works by adding items to existing leaf nodes.
	- If nodes are too full, they split.
- Resulting tree has perfect balance. Runtime for operations is  O(logN).
- Have not discussed deletion. 
- Have not discussed how splitting works if L>3 (see some other class).
- B-trees are more complex, but they can efficiently handle __ANY__ insertion order.