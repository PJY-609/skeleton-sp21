# KD-Tree


Essentially the idea of a K-D tree is that it’s a normal Binary Search Tree, except we alternate what value we’re looking at when we traverse through the tree. 
For example at the root everything to the left has an X value less than the root and everything to the right has a X value greater than the root. 
Then on the next level, every item to the left of some node has a Y value less than that item and everything to the right has a Y value greater than it. 

Nearest Pseudocode

Nearest is a helper method that returns whichever is closer to goal out of the following two choices:
1. best
2. all items in the subtree starting at n

```
nearest(Node n, Point goal, Node best):
    If n is null, return best
    If n.distance(goal) < best.distance(goal), best = n
    If goal < n (according to n’s comparator):
        goodSide = n.”left”Child
        badSide = n.”right”Child
    else:
        goodSide = n.”right”Child
        badSide = n.”left”Child
    best = nearest(goodSide, goal, best)
    If bad side could still have something useful  (That's the pruning rule.)
        best = nearest(badSide, goal, best)
    return best

```