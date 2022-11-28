# Asymptotic

(a)
$O(1) ⊂ O(logn) ⊂ O(n) ⊂ O(nlogn) ⊂ O(n^2logn) ⊂ O(n^3) ⊂ O(2^n) ⊂ O(n!) ⊂ O(n^n)$

(b)
| --- | --- | --- | --- |
| $f(n) = 20501$ | $g(n) = 1$ | $f(n) ∈ O(g(n))$ | False, should be $f(n) ∈ Θ(g(n))$ |
| $f(n) = n^2 + n$ |  $g(n) = 0.000001n^3$ | $f(n) ∈ Ω(g(n))$ | False,  $f(n) ∈ O(g(n))$ |
| $f(n) = 2^{2n} + 1000$ | $g(n) = 4^n + n^100 =$ | $f(n) ∈ O(g(n))$ | False, $f(n) ∈ Θ(g(n))$ |
| $f(n) = log(n^100)$ | $g(n) = nlogn $ | $f(n) ∈ Θ(g(n))$ | False, $f(n) ∈ O(g(n))$|
| $f(n) = nlogn + 3^n + n$  | $g(n) = n^2 + n + logn$ | $f(n) ∈ Ω(g(n))$ | True |
| $f(n) = nlogn + n^2$ | $g(n) = logn + n^2$ | $f(n) ∈ Θ(g(n))$ | True |
| $f(n) = nlogn$ | $g(n) = (logn)^2$ | $f(n) ∈ O(g(n))$ | False, $f(n) ∈ Ω(g(n))$|

