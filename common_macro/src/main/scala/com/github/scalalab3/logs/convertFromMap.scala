package com.github.scalalab3.logs.common_macro

import java.util.HashMap
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

trait FromMap[T] {
  def fromMap(map: HashMap[String, Any]): T
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

    val fromMapParams = fields.map { field =>
      val name = field.name.toTermName
      val decoded = name.decodedName.toString
      val returnType = tpe.decl(name).typeSignature

      decoded match {
        case "id" => q"""(if (map.containsKey($decoded)) Some(map.get($decoded)) else None)
            .asInstanceOf[$returnType]"""
        case _ => q"map.get($decoded).asInstanceOf[$returnType]"
      }
    }

    c.Expr[FromMap[T]] {
      q"""
      new FromMap[$tpe] {
        def fromMap(map: java.util.HashMap[String, Any]): $tpe = $companion(..$fromMapParams)
      }
    """
    }
  }
}
