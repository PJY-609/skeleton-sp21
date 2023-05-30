# Shortest Path

## Dijktra

```
def dijkstras(source):
    PQ.add(source, 0)
    For all other vertices, v, PQ.add(v, infinity)
    while PQ is not empty:
        p = PQ.removeSmallest()
        relax(all edges from p)


def relax(edge p,q):
   if q is visited (i.e., q is not in PQ):
       return

   if distTo[p] + weight(edge) < distTo[q]:
       distTo[q] = distTo[p] + w
       edgeTo[q] = p
       PQ.changePriority(q, distTo[q])
```

Runtime is O(V×logV+V×logV+E×logV)
, and since E>V
for any graph we’d run Dijkstra’s algorithm on, this can be written as more simply O(E log V)

## A* Single-Target Shortest Paths. 

If we need only the path to a single target, then Dijkstra’s is inefficient as it explores many many edges that we don’t care about.

To fix this, we make a very minor change to Dijkstra’s, where instead of visiting vertices in order of distance from the source, we visit them in order of distance from the source + h(v), where h(v) is some heuristic.

It turns out (but we did not prove), that as long as h(v) is less than the true distance from s to v, then the result of A* will always be correct.