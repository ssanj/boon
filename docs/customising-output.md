# Customise Output

To customise the output of boon, you must implement the `boon.printers.BoonPrinter` trait:

```scala
trait BoonPrinter {
  def print(co: ColourOutput, out: String => Unit, so: SuiteOutput): Unit
}
```

Then supply your implementation (which must be an `object`) to boon via SBT with the `-P` flag and the full package path to the printer class:

```bash
testOnly *MyFirstSuite -- -P some.AwesomePrinter
```

where `some.AwesomePrinter` is:

```scala
package some

import boon.printers.BoonPrinter

object AwesomePrinter extends BoonPrinter {
  def print(co: ColourOutput, out: String => Unit, so: SuiteOutput): Unit = ...
}
```

See [SimplePrinter](https://github.com/ssanj/boon/blob/master/core/src/main/scala/boon/printers/SimplePrinter.scala) and [FlatPrinter](https://github.com/ssanj/boon/blob/master/core/src/main/scala/boon/printers/FlatPrinter.scala) for more details.
