# Software Engineerning

## Complexity

There are two approaches to managing complexity:
- Making code **simpler** and **more obvious**
- **Encapsulation** into modules.

“If a software system is hard to **understand and modify**, then it is complicated. If it is easy to understand and modify, then it is simple”.

A system may have a few pieces that are highly complex, but if nobody ever looks at that code, then the overall impact is minimal.
Ousterhout’s book gives a crude mathematical formulation:

C = sum(c_p * t_p) for each part p.
- c_p is the complexity of part p.
- t_p is the time spent working on part p.

Every software system starts out beautiful, pure, and clean.
As they are built upon, they slowly twist into uglier and uglier shapes. This is almost inevitable in real systems.
Ousterhout recommends a **zero tolerance**  philosophy.

Tactical vs. Strategic Programming

Seeking Obvious Code through **Decomposition**

There are two primary sources of complexity:
**Dependencies**: When a piece of code cannot be read, understood, and modified independently.
**Obscurity**: When important information is not obvious.

Some suggestions as you embark on BYOW:
- Build classes that provide functionality needed in many places in your code.
- Create “deep modules”, e.g. classes with simple interfaces that do complicated things.
- Avoid over-reliance on “temporal decomposition” where your decomposition is driven primarily by the order in which things occur.
  - It’s OK to use some temporal decomposition, but try to fix any information leakage that occurs!
- Be strategic, not tactical.
- Most importantly: Hide information from yourself when unneeded!



## Teamwork

In the famous “Evidence for a Collective Intelligence Factor in the Performance of Human Groups”, Woolley et. al investigated the success of teams of humans on various tasks.

Studying individual group members, Woolley et. al found that:
- Collective intelligence is not significantly correlated with average or max intelligence of each group.
Instead, collective intelligence was correlated with three things:
- Average __social sensitivity__ of group members as measured using the “Reading the Mind in the Eyes Test” (this is really interesting).
- How equally distributed the group was in __conversational turn-taking__, e.g. groups where one person dominated did poorly.
- Percentage of __females__ in the group (paper suggests this is due to correlation with greater social sensitivity).

__Reflexivity__: a group's ability to collectively reflect upon team objectives, strategies, and processes and to
adapt them accordingly 

One important strategy is cultivating a team collaborative environment in which giving and receiving feedback on an ongoing basis is seen as a mechan ism for reflection and learning.

## Programming

Unlike other engineering disciplines, software is effectively unconstrained by the laws of physics.
Programming is an act of almost pure creativity!
