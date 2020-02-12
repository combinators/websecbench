package org.combinators.websecbench

sealed trait ComponentTag

object ComponentTag {
  case object ReadFromRequest extends ComponentTag
  case object Process extends ComponentTag
  case object FileIO extends ComponentTag
  case object DatabaseIO extends ComponentTag
}
