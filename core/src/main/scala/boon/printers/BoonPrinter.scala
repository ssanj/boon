package boon
package printers

import boon.result.SuiteOutput

trait BoonPrinter {
  def print(co: ColourOutput, out: String => Unit, so: SuiteOutput): Unit
}