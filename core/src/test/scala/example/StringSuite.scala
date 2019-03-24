package example

import boon._
import syntax._

object StringSuite extends SuiteLike("StringSuite") {

  private val t1 = test("String methods") {
    ("Daniel" + " " + "Jackson") =?= "Daniel Jackson" | "concat"     and
    "yohoho"                     =?= "ohohoy".reverse | "reversing"  and
    "hello".toUpperCase          =?= "HELLO"          | "UPPER"      and
    "DollHouse".startsWith("Doll")                    | "startsWith" and
    "Battlestar".endsWith("star")                     | "endsWith"   and
    "Bilbo".contains("lbo") |# (
      "contains",
      "subject"   -> """"Bilbo"""",
      "predicate" -> "contains",
      "value"     -> """"ob"""")
  }

  private val strTable =
    NonEmptyMap.values(
      ("thequickbrownfoxjumpedoverthelazydog" -> tval(36)),
      (""                                     -> tval(0)),
      ("Hello World"                          -> tval(11))
    )

  private val t2 = table[String, Int]("String length", strTable)(_.length)

  override def tests = NonEmptySeq.nes(t1, t2)
}