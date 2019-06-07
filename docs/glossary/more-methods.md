# More Methods

| Method  | What it's for | Example |
| ------------- | ------------- | ------------- |
| xtest | ignore a test | <code>xtest(name) {<br>&nbsp;&nbsp;one or more assertions<br>}</code> |
| truthTable | truth table for a tabulated test | <code>val multTable = truthTable(<br>&nbsp;&nbsp;(1, 4)&nbsp;&nbsp;&nbsp;-> tval(4),<br>&nbsp;&nbsp;(2, 6)&nbsp;&nbsp;&nbsp;-> tval(12),<br>&nbsp;&nbsp;(5, 10)&nbsp;&nbsp;-> tval(50),<br>&nbsp;&nbsp;(7, 7)&nbsp;&nbsp;&nbsp;-> tval(49),<br>&nbsp;&nbsp;(-2, -1) -> tval(2),<br>&nbsp;&nbsp;(10, 20) -> tval(200)<br>)</code> |
| tval | truth table value | <code>(2, 6) -> tval(12)</code> |
| table | tabulated test | <br>table[(Int, Int), Int]("Multiplication", multTable)(n => n._1 * n._2)</code> |
| oneOrMore | create a NonEmptySeq | <code>override val tests = oneOrMore(test1, test2)</code> |
| seq() | run Assertions sequentially | <code>1 =?= 1 \\| "onsies" and<br>&nbsp;2 =?= 2 \\| "twosies" seq() </code> |
| ind() | run Assertions independently. This is the default | <code>1 =?= 1 \\| "onsies" and<br>&nbsp;2 =?= 2 \\| "twosies" ind() </code> |
---