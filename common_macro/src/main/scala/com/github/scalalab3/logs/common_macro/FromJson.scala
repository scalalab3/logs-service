package com.github.scalalab3.logs.common_macro

import scala.reflect.macros.whitebox.Context


class FromJson (override val c: Context) extends AnyToCaseClass(c) {
  import c.universe._

  override def getName(name: String, returnType: Type):Tree = name match {
      case "id" => q"""Some((value \ "id").asOpt[java.util.UUID])"""
      case "level" => q"""(value \ "level").asOpt[String]"""
      case any => q"(value \ $name).asOpt[$returnType]"
  }
  override def materializeMacro[T: c.WeakTypeTag, A: c.WeakTypeTag]: c.Expr[AnyToCC[T, A]] = {
    super.materializeMacro[T, c.weakTypeTag[A].asInstanceOf[HM]]
  }
}
