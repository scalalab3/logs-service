package com.github.scalalab3.logs.common_macro

import scala.reflect.macros.whitebox.Context


class FromMap (override val c: Context) extends AnyToCaseClass(c) {
  import c.universe._

  override def getName(name: String, returnType: Type):Tree = {
    q"value.safeGet($name).map(_.asInstanceOf[$returnType])"
  }

  override def outType[A: c.WeakTypeTag] = weakTypeOf[HM]
}
