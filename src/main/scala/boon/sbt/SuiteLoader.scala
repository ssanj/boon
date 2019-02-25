package boon.sbt

import boon.SuiteLike

import scala.reflect.runtime.universe._
import scala.util.Try

object SuiteLoader {

  def loadSuite(name: String, loader: ClassLoader): Try[SuiteLike] = {
    Try(reflectivelyInstantiateSuite(name, loader)).collect {
      case ref: SuiteLike => ref
    }
  }

  def reflectivelyInstantiateSuite(className: String, loader: ClassLoader): Any = {
   val mirror = runtimeMirror(loader)
   val module = mirror.staticModule(className)
   mirror.reflectModule(module).instance.asInstanceOf[Any]
  }
}