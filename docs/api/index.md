# API

## Predicate

At the heart of boon, is a **Predicate**. A Predicate is a boolean expression. The most basic way of creating a Predicate in boon, is by testing two values for equality:

```scala
actualValue =?= expectedValue
```

_The `=?=` operator is a typesafe equality operator. See [Operators](/docs/glossary/operators.md) for additional operators_.

For example to test two Int operands for equality:

```scala
1 =?= 1
```

## Assertion

A Predicate with a description is an **Assertion**:

```scala
actualValue =?= expectedValue | "description"
```

For example:

```scala
1 =?= 1 | "one is one"
```

Assertions are first class constructs in boon and can be combined with the `and` method to give even more Assertions:

```scala
1 =?= 1             | "Int equality"    and
"Hello" =?= "Hello" | "String equality" and
true =?= true       | "Boolean equality"
```

## Test

**Assertions** are grouped into a **Test**:

```scala
test("equality of things") {
  1 =?= 1             | "Int equality"    and
  "Hello" =?= "Hello" | "String equality" and
  true =?= true       | "Boolean equality"
}
```

## Suite

**Tests** are grouped into a **Suite**. All Suites must follow these rules:
- Must be an `object` - This prevents inheritance abuse
- Extend `SuiteLike`
- Override the `tests` method

Here is an outline of a simple **Suite**:

```scala
object MySuite extends SuiteLike("Stuff I want to test") {

  val test1 =

  val test2 =
  ..
  val testn =

  override def tests = oneOrMore(test1, test2 ... testn)
}
```

A more complete example:

```scala
package example

import boon._

object MyFirstSuite extends SuiteLike("Simple Stuff") {

  private val t1 = test("equality of things") {
    1 =?= 1             | "Int equality"    and
    "Hello" =?= "Hello" | "String equality" and
    true =?= true       | "Boolean equality"
  }

  override def tests = oneOrMore(t1)
}
```

As shown above, to define a Suite, you need the following import:

```scala
import boon._
```

The `tests` method on `SuiteLike` is defined as:

```
def tests: NonEmptySeq[Test]
```

`NonEmptySeq` is a collection that must have at least one element. It is similar to `NonEmptyList` in [Cats](https://typelevel.org/cats/datatypes/nel.html) and [Scalaz](https://scalaz.github.io/scalaz/scalaz-2.10-7.0.4/doc/index.html#scalaz.NonEmptyList). It can be constructed using the `oneOrMore` function for when you have one or more tests. Requiring a `NonEmptySeq` when defining a Suite means that you can't create a Suite without any tests. This is something that is possible in other testing frameworks and leads to confusion when an empty Suite passes (since it has no tests). boon strives to ensure that invalid states can't be represented.

Running the above **Suite** produces the following output:

![boon-output](/images/boon-my-first-suite.png)

Let's update our String equality Predicate so it fails:

```scala
"Hello" =?= "Yello" | "String equality"
```

Now when we run the Suite it produces the following output:

![failure-output](/images/boon-my-first-suite-failure.png)

## Booleans are Predicates

Any `Boolean` expression can be turned into a Predicate. That Predicate can then be made an Assertion:

```scala
List.empty[String].isEmpty | "empty List is empty"
```

The main difference is that if the Assertion fails you get a Boolean failure not a type-specific failure:

```bash
[info]    - empty List is empty [✗]
[info]      => false != true
```

## Block Assertions

If you want to run multiple Assertions on an expression you can do so with the `%@` operator:

```scala
%@(List(1, 2, 3, 4, 5)) { list =>
  list.sum =?= 15            | "list sum" and
  list.take(2) =?= List(1,2) | "list take"
}
```

In addition each block can be given a prefix:

```scala
%@(List(1, 2, 3, 4, 5), "list") { list =>
  list.sum =?= 15            | "sum" and
  list.take(2) =?= List(1,2) | "take"
}
```

results in the prefix prepended to each Assertion description:

```
[info]    - list.sum [✓]
[info]    - list.take [✓]
```

_When nesting blocks with prefixes, lower blocks will have the prefix of each upper block prepended to their Assertions_.

## Contextual Errors

Sometimes when a test fails you want more information about the values of certain variables used to calculate the result. You can specify these values when creating an Assertion:

```scala
"Bilbo".contains("lbo") || "contains" |>
  oneOrMore(
   "subject"   -> """"Bilbo"""",
   "predicate" -> "contains",
   "value"     -> """"ob""""
  ) and
```

When the above Assertion fails, the contextual values supplied will be displayed:

```
[info]      => false != true
[info]      at .../StringSuite.scala:13
[info]        #: subject -> "Bilbo"
[info]           predicate -> contains
[info]           value -> "ob"
```

Notice the use of the double pipes (`||`) when adding a context (`|>`). The double pipes imply you have more information to supply in addition to a description.

## Continue-On-Failure Assertions

By default, all Assertions are executed independently of each other. What this means is that a prior failing Assertion, will not prevent a subsequent Assertion from running:

```
val t1 = test("equality of things") {
  1 =?= 2             | "Int equality"    and
  "Hello" =?= "Hello" | "String equality" and
  true =?= true       | "Boolean equality"
}
```

Results in:

```bash
[info]  - equality of things [failed]
[info]    - Int equality [✗]
[info]      => 1 != 2
[info]      at .../MyFirstSuite.scala:8
[info]    - String equality [✓]
[info]    - Boolean equality [✓]
```

Notice that although, the **Int equality** Assertion failed, the other Assertions completed successfully.

Continue-On-Failure Assertions are shown with a '-' in the output.

Although unnecessary, you could explicitly define a Test as Continue-On-Failure with the `continueOnFailure` method:

```scala
val t1 = test("equality of things") {
  continueOnFailure(
    1 =?= 2             | "Int equality"    and
    "Hello" =?= "Hello" | "String equality" and
    true =?= true       | "Boolean equality"
  )
}
```

## Stop-On-Failure Assertions

What if we didn't want to run any of the other Assertions after a failing Assertion? We could specify that by using the `stopOnFailuire` method:

```scala
val t1 = test("equality of things") {
  stopOnFailure(
    1 =?= 2             | "Int equality"    and
    "Hello" =?= "Hello" | "String equality" and
    true =?= true       | "Boolean equality"
  )
}
```

If we ran the above, it would fail with:

```bash
[info]  - equality of things [failed]
[info]    ↓ Int equality [✗]
[info]      => 1 != 2
[info]      at .../MyFirstSuite.scala:8
[info]    ↓ String equality (not run)
[info]    ↓ Boolean equality (not run)
```

Notice that the **String equality** and **Boolean equality** Assertions did not run after the **Int equality** Assertion failed.

Stop-On-Failure Assertions are shown with a '↓' symbol in the output.

## Tabulated Tests

If you have a truth table of inputs against some expected output, you can create a tabulated test. Start off by creating a `truthTable`:

```scala
  val multTable =
    truthTable(
      (1, 4)   -> tval(4),
      (2, 6)   -> tval(12),
      (5, 10)  -> tval(50),
      (7, 7)   -> tval(49),
      (-2, -1) -> tval(2),
      (10, 20) -> tval(200)
    )
```

`multTable` defines a truth table that takes two `Ints` and produces an expected result which is also an `Int` (`tval`).

You can then use the truth table within a `table` test:

```scala
val multTest = table[(Int, Int), Int]("Multiplication", multTable)(n => n._1 * n._2)
```

When we run the **multTest** we get:

```
- Multiplication [passed]
  - with (1, 4) is 4 [✓]
  - with (2, 6) is 12 [✓]
  - with (5, 10) is 50 [✓]
  - with (7, 7) is 49 [✓]
  - with (-2, -1) is 2 [✓]
  - with (10, 20) is 200 [✓]
```

## Collections of Assertions are an Assertion

Say you had a collection of values and you wanted to assert that they all held some kind of property. You could just map over those values with your Assertion and get one big Assertion that verifies it all.

```scala
val t1 = test("less than 10") {
  oneOrMore(1, (2 to 9):_*).map(n => n < 10 | s"$n < 10")
}
```

When we run the above Test we get:

```
 - less than 10 [passed]
   - 1 < 10 [✓]
   - 2 < 10 [✓]
   - 3 < 10 [✓]
   - 4 < 10 [✓]
   - 5 < 10 [✓]
   - 6 < 10 [✓]
   - 7 < 10 [✓]
   - 8 < 10 [✓]
   - 9 < 10 [✓]
```

*note*: This works with `NonEmptySeq` and any `scala.collection.Iterable`

As you can see the default way of combining Assertions is by the Continue-On-Failure model. The above is just a short-hand for:

```
val t1 = test("less than 10") {
  continueOnFailure(oneOrMore(1, (2 to 9):_*).map(n => n < 10 | s"$n < 10"))
}
```

Similarly we can use the Stop-On-Failure:

```
val t1 = test("less than 10") {
  stopOnFailure(oneOrMore(1, (2 to 9):_*).map(n => n < 10 | s"$n < 10"))
}
```

## Assertions on Differing Types

Sometimes you want to run an Assertion on a couple of different types.  You can't use the type safe equal operator (`=?=`) as it expects the same types.

For example to run an Assertion on a `String` and an `Int`, you just need to provide a `Assertion` function that compares the two values:

```
(valueOfType1, valueOfType2) =>= ((v1, v2) => Assertion)
```

For example to Assert that the length of "Hello World" is 11:

```
("Hello World", 11) =>= ((a, b) => a.length =?= b | "greet length")
```

When we run the above we get:

```
  - greet length [✓]
```

or when it fails we get:

```
  - greet length [✗]
    => 11 != 12
    at ...
      #: values -> ("Hello World", 12)
```
