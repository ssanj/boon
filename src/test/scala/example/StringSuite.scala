package example

import boon._
import syntax._

object StringSuite extends SuiteLike("StringSuite") {

  val t1 = test("String methods") {
    (("Daniel" + " " + "Jackson") =?= "Daniel Jackson" | "concat")     &
    ("yohoho"                     =?= "ohohoy".reverse | "reversing")  &
    ("hello".toUpperCase          =?= "HELLO"          | "UPPER")      &
    ("DollHouse".startsWith("Doll")                    | "startsWith") &
    ("Battlestar".endsWith("star")                     | "endsWith")   &
    ("Bilbo".contains("lbo") |# (
      "contains",
      "subject" -> "\"Bilbo\"",
      "predicate" -> "contains",
      "value" -> "\"ob\""))
  }

  val strTable =
    NonEmptyMap.values(
      "thequickbrownfoxjumpedoverthelazydog" -> 36,
      ""                                     -> 0,
      "Hello World"                          -> 11
    )

  val t2 = table[String, Int]("String length", strTable)(_.length)

  override def tests = NonEmptySeq.nes(t1, t2)
}