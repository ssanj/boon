package boon
package syntax

import nulls._

object NullSuite extends SuiteLike("NullSuite") {

  private val t1 = test("null values") {
    val nullValue = null
    null_?(null)(pass | "null literal") and
    null_?(nullValue)(pass | "null value")
  }

  private val t2 = test("not null values") {
    val notNullString  = "notNull"
    val notNullBoolean = true

    null_!(notNullString)(_  =?= "notNull" | "not null String" ) and
    null_!(notNullBoolean)(_ =?= true      | "not null Boolean")
  }

  override val tests = oneOrMore(t1, t2)
}