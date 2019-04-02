# boon [![Build Status](https://travis-ci.org/ssanj/boon.svg?branch=master)](https://travis-ci.org/ssanj/boon)

boon is a small framework for testing pure code. Boon has the following goals:

1. Simple and limited API
1. A focus on testing pure code
1. Fast
1. No external library dependencies
1. Limited test hierarchy

boon was inspired by [ScalaCheck](https://www.scalacheck.org). What boon does differently is to stay out of your way and add a minimal footprint to your test code.

## Usage in SBT ##

Add the following to your `build.sbt` file:

```scala
libraryDependencies += "net.ssanj" %% "boon" % "0.0.1-b30" % Test

testFrameworks += new TestFramework("boon.sbt.BoonFramework")

resolvers += Resolver.bintrayRepo("ssanj", "maven")
```

You can now run all the usual `sbt` test commands such as: `test` and `testOnly`.

## API ##

At the heart of boon, is an **Assertion**. One or more **Assertions** are combined into a **Test** and multiple **Tests** are combined into a **Suite**.

Here is an example of a simple **Suite**:

```scala
package blah

import boon._
import syntax._

object FirstSuite extends SuiteLike("FirstSuite") {

  private val t1 = test("String methods") {
    "Hello" + " " + "World" =?= "Hello World"    | "concat"     and
    "yohoho"                =?= "ohohoy".reverse | "reversing"
  }

  override def tests = NonEmptySeq.nes(t1)
}
```

To use boon, you need the following imports:

```scala
import boon._
import syntax._
```

All boon **Suites** must be `object`s and extend the `SuiteLike` class and supply it with a suite name. In the above example the suite name is "FirstSuite". You can call your **Suite** whatever you like.

Tests are defined through the `test` function and require a name, which in the above example is "String Methods" and one or more **Assertions**.

An **Assertion** takes the following format:

```scala
actualValue =?= expectedValue | "assertion name"
```

The `=?=` operator is a typesafe equals operator and the `|` operator simply adds a name to the preceding assertion. All **Assertions** must have names. The `and` operator is used to combine assertions.

Defined tests such as t1 in the above example, must be added a `NonEmptySeq` and used as part of the definition for the `tests` abstract method from the `SuiteLike` trait to be included in the **Suite**.

Running the above **Suite** produces the following output:

![boon-output](images/boon-first-suite.png)

We can provoke an assertion failure by changing our *concat* **Assertion**:

```scala
"Hello" + " " + "World" =?= "Hello Globe"    | "concat"
```

which then produces the following output:

![failure-output](images/boon-first-failure.png)


### Operators ###

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| =?=  | Typesafe predicate for equality  | 1 + 2 =?= 3 |
| =/=  | Typesafe predicate for inequality  | 1 + 2 =/= 4 |
| \\|   | Convert a predicate to an assertion | 1 + 2 =?= 3 \\| "addition" |
| \\|#   | Convert a predicate to an assertion with a context. *The context is displayed when an assertion fails* | x * y =?= 3 \\|#("multiplication", "x" -> x.toString, "y" -> y.toString)  |
| =!=  | Compare Exception thrown by class and message | flakey =!=[RuntimeException](_ =?= "Boom!") |
| and  | Combine Assertions | 1 + 2 =?= 3 \\| "1+2" and <br>4 + 5 =?= 9 \\| "4+5" |
| %@  | Multiple assertions on a single value | %@(List(1,2,3)){ l => <br> &nbsp;&nbsp;l.length =?= 5 \\| "length" and <br>&nbsp;&nbsp;l.contains(2) &nbsp;&nbsp;&nbsp;\\| "has 2" <br>} |

### Methods ###

| Method  | What it's for | Example |
| ------------- | ------------- | ------------- |
| fail | Fail an assertion | fail("reason") \| "assertion name" |
| pass | Pass an assertion | pass \| "assertion name" |

## Publishing

To publish a new version perform the following tasks:

```
publish
bintrayRelease
```