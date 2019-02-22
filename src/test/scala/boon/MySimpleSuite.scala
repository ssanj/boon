package boon

import syntax._

object MySimpleSuite extends SuiteLike("SimpleSuite") {

  val t1 = test("addition") {
    (2 + 1     =?= 1 + 2       | "commutative")    &
    (2 + 1 + 3 =?= 2 + (1 + 3) | "associative")    &
    (0 + 5     =?= 5           | "left identity")  &
    (5 + 0     =?= 5           | "right identity") &
    (3         =/= (3 + 2)     | "valid")
  }

  val t2 = test("strings") {
    (("Daniel" + " " + "Jackson") =?= "Daniel Jackson" | "concat")     &
    ("yohoho"                     =?= "ohohoy".reverse | "reversing")  &
    ("hello".toUpperCase          =?= "HELLO"          | "UPPER")      &
    ("DollHouse".startsWith("Doll")                    | "startsWith") &
    ("Battlestar".endsWith("star")                     | "endsWith")   &
    ("Frodo".contains("od")                            | "contains")   &
    ("Bilbo".contains("ob") |# (
      "!contains",
      "subject" -> "\"Bilbo\"",
      "predicate" -> "!contain",
      "value" -> "\"ob\""))
  }

  private def factorial(n: Int): Int =
    if (n <= 0) 1 else n * factorial(n - 1)

  val t3 = test("factorial") {
    (factorial(1)  =?= 1       | "of 1 is 1")        &
    (factorial(-1) =?= 1       | "of -1 is 1")       &
    (factorial(5)  =?= 120     | "of 5 is 120")      &
    (factorial(4)  =/= 120     | "of 4 is not 120")  &
    (factorial(10) =?= 3628800 | "of 10 is 3628800")
  }

  val t4 = test("takes a while") {
    (1 to 6000).flatMap(x => (1 to x).toList).length =?= 18003000 | "6k"
  }

  override def tests = NonEmptySeq.nes(t1, t2, t3, t4)
}