# More Operators

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| \\|   | Also adds a context to an Assertion. *The context is displayed when an assertion fails* | x * y =?= 3 \\|("multiplication", "x" -> x.toString, "y" -> y.toString)  |
  | \\|? | Customising Predicate output and equality  | <code>1 =?= 2 \\|? ("numbers", Difference.from[Int]((v1,v2) => oneOrMore(s"Invalid! $v1 is not $v2")), genEq, noContext)</code> |
| >> | Replace custom errors on failure | 1 =?= 2 >> (oneOrMore("error1","error2"), Replace) |
| >> | Append custom errors on failure | 1 =?= 2 >> (oneOrMore("error1","error2"), Append) |