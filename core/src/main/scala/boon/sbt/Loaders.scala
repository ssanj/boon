package boon.sbt

import boon.printers.BoonPrinter
import boon.SuiteLike

import scala.reflect.runtime.universe._
import scala.util.Try

object Loaders {

  def loadSuite(name: String, loader: ClassLoader): Try[SuiteLike] = {
    Try(reflectivelyInstantiateSuite(name, loader)).collect {
      case ref: SuiteLike => ref
    }
  }

  def loadPrinter(name: String, loader: ClassLoader): Try[BoonPrinter] = {
    Try(reflectivelyInstantiateSuite(name, loader)).collect {
      case ref: BoonPrinter => ref
    }
  }

  def reflectivelyInstantiateSuite(className: String, loader: ClassLoader): Any = {
   val mirror = runtimeMirror(loader)
   val module = mirror.staticModule(className)
   mirror.reflectModule(module).instance.asInstanceOf[Any]
  }
}