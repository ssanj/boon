package boon

import syntax._

object CustomClassesSuite extends SuiteLike("CustomClasses") {

  private val t1 = test("use default TC instances") {
    final case class Name(value: String)
    final case class Age(value: Int)
    final case class Person(name: Name, age: Age)

    val p1 = Person(Name("Royd Eris"), Age(30))
    val p2 = Person(Name("Royd Eris"), Age(30))
    val p3 = Person(Name("Melantha Jhirl"), Age(26))

    val a1 = Age(50)
    val a2 = Age(50)

    (p1 =?= p2 | "Person instances with same data are equal") &
    (p2 =/= p3 | "Person instances with different data are not equal") &
    (a1 =?= a2 | "The same ages are equal")
   }

  val tests = NonEmptySeq.nes(t1)
}