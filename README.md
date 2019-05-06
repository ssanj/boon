# boon [![Build Status](https://travis-ci.org/ssanj/boon.svg?branch=master)](https://travis-ci.org/ssanj/boon) [ ![Download](https://api.bintray.com/packages/ssanj/maven/boon/images/download.svg) ](https://bintray.com/ssanj/maven/boon/_latestVersion)

boon is a small framework for testing pure code. boon is:

1. Opinionated
1. Focuses on testing pure code
1. Has no external library dependencies
1. Fast
1. Strongly typed
1. Easy to run in the REPL

boon is inspired by [ScalaCheck](https://www.scalacheck.org) - which is a simple but very powerful Property-Based Testing framework.

Some things that are unique to boon:

1. Purity - test failures don't throw Exceptions
1. First Class Assertions - Assertions can be named and combined
1. Failure context - Failures can have write out a context of all useful information
1. Two failure modes - Assertions can either fail on the first error (**Seq**uential) or continue running other Assertions (**Ind**ependent)

## Usage in SBT ##

Add the following to your `build.sbt` file:

```scala
libraryDependencies += "net.ssanj" %% "boon" % "0.0.1-b36" % Test

testFrameworks += new TestFramework("boon.sbt.BoonFramework")

resolvers += Resolver.bintrayRepo("ssanj", "maven")
```

You can now run all the usual `sbt` test commands such as: `test` and `testOnly`.

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

With a Predicate, you can use the `>>` operator to override the failure message to something more descriptive:

```scala
List.empty[String].isEmpty >> oneOrMore("empty List is not empty", "I expected empty!") | "empty List is empty"
```

Results in:

```bash
[info]    - empty List is empty [✗]
[info]      => empty List is not empty
[info]         I expected empty!
```

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

## Customise Output

To customise the output of boon, you must implement the `boon.printers.BoonPrinter` trait:

```scala
trait BoonPrinter {
  def print(co: ColourOutput, out: String => Unit, so: SuiteOutput): Unit
}
```

Then supply your implementation (which must be an `object`) to boon via SBT with the `-P` flag and the full package path to the printer class:

```bash
testOnly *MyFirstSuite -- -P some.AwesomePrinter
```

where `some.AwesomePrinter` is:

```scala
package some

import boon.printers.BoonPrinter

object AwesomePrinter extends BoonPrinter {
  def print(co: ColourOutput, out: String => Unit, so: SuiteOutput): Unit = ...
}
```

See [SimplePrinter](https://github.com/ssanj/boon/blob/master/core/src/main/scala/boon/printers/SimplePrinter.scala) and [FlatPrinter](https://github.com/ssanj/boon/blob/master/core/src/main/scala/boon/printers/FlatPrinter.scala) for more details.

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

## Use Custom Types in Assertions

To use boon with your own custom types, you need three functions:

1. `(T, T) => Boolean` - defines how two values of a type `T` are equated This is similar to the Cats [Eq](https://typelevel.org/cats/typeclasses/eq.html) typeclass.
1. `T => String` - defines how an instance of type `T` is displayed. This is similar to the Cats [Show](https://typelevel.org/cats/typeclasses/show.html) typeclass
1. `(T, T) => NonEmptySeq[String]` -  defines how the differences between two instances of type `T` are displayed on failure

These three functions are bundled into the `BoonType` typeclass.

For instance, given a `Person` class:

```scala
final case class Name(value: String)
final case class Age(value: Int)
final case class Person(name: Name, age: Age)
```

you could use one of the helper functions on `BoonType` to generate a `default` instance:

```scala
implicit val personBoonType = BoonType.defaults[Person]
```

What `BoonType.defaults` does is to use scala's `==` for equality, `.toString` for display Strings and default difference of `t1 != t2`.

After defining the above you can make Assertions on Person instances:

```scala
val p1 = Person(Name("Royd Eris"), Age(30))
val p2 = Person(Name("Royd Eris"), Age(30))
val p3 = Person(Name("Melantha Jhirl"), Age(26))

p1 =?= p2 | "Person instances with same data are equal" and
p2 =/= p3 | "Person instances with different data are not equal" and
```

If we change `p1 =?= p3` we get:

```bash
[info]    - Person instances with same data are equal [✗]
[info]      => Person(Name(Royd Eris),Age(30)) != Person(Name(Melantha Jhirl),Age(26))
```

If you don't want to use default instances, you can use one of the many methods on [BoonType](https://github.com/ssanj/boon/blob/master/core/src/main/scala/boon/BoonType.scala) to create `BoonType` instances.

## Glossary ##

### Operators ###

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| =?=  | Typesafe Predicate for equality  | 1 + 2 =?= 3 |
| =/=  | Typesafe Predicate for inequality  | 1 + 2 =/= 4 |
| \\|   | Converts a Predicate to an Assertion | 1 + 2 =?= 3 \\| "addition" |
| %@  | Multiple Assertions on a single value | %@(List(1,2,3)){ l => <br> &nbsp;&nbsp;l.length =?= 5 \\| "length" and <br>&nbsp;&nbsp;l.contains(2) &nbsp;&nbsp;&nbsp;\\| "has 2" <br>} |

### Methods ###

| Method  | What it's for | Example |
| ------------- | ------------- | ------------- |
| and  | Combine Assertions | 1 + 2 =?= 3 \\| "1+2" and <br>4 + 5 =?= 9 \\| "4+5" |
| fail | Fail an Assertion | fail("reason") \| "assertion name" |
| pass | Pass an Assertion | pass \| "assertion name" |
| test | create a Test | <code>test(name) {<br>&nbsp;&nbsp;one or more assertions<br>}</code> |

---

### More Operators ###

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| \\|   | Also adds a context to an Assertion. *The context is displayed when an assertion fails* | x * y =?= 3 \\|("multiplication", "x" -> x.toString, "y" -> y.toString)  |
| >> | Provides custom errors on failure | 1 =?= 2 >> oneOrMore("error1","error2") |


### More Methods ###

| Method  | What it's for | Example |
| ------------- | ------------- | ------------- |
| xtest | ignore a test | <code>xtest(name) {<br>&nbsp;&nbsp;one or more assertions<br>}</code> |
| truthTable | truth table for a tabulated test | <code>val multTable = truthTable(<br>&nbsp;&nbsp;(1, 4)&nbsp;&nbsp;&nbsp;-> tval(4),<br>&nbsp;&nbsp;(2, 6)&nbsp;&nbsp;&nbsp;-> tval(12),<br>&nbsp;&nbsp;(5, 10)&nbsp;&nbsp;-> tval(50),<br>&nbsp;&nbsp;(7, 7)&nbsp;&nbsp;&nbsp;-> tval(49),<br>&nbsp;&nbsp;(-2, -1) -> tval(2),<br>&nbsp;&nbsp;(10, 20) -> tval(200)<br>)</code> |
| tval | truth table value | <code>(2, 6) -> tval(12)</code> |
| table | tabulated test | <br>table[(Int, Int), Int]("Multiplication", multTable)(n => n._1 * n._2)</code> |
| oneOrMore | create a NonEmptySeq | <code>override val tests = oneOrMore(test1, test2)</code> |
| seq() | run Assertions sequentially | <code>1 =?= 1 \\| "onsies" and<br>&nbsp;2 =?= 2 \\| "twosies" seq() </code> |
| ind() | run Assertions independently. This is the default | <code>1 =?= 1 \\| "onsies" and<br>&nbsp;2 =?= 2 \\| "twosies" ind() </code> |
---

### Syntax Extensions ###

Syntax extensions are more for ease-of-use than something that must be used. It's pretty easy to write these extensions yourself. The following extensions have been created to save you even more time.

#### Exceptions ####

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| =!=  | Compares an Exception thrown by class and message | <code>flakey =!=[RuntimeException](_ =?= "Boom!" \\| "expected Boom!")</code> |
|   | Also compares an Exception by class and message | <code>ex =!=[RuntimeException](_ =?= "Boom!" \\| "expected Boom!")</code> |

#### Regular Expressions ####

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| =^= | Compares a String to a regular expression | "Path error: /some/path" =^= "Path error:.*".r |

#### Option ####

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| some_? | expects a Some(value) | <code>some_?\[Int\](20.some)(_ =/= 10  \\| "Some(20) is not ten")</code>  |
| none_? | expects a None | <code>none_?\[Int\](noneValue)(pass \\| "expected None")</code> |

#### Either ####

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| left_? | expects Left value | <code>left_?\[String, Int\](leftValue)(_.endsWith("or1") \\| "ends with or1")</code> |
| right_? | expects a Right value | <code>right_?\[String, Int\](rightValue)(_ =/= 10 \\| "right is not ten")</code> |

#### null ####

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| null_? | expects a null value | <code>null_?(nullValue)(pass \\| "null value")</code> |
| null_! | expects a non-null value | <code>null_!(notNullString)(_  =?= "notNull" \\| "not null String" )</code> |

---


## Publishing

To publish a new version perform the following tasks:

```
publish
bintrayRelease
```