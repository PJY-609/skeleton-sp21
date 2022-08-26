# List 4

__Lists vs. Arrays__ Our `DLList` has a drawback. Getting the i’th item is slow; we have to scan through each item in the list, starting from the beginning or the end, until we reach the i’th item. For an array named `A`, however, we can quickly access the i’th item using bracket notation, `A[i]`. Thus, our goal is to implement a list with an array.

__`AList`__ The `AList` will have the same API as our `DLList`, meaning it will have the same methods as `DLList` (`addLast()`, `getLast()`, `removeLast()`, and `get(int i)`). The AList will also have a size variable that tracks its size.

__Array Resizing__ When the array gets too full, we can resize the array. The solution is, instead, to create a new array of a larger size, then copy our old array values to the new array.

__Resizing Speed__ In the lecture video, we started off resizing the array by one more each time we hit our array size limit. This turns out to be extremely slow, because copying the array over to the new array means we have to perform the copy operation for each item. The worst part is, since we only resized by one extra box, if we choose to add another item, we have to do this again each time we add to the array.

__Improving Resize Performance__ Instead of adding by an extra box, we can instead create a new array with `size * FACTOR` items, where `FACTOR` could be any number, like 2 for example. We will discuss why this is fast later in the course.

__Downsizing Array Size__ What happens if we have a 1 million length array, but we remove 990,000 elements of the array? Well, similarly, we can downsize our array by creating an array of half the size, if we reach 250,000 elements, for example. Again, we will discuss this more rigorously later in the course.