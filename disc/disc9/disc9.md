# Disc9: Heaps and Graphs

Assume that we have a binary min-heap (smallest value on top) data structure called `MinHeap` that has properly implemented insert and `removeMin` methods. 
Draw the heap and its corresponding array representation after each of the operations below:

`Heap<Character>h=newMinHeap<>();` 

1. `h.insert('f');`

```
| f |
```

2. `h.insert('h');`

```
| f | h |
```

3. `h.insert('d');`

```
| f | h | d |
```

swim

```
| d | h | f |
```

4. `h.insert('b');`

```
| d | h | f | b |
```

swim

```
| d | b | f | h |
```

swim

```
| b | d | f | h |
```

5. `h.insert('c');`

```
| b | d | f | h | c |
```

swim

```
| b | c | f | h | d |
```

6. `h.removeMin();`

swap

```
| d | c | f | h | b |
```

remove

```
| d | c | f | h |
```

sink

```
| c | d | f | h |
```

7. `h.removeMin()`

swap

```
| h | d | f | c |
```

remove

```
| h | d | f |
```

sink

```
| d | h | f |
```