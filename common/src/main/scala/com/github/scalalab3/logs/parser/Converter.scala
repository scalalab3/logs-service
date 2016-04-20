package com.github.scalalab3.logs.parser


object Converter {

}

object ToMap {

  import shapeless._, labelled.FieldType, record._

  trait ToMapRec[L <: HList] {
    def apply(l: L): Map[String, Any]
  }

  trait LowPriorityToMapRec {

    implicit def hconsToMapRec1[K <: Symbol, V, T <: HList](implicit
                                                            wit: Witness.Aux[K],
                                                            tmrT: ToMapRec[T]
                                                           ): ToMapRec[FieldType[K, V] :: T] = new ToMapRec[FieldType[K, V] :: T] {
      def apply(l: FieldType[K, V] :: T): Map[String, Any] = {
        Map(wit.value.name -> l.head) ++ tmrT(l.tail)
      }
    }
  }

  object ToMapRec extends LowPriorityToMapRec {

    object optionExtractor extends Poly1 {
      implicit def caseSome[T] = at[Some[T]](_.get :: HNil)

      implicit def caseNone = at[None.type](_ => HNil)

      implicit def default[T] = at[T](a => a)
    }

    implicit val hnilToMapRec: ToMapRec[HNil] = (l: HNil) => Map.empty

    implicit def hconsToMapRec0[K <: Symbol, V, R <: HList, T <: HList](implicit
                                                                        wit: Witness.Aux[K],
                                                                        gen: LabelledGeneric.Aux[V, R],
                                                                        tmrH: ToMapRec[R],
                                                                        tmrT: ToMapRec[T]
                                                                       ): ToMapRec[FieldType[K, V] :: T] = new ToMapRec[FieldType[K, V] :: T] {
      def apply(l: FieldType[K, V] :: T): Map[String, Any] =
        tmrT(l.tail) + (wit.value.name -> tmrH(gen.to(l.head)))
    }
  }

  implicit class ToMapRecOps[A](val a: A) extends AnyVal {
    def toMapRec[L <: HList](implicit
                             gen: LabelledGeneric.Aux[A, L],
                             tmr: ToMapRec[L]
                            ): Map[String, Any] = {
      val to: L = gen.to(a)
      tmr(to)
    }
  }

}


import scala.language.experimental.macros
import scala.reflect.macros.whitebox
// change to j.u.HM
trait FromMap[T] {
  def fromMap(map: Map[String, Any]): T
}

object FromMap {
  implicit def materializeMappable[T]: FromMap[T] = macro materializeMappableImpl[T]

  def materializeMappableImpl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[FromMap[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    val companion = tpe.typeSymbol.companion

    val fields = tpe.decls.collectFirst {
      case m: MethodSymbol if m.isPrimaryConstructor ⇒ m
    }.get.paramLists.head

    val (_, fromMapParams) = fields.map { field ⇒
      val name = field.name.toTermName
      val decoded = name.decodedName.toString
      val returnType = tpe.decl(name).typeSignature

      (q"$decoded → t.$name", q"map($decoded).asInstanceOf[$returnType]")
    }.unzip

    c.Expr[FromMap[T]] { q"""
      new Mappable[$tpe] {
        def fromMap(map: Map[String, Any]): $tpe = $companion(..$fromMapParams)
      }
    """ }
  }
}