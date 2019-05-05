package boon

import boon.result.SuiteOutput

trait BoonPrinter {

  def print(so: SuiteOutput): Unit
}