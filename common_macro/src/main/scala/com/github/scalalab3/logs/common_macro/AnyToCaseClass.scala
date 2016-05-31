package com.github.scalalab3.logs.common_macro

import scala.reflect.macros.whitebox.Context


trait AnyToCC[CC] {
  def fromValue[A](value: A): Option[CC]
}

class AnyToCaseClass (val c: Context) {
  import c.universe._

  def getName(name: String, returnType: Type):Tree = ???

  def materializeMacro[T: c.WeakTypeTag, Ret]: c.Expr[AnyToCC[T]] = {
    val tpe = weakTypeOf[T]
    val ret = weakTypeOf[Ret]

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

    c.Expr[AnyToCC[T]] {
      q"""
      new AnyToCC[$tpe] {
        def fromValue(value: $ret): Option[$tpe] = {
          for (..$forLoop) yield $companion(..$names)
        }
      }
    """
    }

  }
}
