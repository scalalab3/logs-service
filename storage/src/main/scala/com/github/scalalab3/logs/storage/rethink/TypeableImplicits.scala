package com.github.scalalab3.logs.storage.rethink

import java.util

import com.github.scalalab3.logs.common_macro._
import com.rethinkdb.net.Cursor
import shapeless.Typeable

import scala.util.Try

object TypeableImplicits {
  implicit val typeableCursor: Typeable[Cursor[HM]] =
    new Typeable[Cursor[HM]] {
      override def cast(t: Any): Option[Cursor[HM]] = {
        if (t == null) None
        else t match {
          case c: Cursor[_] => Try(c.asInstanceOf[Cursor[HM]]).toOption
          case _ => None
        }
      }

      override def describe: String = "Cursor[util.HashMap[String, Any]]"
    }

  implicit val typeableList: Typeable[util.List[_]] =
    new Typeable[util.List[_]] {
      override def cast(t: Any): Option[util.List[_]] = {
        if (t == null) None
        else t match {
          case c: util.List[_] => Some(c)
          case _ => None
        }
      }

      override def describe: String = "util.List[_]"
    }

  implicit val typeableMap: Typeable[HM] =
    new Typeable[HM] {
      override def cast(t: Any): Option[HM] = {
        if (t == null) None
        else t match {
          case c: util.Map[_, _] => Try(c.asInstanceOf[HM]).toOption
          case _ => None
        }
      }

      override def describe: String = "util.HashMap[String, Any]"
    }
}