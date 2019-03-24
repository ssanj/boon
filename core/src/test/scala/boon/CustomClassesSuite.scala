package boon

import syntax._

object CustomClassesSuite extends SuiteLike("CustomClasses") {

  final case class Name(value: String)
  final case class Age(value: Int)
  final case class Person(name: Name, age: Age)

  private implicit val personBoonType = BoonType.defaults[Person]
  private implicit val ageBoonType    = BoonType.defaults[Age]
  private implicit val nameBoonType   = BoonType.defaults[Name]

  private val t1 = test("use default TC instances") {

    val p1 = Person(Name("Royd Eris"), Age(30))
    val p2 = Person(Name("Royd Eris"), Age(30))
    val p3 = Person(Name("Melantha Jhirl"), Age(26))

    val a1 = Age(50)
    val a2 = Age(50)

    val n1 = Name("Royd Eris")
    val n2 = Name("Royd Eris")

    p1 =?= p2 | "Person instances with same data are equal" and
    p2 =/= p3 | "Person instances with different data are not equal" and
    a1 =?= a2 | "The same ages are equal" and
    n1 =?= n2 | "The same names are equal"
   }

  override val tests = one(t1)
}