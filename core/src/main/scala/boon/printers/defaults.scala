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

final case class AssertionToken(common: Tokens, notRun: String)

final case class SuitePrinterSettings(tokens: Tokens)

final case class TestTokens(common: Tokens, ignored: String)

final case class TestPrinterSettings(tokens: TestTokens, padding: String, colour: String => String)

final case class AssertionPrinterSettings(
  tokens: AssertionToken,
  padding: String,
  compositePrefix: String,
  failedPadding: String,
  failedPaddingPrefix: String,
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
        tokens = TestTokens(
                    Tokens(colourise(green(showColours), "[passed]"),
                           colourise(red(showColours), "[failed]")),
                    colourise(green(showColours), "[ignored]")
                  ),
        padding = "",
        colour = colourise(yellow(showColours), _: String)
      )

    val assertion =
      AssertionPrinterSettings(
        tokens = AssertionToken(
                    Tokens(colourise(green(showColours), "[✓]"),
                           colourise(red(showColours), "[✗]")),
                    colourise(red(showColours), "(not run)")
                 ),
        padding = " " * 2,
        compositePrefix = "↓",
        failedPadding = " " * 4,
        failedPaddingPrefix = "=>",
        failedContextPadding = " " * 7,
        failedContextElementPadding = " " * 10
      )

    val colourError = colourise(redU(showColours), _: String)

    PrinterSetting(suite, test, assertion, colourError)
  }
}


