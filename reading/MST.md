# Minimum Spanning Tree

A minimum spanning tree (MST) is the lightest set of edges in a graph possible such that all the vertices are connected. 
Because it is a tree, it must be connected and acyclic. 
And it is called "spanning" since all vertices are included.

## Cut property

We can define a cut as an assignment of a graph’s nodes to two non-empty sets (i.e. we assign every node to either set number one or set number two).

We can define a crossing edge as an edge which connects a node from one set to a node from the other set.

With these two definitions, we can understand the Cut Property; given any cut, the minimum weight crossing edge is in the MST.


## Prim's Algorithm

This is one algorithm to find a MST from a graph. It is as follows:

```
1. Start from some arbitrary start node.
2. Repeatedly add the shortest edge that has one node inside the MST under construction.
3. Repeat until there are V-1 edges.
```

Prim's algorithm works because at all stages of the algorithm, if we take all the nodes that are part of our MST under construction as one set, and all other nodes as a second set, then this algorithm always adds the lightest edge that crosses this cut, which is necessarily part of the final MST by the Cut Property.

Essentially, this algorithm runs via the same mechanism as Dijkstra's algorithm, but while Dijkstra's considers candidate nodes by their distance from the source node, Prim's looks at each candidate node's distance from the MST under construction.
Yet another way of thinking about Prim’s algorithm is that it is basically just Dijkstra’s algorithm, but where we consider vertices in order of the distance from the entire tree, rather than from source. Or in pseudocode, we simply change relax so that it reads:
```
relax(e):
    v = e.source
    w = e.target        
    currentBestKnownWeight = distTo(w)
    possiblyBetterWeight = e.weight // Only difference!
    if possiblyBetterWeight > currentBestKnownWeight
        Use e instead of whatever we were using before
```

Thus, the runtime of Prim's if done using the same mechanism as Dijkstra's, would be the same as Dijkstra's, which is
O((∣V∣+∣E∣)log∣V∣). Remember, this is because we need to add to a priority queue fringe once for every edge we have, and we need to dequeue from it once for every vertex we have.


## Kruskal’s Algorithm

The algorithm is as follows:
```
1. Sort all the edges from lightest to heaviest.
2. Taking one edge at a time (in sorted order), add it to our MST under construction if doing so does not introduce a cycle.
3. Repeat until there are {% math %}V-1{% endmath %} edges.
```

Kruskal's algorithm works because any edge we add will be connecting one node, which we can say is part of one set, and a second node, which we can say is part of a second set. 
This edge we add is not part of a cycle, because we are only adding an edge if it does not introduce a cycle. 
Further, we are looking at edge candidates in order from lightest to heaviest. 
Therefore, this edge we are adding must be the lightest edge across this cut (if there was a lighter edge that would be across this cut, it would have been added before this, and adding this one would cause a cycle to appear). 
Therefore, this algorithm works by the Cut Property as well.

Kruskal's runs in O(∣E∣log∣E∣) time because the bottleneck of the algorithm is sorting all of the edges to start 
(for example, we can use heap sort, in which we insert all of the edges into a heap and remove the min one at a time). 
If we are given pre-sorted edges and don't have to pay for that, then the runtime is O(∣E∣log∣V∣). 
This is because with every edge we propose to add, we need to check whether it will introduce a cycle or not. 
One way we know how to do this is by using Weighted Quick Union with Path Compression; 
this will efficiently tell us whether two nodes are connected (unioned) together already or not. 