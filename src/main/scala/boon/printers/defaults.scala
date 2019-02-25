package boon.printers

abstract class PrinterSetting(
  val suitePassedToken: String,
  val suiteFailedToken: String,
  val testPassedToken: String,
  val testFailedToken: String,
  val assertionPassedToken: String,
  val assertionFailedToken: String,
  val testPadding: String,
  val assertionPadding: String,
  val assertionFailedPadding: String,
  val assertionFailedContextPadding: String,
  val assertionFailedContextElementPadding: String

) {
  def colourError(message: String): String
}

sealed trait ColourOutput
case object ShowColours extends ColourOutput
case object DontShowColours extends ColourOutput

object ColourOutput {
  def fromBoolean(showColours: Boolean): ColourOutput =
    if (showColours) ShowColours else DontShowColours
}