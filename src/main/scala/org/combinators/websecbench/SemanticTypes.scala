package org.combinators.websecbench

import org.combinators.cls.types.{Constructor, Type}

object SemanticTypes {
  val JavaString: Type = Constructor("String")
  val Decoded: Type = Constructor("Decoded")
  val JavaFilename: Type = Constructor("FileName")
  def JavaInputStream(status: Type): Type = Constructor("InputStream", status)
  val JavaVoid: Type = Constructor("Void")
  val Used: Type = Constructor("Used")
  val Unused: Type = Constructor("Unused")
  val Encoded: Type = Constructor("Encoded")
  val JavaSQL: Type = Constructor("JavaSQL");
}
