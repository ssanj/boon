## Use Custom Types in Assertions

To use boon with your own custom types, you need three functions:

1. `(T, T) => Boolean` - defines how two values of a type `T` are equated. This is modelled by the [Equality](https://github.com/ssanj/boon/blob/master/core/src/main/scala/boon/model/Equality.scala) typeclass. It is similar to the Cats [Eq](https://typelevel.org/cats/typeclasses/eq.html) typeclass.
1. `T => String` - defines how an instance of type `T` is displayed. This is modelled by the [StringRep](https://github.com/ssanj/boon/blob/master/core/src/main/scala/boon/model/StringRep.scala) typeclass. It is similar to the Cats [Show](https://typelevel.org/cats/typeclasses/show.html) typeclass
1. `(T, T) => NonEmptySeq[String]` -  defines how the differences between two instances of type `T` are displayed on failure. This is modelled by the [Difference](https://github.com/ssanj/boon/blob/master/core/src/main/scala/boon/model/Difference.scala) trait.

These three functions are bundled into the `BoonType` typeclass.

For instance, given a `Person` class:

```scala
final case class Name(value: String)
final case class Age(value: Int)
final case class Person(name: Name, age: Age)
```

you could use one of the helper functions on `BoonType` to generate a `default` instance:

```scala
implicit val personBoonType = BoonType.defaults[Person]
```

What `BoonType.defaults` does is to use scala's `==` for equality, `.toString` for display Strings and default difference of `t1 != t2`.

After defining the above you can make Assertions on Person instances:

```scala
val p1 = Person(Name("Royd Eris"), Age(30))
val p2 = Person(Name("Royd Eris"), Age(30))
val p3 = Person(Name("Melantha Jhirl"), Age(26))

p1 =?= p2 | "Person instances with same data are equal" and
p2 =/= p3 | "Person instances with different data are not equal" and
```

If we change `p1 =?= p3` we get:

```bash
[info]    - Person instances with same data are equal [✗]
[info]      => Person(Name(Royd Eris),Age(30)) != Person(Name(Melantha Jhirl),Age(26))
```

If you don't want to use default instances, you can use one of the many methods on [BoonType](https://github.com/ssanj/boon/blob/master/core/src/main/scala/boon/BoonType.scala) to create `BoonType` instances.
