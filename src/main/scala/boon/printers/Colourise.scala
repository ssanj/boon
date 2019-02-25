package boon
package printers

import scala.Console

object Colourise {

  sealed trait ConsoleColour
  case object Red extends ConsoleColour
  case object Green extends ConsoleColour
  case object RedUnderlined extends ConsoleColour

  def colourise(opColour: Option[ConsoleColour], message: String): String = {
    opColour.fold(message) { colour =>
      val chosenColour = colour match {
        case Red => Console.RED
        case Green => Console.GREEN
        case RedUnderlined => s"${Console.UNDERLINED}${Console.RED}"
      }

      s"${chosenColour}${message}${Console.RESET}"
    }
  }


  def colour(showColour: ColourOutput, colour: ConsoleColour): Option[ConsoleColour] =
    showColour match {
      case ShowColours => Option(colour)
      case DontShowColours => None
    }

  def green(showColour: ColourOutput): Option[ConsoleColour] =
    colour(showColour, Green)

  def red(showColour: ColourOutput): Option[ConsoleColour] =
    colour(showColour, Red)

  def redU(showColour: ColourOutput): Option[ConsoleColour] =
    colour(showColour, RedUnderlined)
}