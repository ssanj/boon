package boon

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
}