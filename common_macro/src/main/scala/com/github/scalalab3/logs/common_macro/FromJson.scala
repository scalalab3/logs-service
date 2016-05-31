package com.github.scalalab3.logs.common_macro

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import play.api.libs.json._


class FromJson (override val c: Context) extends AnyToCaseClass(c) {
  import c.universe._

  override def getName(name: String, returnType: Type):Tree = name match {
      case "id" => q"""Some((value \ "id").asOpt[java.util.UUID])"""
      case "level" => q"""(value \ "level").asOpt[String]"""
      case any => q"(value \ $name).asOpt[$returnType]"
  }
}

object FromJson {
  implicit def macroI[T: AnyToCC]: AnyToCC[T] = macro FromJson.materializeMacro[T, FromJson]
}
