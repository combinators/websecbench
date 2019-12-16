package org.combinators.websecbench

import org.combinators.cls.types.{Constructor, Type}

object SemanticTypes {
  val JavaString: Type = Constructor("String")
  val JavaFilename: Type = Constructor("FileName")
  val JavaInputStream: Type = Constructor("InputStream")
  val JavaVoid: Type = Constructor("Void")
}
