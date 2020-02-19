package org.combinators.websecbench

package object iointeraction {
  val components: Seq[TaggedComponent] =
    Seq(
      CloseInputStream,
      CloseOutputStream,
      CreateFileInputStream,
      CreateFileOutputStream,
      ReadFromInputStream
    )
}
