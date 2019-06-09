package boon

object CustomClassesSuite extends SuiteLike("CustomClasses") {

  private val t1 = test("default TC instance for Product types") {

    final case class Name(value: String)
    final case class Age(value: Int)
    final case class Person(name: Name, age: Age)

    implicit val ageBoonType    = BoonType.defaults[Age]
    implicit val nameBoonType   = BoonType.defaults[Name]
    implicit val personBoonType = BoonType.caseClass[Person]


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

 private val t2 = test("default TC instance for Sum type") {
   sealed trait Colour
   case object Red   extends Colour
   case object Green extends Colour
   case object Blue  extends Colour

   implicit val colourBoonType: BoonType[Colour] = BoonType.defaults[Colour]

   val red: Colour   = Red
   val green: Colour = Green
   val blue: Colour  = Blue

   red =?= red     | "red is red"        and
   red =/= green   | "red is not green"  and
   red =/= blue    | "red is not blue"   and
   green =/= red   | "green is not red"  and
   green =/= blue  | "green is not blue" and
   green =?= green | "green is green"    and
   blue =/= red    | "blue is not red"   and
   blue =/= green  | "blue is not green" and
   blue =?= blue   | "blue is blue"
 }

  override val tests = oneOrMore(t1, t2)
}