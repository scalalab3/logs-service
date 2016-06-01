package com.github.scalalab3.logs.common_macro

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import play.api.libs.json.JsValue


trait AnyToCC[CaseClass, SourceType] {
  def fromValue(value: SourceType): Option[CaseClass]
}

object AnyToCC {
  implicit def macroJ[T]: AnyToCC[T, JsValue] = macro FromJson.materializeMacro[T, JsValue]
  implicit def macroM[T]: AnyToCC[T, HM] = macro FromMap.materializeMacro[T, HM]
}

abstract class AnyToCaseClass (val c: Context) {
  import c.universe._

  def getName(name: String, returnType: Type):Tree = ???

  def materializeMacro[T: c.WeakTypeTag, A: c.WeakTypeTag]: c.Expr[AnyToCC[T, A]] = {
    val tpe = weakTypeOf[T]
    val a = if (weakTypeOf[A] <:< typeOf[java.util.HashMap[_, _]])
      weakTypeOf[java.util.HashMap[String, Any]]
    else
      weakTypeOf[A]

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

      val get = getName(decoded, returnType)
      fq"$name <- $get"
    }

    c.Expr[AnyToCC[T, A]] {
      q"""
       new AnyToCC[$tpe, $a] {
        def fromValue(value: $a): Option[$tpe] = {
          for (..$forLoop) yield $companion(..$names)
        }
      }
    """
    }
  }
}
