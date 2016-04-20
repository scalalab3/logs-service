import scala.language.experimental.macros
import scala.reflect.macros.whitebox

// change to j.u.HM
trait FromMap[T] {
  def fromMap(map: Map[String, Any]): T
}

object FromMap {
  implicit def materializeMappable[T]: FromMap[T] = macro materializeMappableImpl[T]

  def materializeMappableImpl[T: c.WeakTypeTag](c: whitebox.Context):
      c.Expr[FromMap[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val companion = tpe.typeSymbol.companion

    val fields = tpe.decls.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor ⇒ m
    }.get.paramLists.head

    val (_, fromMapParams) = fields.map { field ⇒
      val name = field.name.toTermName
      val decoded = name.decodedName.toString
      val returnType = tpe.decl(name).typeSignature

      (q"$decoded → t.$name", q"map($decoded).asInstanceOf[$returnType]")
    }.unzip

    c.Expr[FromMap[T]] {
      q"""
      new Mappable[$tpe] {
        def fromMap(map: Map[String, Any]): $tpe = $companion(..$fromMapParams)
      }
    """
    }
  }
}
