### Operators ###

| Operator  | What it's for | Example |
| ------------- | ------------- | ------------- |
| =?=  | Typesafe Predicate for equality  | 1 + 2 =?= 3 |
| =/=  | Typesafe Predicate for inequality  | 1 + 2 =/= 4 |
| \\|   | Converts a Predicate to an Assertion | 1 + 2 =?= 3 \\| "addition" |
| %@  | Multiple Assertions on a single value | %@(List(1,2,3)){ l => <br> &nbsp;&nbsp;l.length =?= 5 \\| "length" and <br>&nbsp;&nbsp;l.contains(2) &nbsp;&nbsp;&nbsp;\\| "has 2" <br>} |