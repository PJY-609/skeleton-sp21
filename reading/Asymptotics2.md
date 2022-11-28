# Asymptotics II

__Important Sums.__ This is not a math class so we’ll be a bit sloppy, but the two key sums that you should know are that:
```
1 + 2 + 3 + … + N ∈ Θ(N2)
1 + 2 + 4 + 8 + … + N ∈ Θ(N)
```


Note: Big O is NOT the same as "worst case". But it is often used as such.

To summarize the usefulness of Big O:
- It allows us to make simple statements without case qualifications, in cases where the runtime is different for different inputs.
- Sometimes, for particularly tricky problems, we (the computer science community) don't know the exact runtime, so we may only state an upper bound.


| --- | --- |
| Big Theta $\Theta(f(N))$ | Order of growth is $f(N)$ |
| Big O $O(f(N))$ | Order of growth is less than or equal to $f(N)$ |
| Big Omega $\Omega(f(N))$ | Order of growth is greater than or equal to $f(N)$ |