## API

### Predicate

At the heart of boon, is a **Predicate**. A Predicate is a boolean expression. The most basic way of creating a Predicate in boon, is by testing two values for equality:

```scala
actualValue =?= expectedValue
```

_The `=?=` operator is a typesafe equality operator. See [Operators](#operators) for additional operators_.

For example to test two Int operands for equality:

```scala
1 =?= 1
```

### Assertion

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

### Test

**Assertions** are grouped into a **Test**:

```scala
test("equality of things") {
  1 =?= 1             | "Int equality"    and
  "Hello" =?= "Hello" | "String equality" and
  true =?= true       | "Boolean equality"
}
```

### Suite

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

![boon-output](images/boon-my-first-suite.png)

Let's update our String equality Predicate so it fails:

```scala
"Hello" =?= "Yello" | "String equality"
```

Now when we run the Suite it produces the following output:

![failure-output](images/boon-my-first-suite-failure.png)

### Booleans are Predicates

Any `Boolean` expression can be turned into a Predicate. That Predicate can then be made an Assertion:

```scala
List.empty[String].isEmpty | "empty List is empty"
```

The main difference is that if the Assertion fails you get a Boolean failure not a type-specific failure:

```bash
[info]    - empty List is empty [✗]
[info]      => false != true
```

With a Predicate, you can use the `>>` operator to override (or *Replace*) the failure message to something more descriptive:

```scala
!List.empty[String].isEmpty >> (oneOrMore("empty List is not empty", "I expected empty!"), Replace) | "empty List is empty"
```

Results in:

```bash
   - empty List is empty [✗]
     => empty List is not empty
        I expected empty!
```

You can also add (or *Append*) some additional messages to the failed output:

```scala
!List.empty[String].isEmpty >> (oneOrMore("empty List is not empty", "I expected empty!"), Append) | "empty List is empty"
```

Results in:

```bash
   - empty List is empty [✗]
     => false != true
        empty List is not empty
        I expected empty!
```

Notice the *false != true* error.

### Block Assertions

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

### Contextual Errors

Sometimes when a test fails you want more information about the values of certain variables used to calculate the result. You can specify these values when creating an Assertion:

```scala
"Bilbo".contains("lbo") | (
  "contains",
  "subject"   -> """"Bilbo"""",
  "predicate" -> "contains",
  "value"     -> """"ob"""")
```

When the above Assertion fails, the contextual values supplied will be displayed:

```
[info]      => false != true
[info]      at .../StringSuite.scala:13
[info]        #: subject -> "Bilbo"
[info]           predicate -> contains
[info]           value -> "ob"
```

### Independent Assertions

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

Independent Assertions are shown with a '-' in the output.

Although unnecessary, you could explicitly define a Test as Independent with the `ind()` method:

```scala
val t1 = test("equality of things") {
  1 =?= 2             | "Int equality"    and
  "Hello" =?= "Hello" | "String equality" and
  true =?= true       | "Boolean equality" ind()
}
```

### Sequential Assertions

What if we didn't want to run any of the other Assertions after a failing Assertion? We could specify that by using the `seq()` method:

```scala
val t1 = test("equality of things") {
  1 =?= 2             | "Int equality"    and
  "Hello" =?= "Hello" | "String equality" and
  true =?= true       | "Boolean equality" seq()
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

Sequential Assertions are shown with a '↓' symbol in the output.

### Tabulated Tests

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