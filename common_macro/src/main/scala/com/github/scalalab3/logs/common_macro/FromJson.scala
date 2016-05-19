package com.github.scalalab3.logs.common_macro

import scala.language.experimental.macros
import scala.reflect.macros.whitebox
import play.api.libs.json._

trait FromJson[T] {
  def fromJson(json: JsValue): Option[T]
}

object FromJson {

  implicit def materializeMappable[T]: FromJson[T] = macro materializeMappableImpl[T]

  def materializeMappableImpl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[FromJson[T]] = {

    import c.universe._
    val tpe = weakTypeOf[T]

    // check if case class passed
    if (!(tpe.typeSymbol.isClass && tpe.typeSymbol.asClass.isCaseClass)) {
      c.abort(c.enclosingPosition, "Not a case class")
    }

    val companion = tpe.typeSymbol.companion

    val fields = tpe.decls.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor => m
    }.get.paramLists.head

    val names = fields.map { field =>
      q"${field.name.toTermName}"
    }

    val forLoop = fields.map { field =>
      val name = field.name.toTermName
      val decoded = name.decodedName.toString
      val returnType = tpe.decl(name).typeSignature


      val ret = decoded match {
        case "id" => q"""Some((json \ "id").asOpt[java.util.UUID])"""
        case "level" => q"""(json \ "level").asOpt[String]"""
        case any => q"(json \ $decoded).asOpt[$returnType]"
      }
      fq"$name <- $ret"
    }

    c.Expr[FromJson[T]] {
      q"""
      new FromJson[$tpe] {
        def fromJson(json: JsValue): Option[$tpe] = {
          for (..$forLoop) yield $companion(..$names)
        }
      }
    """
    }
  }
}
