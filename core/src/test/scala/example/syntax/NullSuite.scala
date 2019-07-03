package example.syntax

import boon._
import syntax.nulls._

object NullSuite extends SuiteLike("Null Syntax Suite") {

  private val t1 = test("null values") {
    val nullValue: String = null
    null_?(null: String)(pass | "null literal") and
    null_?(nullValue)(pass | "null value")      and 
    isNull[String](nullValue)
  }

  private val t2 = test("not null values") {
    val notNullString  = "notNull"
    val notNullBoolean = true

    null_!(notNullString)(_  =?= "notNull" | "not null String" ) and
    null_!(notNullBoolean)(_ =?= true      | "not null Boolean") and
    isNotNull(notNullString)                                     and
    isNotNull(notNullBoolean)
  }

  override val tests = oneOrMore(t1, t2)
}