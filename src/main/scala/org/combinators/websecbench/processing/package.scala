package org.combinators.websecbench

package object processing {
  val components: Seq[TaggedComponent] =
    Seq(
      AttachDirectoryName,
      ReplaceFilenameWithStaticString
    )
}
