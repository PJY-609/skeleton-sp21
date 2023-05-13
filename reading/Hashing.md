# Hashing

## Properties of HashCodes
Hash codes have three necessary properties, which means a hash code must have these properties in order to be valid:

- It must be an Integer
- If we run `.hashCode()` on an object twice, it should return the same number
Two objects that are considered `.equal()` must have the same hash code.

Not all hash codes are created equal, however. If you want your hash code to be considered a good hash code, it should:
- Distribute items evenly


Note that at this point, we know how to add arbitrary objects to our data structure, not only strings.

## Handling Collisions
Time to address the elephant in the room. The big idea is to change our array ever so slightly to not contain just items, but instead contain a LinkedList (or any other List) of items. So...

Everything in the array is originally empty.
If we get a new item, and its hashcode is $h$:

- If there is nothing at index $h$ at the moment, we'll create a new `LinkedList` for index $h$, place it there, and then add the new item to the newly created `LinkedList`.
- If there is already something at index $h$, then there is already a `LinkedList` there. We simply add our new item to that `LinkedList`. Note: Our data structure is not allowed to have any duplicate items / keys. Therefore, we must first check whether the item we are trying to insert is already in this `LinkedList`. If it is, we do nothing! This also means that we will insert to the END of the linked list, since we need to check all of the elements anyways.

## Concrete workflow
- `add` item
	- Get hashcode (i.e., index) of item.
	- If index has no item, create new `List`, and place item there.
	- If index has a `List` already, check the `List` to see if item is already in there. If not, add item to `List`.
- `contains` item
	- Get hashcode (i.e., index) of item.
	- If index is empty, return false.
	- Otherwise, check all items in the `List` at that index, and if the item exists, return true.

## Dynamically growing the hash table
Suppose we have M buckets (indices) and N items. We say that our load factor is N/M.

(Note that the load factor is equivalent to our best case runtime from above.)

So... we have incentive to keep our load factor low (after all, it is the best runtime we could possible achieve!).

And note that if we keep M (the number of buckets) fixed, and N keeps increasing, the load factor consistently keeps increasing.

Strategy? Every so often, just double M. The way we do this is as follows:
- Create a new `HashTable` with 2M buckets.
- Iterate through all the items in the old HashTable, and one by one, add them into this new `HashTable`.

We need to add elements one by one again because since the size of the array changes, the modulus also changes, therefore the item probably belongs in a different bucket in the new hashtable than in the old one.

We do this by setting a load factor threshold. As soon as the load factor becomes bigger than this threshold, we resize.

Note that resizing the hash table also helps with shuffling the items in the hashtable!


At this point, __assuming items are evenly distributed__, all the lists will be approximately N/M items long, resulting in Θ(N/M) runtime. Remember that N/M is only allowed to be under a constant load factor threshold, and so, Θ(N/M)=Θ(1).

Items will distribute evenly if we have good hash codes (i.e. hashcodes which give fairly random values for different items.) Doing this in general is.. well... hard.