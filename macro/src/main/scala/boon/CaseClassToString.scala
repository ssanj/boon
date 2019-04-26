package boon

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object CaseClassToString {

  def toStringz[T](obj: T): Map[String, Any]  = macro toStringzImpl

  def toStringzImpl(c: Context)(obj: c.Tree): c.Tree = {
    import c.universe._
    val names = obj.tpe.decls.collect {
      case m: MethodSymbol if m.isCaseAccessor =>
        c.info(c.enclosingPosition, s"${obj.tpe}:${m.returnType}", true)
        val field = q"${m.name.decodedName.toString}"
        val value = q"${obj} ${m.name}"
        (field, value)
    }

     q"_root_.scala.collection.immutable.Map[String, Any](..$names)"
  }

  // def toFlattenedString(c: Context)(obj: c.Tree): String = {
  //   import c.universe._
  //   val names = obj.tpe.decls.collect {
  //     case m: MethodSymbol if m.isCaseAccessor =>
  //       val field = q"${m.name.decodedName.toString}"
  //       val value = q"${obj} ${m.name}"

  //       val flattened  = s"${field}=${value}"

  //       STry(m.returnType.typeSymbol.asClass.isCaseClass).
  //         fold(_ => flattened,
  //              isCaseClass => if (isCaseClass) s"${acc},${toFlattenedString(c)(value)}" else flattened)
  //   }

  //   names.mkString(",")
  // }
}