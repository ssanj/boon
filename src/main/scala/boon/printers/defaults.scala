package boon.printers

import Colourise.colourise
import Colourise.red
import Colourise.green
import Colourise.yellow
import Colourise.redU

sealed trait ColourOutput
case object ShowColours extends ColourOutput
case object DontShowColours extends ColourOutput

object ColourOutput {
  def fromBoolean(showColours: Boolean): ColourOutput =
    if (showColours) ShowColours else DontShowColours
}

final case class Tokens(passed: String, failed: String)

final case class SuitePrinterSettings(tokens: Tokens)

final case class TestPrinterSettings(tokens: Tokens, padding: String, colour: String => String)

final case class AssertionPrinterSettings(
  tokens: Tokens,
  padding: String,
  failedPadding: String,
  failedContextPadding: String,
  failedContextElementPadding: String
)

final case class PrinterSetting(
  suite: SuitePrinterSettings,
  test: TestPrinterSettings,
  assertion: AssertionPrinterSettings,
  colourError: String => String
)

object PrinterSetting {
  def defaults(showColours: ColourOutput): PrinterSetting = {
    val suite =
      SuitePrinterSettings(
        Tokens(colourise(green(showColours), "[passed]"),
               colourise(red(showColours), "[failed]"))
      )

    val test =
      TestPrinterSettings(
        tokens = Tokens(colourise(green(showColours), "[passed]"),
                        colourise(red(showColours), "[failed]")),
        padding = "",
        colour = colourise(yellow(showColours), _: String)
      )

    val assertion =
      AssertionPrinterSettings(
        tokens = Tokens(colourise(green(showColours), "[✓]"),
                        colourise(red(showColours), "[✗]")),
        padding = " " * 2,
        failedPadding = " " * 4,
        failedContextPadding = " " * 7,
        failedContextElementPadding = " " * 10
      )

    val colourError = colourise(redU(showColours), _: String)

    PrinterSetting(suite, test, assertion, colourError)
  }
}

