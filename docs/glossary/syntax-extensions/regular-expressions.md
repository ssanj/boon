# Regular Expressions

Compares a String to a regular expression

## Imports:

```
import syntax.regex._
```

## Symbol

`=^=`

## Given:

```
val date = raw"(\d{4})-(\d{2})-(\d{2})".r
```

## Regex Match

Asserts whether a regular expression matches.

### Success

```
val a1 = "2019-06-25" =^= date | "date format"
```

When run results in:

```
  - date format [✓]
```

### Failure

```
val a2 = "2019/06/25" =^= date | "date format"
```

When run results in:

```
  - date format [✗]
    => '2019/06/25' did not match regex: /(\d{4})-(\d{2})-(\d{2})/
```

## Group Match

Asserts whether a regular expression matches and extracts groups for further Assertions.

We need the equality syntax for the follwing examples:

```
import syntax.equal._
```

### Success

```
val a1 = "2019-06-25" =^= (date, isSame("2019"), isSame("06"), isSame("25")) | "date format"
```

When run results in:

```
  - date format has length of 3 [✓]
  - date format(0)."2019" is same as "2019" [✓]
  - date format(1)."06" is same as "06" [✓]
  - date format(2)."25" is same as "25" [✓]
```

### Failure


```
val a2 = "2019-06-25" =^= (date, isSame("2019"), isSame("05")) | "date format"
```

When run results in:

```
  - date format has length of 2 [✗]
    => length of date format is different to assertions
       date format length: 3
       assertions length: 2
    at ...
      #: values -> (date format(0) -> "2019", date format(1) -> "06", date format(2) -> "25")
  - date format(0)."2019" is same as "2019" [✓]
  - date format(1)."05" is same as "06" [✗]
    => "05" != "06"
    at ...
      #: expected value at date format(1) -> "06"
         values -> (date format(0) -> "2019", date format(1) -> "06", date format(2) -> "25")
```
