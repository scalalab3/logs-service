package com.github.scalalab3.logs.common_macro

import scala.reflect.macros.whitebox.Context


class AnyToCaseClass[R] (val c: Context) {
  import c.universe._

  def fromValue[T](value: R): Option[T]

  def getName(name: String, returnType: Type):Tree = ???

  def materializeMacro[T: c.WeakTypeTag, Ret]: c.Expr[Ret] = {
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

      val ret = getName(decoded, returnType)
      fq"$name <- $ret"
    }

    c.Expr[Ret] {
      q"""
      new FromJson[$tpe] {
        def fromValue(value: R): Option[$tpe] = {
          for (..$forLoop) yield $companion(..$names)
        }
      }
    """
    }

  }
}
