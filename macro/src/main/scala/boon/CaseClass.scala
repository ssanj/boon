package boon

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import scala.util.{Try => STry}

trait CaseClassToMap[T] {
  def asMap(t: T): Map[String, String]
}

object CaseClassToMap {

  def apply[T: CaseClassToMap]: CaseClassToMap[T] = implicitly[CaseClassToMap[T]]

  //Adapted from:
  //http://blog.echo.sh/2013/11/04/exploring-scala-macros-map-to-case-class-conversion.html
  //https://stackoverflow.com/questions/20763434/scala-2-10-2-calling-a-macro-method-with-generic-type-not-work
  implicit def materializeCaseClassToMap[T]: CaseClassToMap[T] = macro materializerImpl[T]

  def materializerImpl[T: c.WeakTypeTag](c: Context): c.Tree = {
    import c.universe._
    val tpe = weakTypeOf[T]
    if (STry(tpe.typeSymbol.asClass.isCaseClass).getOrElse(false)) {
      val caseClassAccessors = tpe.decls.collectFirst{
        case m: MethodSymbol if m.isPrimaryConstructor => m
      }.get.paramLists.head

     val names = caseClassAccessors.map { accessor =>
        val accessorName = accessor.name
        val field = accessorName.decodedName.toString
        val t = TermName("t")// name of the parameter in the CaseClassToMap#asMap method
        val value = q"${t} ${accessorName.toTermName}"
        val returnType = tpe.decl(accessorName).typeSignature

        val strValue = q"_root_.boon.model.StringRep[$returnType].strRep($value)"

        (field, strValue)
      }

      val mapTree = q"_root_.scala.collection.immutable.Map[String, String](..$names)"
      q"""
        new CaseClassToMap[$tpe] {
          def asMap(t: $tpe) = $mapTree
        }
       """
    } else c.abort(c.enclosingPosition, s"case class required:$tpe")
  }
}