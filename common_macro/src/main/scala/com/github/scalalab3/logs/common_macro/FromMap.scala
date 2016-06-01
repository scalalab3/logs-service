package com.github.scalalab3.logs.common_macro

import scala.reflect.macros.whitebox.Context


class FromMap (override val c: Context) extends AnyToCaseClass(c) {
  import c.universe._

  override def getName(name: String, returnType: Type):Tree = name match {
    case "id" => q"""Some(Option(value.get("id")).map(_.asInstanceOf[java.util.UUID]))"""
    case any => q"Option(value.get($name)).map(_.asInstanceOf[$returnType])"
  }

  override def materializeMacro[T: c.WeakTypeTag, A: c.WeakTypeTag]: c.Expr[AnyToCC[T, HM]] = {
    super.materializeMacro[T, weakTypeOf[HM]]
  }
}
