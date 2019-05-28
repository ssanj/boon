## Running in the REPL

Add the following import to run any Assertion, Test or Suite in a Scala Repl:

```scala
import boon._
```

### Assertions

To run any Assertions use the `REPL.runAssertions` method:

```scala
scala> val a1 = "hello" =?= "hello" | "string equality"
a1: boon.model.AssertionData = AssertionData(NonEmptySeq(Assertion(AssertionName(string equality),Defer(boon.Boon$$$Lambda$15567/1635819764@4ba58cfc),Map(),SourceLocation(None,None,17)),WrappedArray()))

scala> val a2 = List(1,2,3).reverse =?= List(3,2,1) | "list reverse"
a2: boon.model.AssertionData = AssertionData(NonEmptySeq(Assertion(AssertionName(list reverse),Defer(boon.Boon$$$Lambda$15567/1635819764@23e80574),Map(),SourceLocation(None,None,17)),WrappedArray()))

scala> REPL.runAssertions(a1, a2)
```

Which results in:

```bash
Pete Holmes [passed]
 - I refer to myself as ‘Old Petey Pants` [passed]
   - string equality [✓]
   - list reverse [✓]
```

_The suite and test names are randomly generated_.

### Tests

To run any Tests use the `REPL.runTests` method:


```scala
scala> val t1 = test("equality of things") {
     |     1 =?= 1             | "Int equality"    and
     |     "Hello" =?= "Hello" | "String equality" and
     |     true =?= true       | "Boolean equality"
     |   }
t1: boon.model.Test = SuccessfulTest(DeferredTest(TestName(equality of things),NonEmptySeq(Assertion(AssertionName(Int equality),Defer(boon.Boon$$$Lambda$15567/1635819764@30ed81d9),Map(),SourceLocation(None,None,18)),ArrayBuffer(Assertion(AssertionName(String equality),Defer(boon.Boon$$$Lambda$15567/1635819764@4bd3b162),Map(),SourceLocation(None,None,19)), Assertion(AssertionName(Boolean equality),Defer(boon.Boon$$$Lambda$15567/1635819764@415b2d75),Map(),SourceLocation(None,None,20)))),Independent))

scala> REPL.runTests(t1)
```

Which results in:

```bash
Tig Notaro [passed]
 - equality of things [passed]
   - Int equality [✓]
   - String equality [✓]
   - Boolean equality [✓]
```

_The suite names are randomly generated_.

### Suites

To run any Suites use the `REPL.runSuites` method:

```scala
scala> object ListSuite extends SuiteLike("ListSuite") {
     |
     |   private val t1 = test("match contents") {
     |     List(1, 2, 3, 4, 5) =/= List(2, 4, 6, 8, 10) | "list contents" and
     |     List(1, 2, 3, 4, 5).length =?= 5             | "list length" and
     |     //combining assertions for a single object
     |     %@(List(1, 2, 3, 4, 5)) { list =>
     |       list.sum =?= 15 | "list sum" and list.take(2) =?= List(1,2) | "list take"
     |     }
     |   }
     |
     |   override val tests = one(t1)
     | }
defined object ListSuite

scala> REPL.runSuites(ListSuite)
```

Which results in:

```bash
ListSuite [passed]
 - match contents [passed]
   - list contents [✓]
   - list length [✓]
   - list sum [✓]
   - list take [✓]
```

To use a custom printer in any repl method, just supply an instance of `ReplConfig` with your printer class:

```scala
scala> REPL.runAssertions(1 =?= 1 | "one")(ReplConfig(boon.printers.FlatPrinter))
```
