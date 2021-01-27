package example

import boon._
import boon.syntax.regex._

object StringSuite extends SuiteLike("StringSuite") {
  private val t1 = test("String methods") {
    ("Daniel" + " " + "Jackson") =?= "Daniel Jackson" | "concat"     and
    "yohoho"                     =?= "ohohoy".reverse | "reversing"  and
    "hello".toUpperCase          =?= "HELLO"          | "UPPER"      and
    "DollHouse".startsWith("Doll")                    | "startsWith" and
    "Battlestar".endsWith("star")                     | "endsWith"   and
    "Bilbo".contains("bobo") || "contains" |>
      oneOrMore(
       "subject"   -> """"Bilbo"""",
       "predicate" -> "contains",
       "value"     -> """"ob""""
      ) and
    """HTTP/1.1 400: {"message":"body cannot be converted to Test: CNil: El(DownField(myValue)"}""" =^=
         """HTTP/1\.1 400.*DownField\(myValue\)""".r | "expect decode error"
  }

  private val strTable =
    truthTable(
      ("thequickbrownfoxjumpedoverthelazydog" -> tval(36)),
      (""                                     -> tval(0)),
      ("Hello World"                          -> tval(11))
    )

  private val t2 = table[String, Int]("String length", strTable)(_.length)

  override def tests = oneOrMore(t1, t2)
}