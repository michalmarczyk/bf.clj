# bf.clj

A [brainfuck](https://en.wikipedia.org/wiki/Brainfuck) interpreter
written in Clojure.

Inspired by
[this Clojure Google Group thread](https://groups.google.com/d/msg/clojure/6RI_MCeY6jY/7yvep-UiUfIJ).
The example program below is taken from this thread.

## Usage

To execute a string of brainfuck code, call `bf.clj/exec`. The
optional second argument sets the length of the data vector. Input
should be provided in the form of `long` literals. Print instructions
are handled by calling `char` on the `long` pointed at by the data
pointer and printing the result. `+` and `-` wrap around.

Alternatively, you may say `(bf.clj/parse txt)` to prepare the code
string `txt` for execution (by bundling it in a map together with a
jump table), `(bf.clj/initial-state-map len)` to prepare a state map
containing a data vector of length `len` initialized with zeros and
`(bf/eval prog state)` to run a preprocessed brainfuck program (as
returned by `parse`) starting in the given state.

After performing all side effects, `exec` and `eval` return the final
state map. *(NB. by default it contains a vector of length 30000!)*

No effort is made to handle errors in any useful fashion. In
particular, it is the responsibility of the user program to avoid
attempting to print values outside the `char` range. The remaining
Vars defined in the `bf.clj` namespace are public to facilitate
experimentation at the REPL.

```clj
(require '[bf.clj :as bf])

;; run program with data vector of specified length:
(bf/exec "++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++
          ..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>."
         10)

;; separate parsing / state creation / evaluation:
(bf/eval
 (bf/parse "++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++
            ..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.")
 (bf/initial-state-map 10))
```

## Licence

Copyright © 2013 Michał Marczyk

Distributed under the Eclipse Public License, the same as Clojure.
