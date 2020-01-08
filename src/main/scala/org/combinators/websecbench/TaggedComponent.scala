package org.combinators.websecbench

import org.combinators.cls.interpreter.ReflectedRepository

trait TaggedComponent {
  val tags: Set[ComponentTag]
  def addToRepository(repository: ReflectedRepository[Repository.type]): ReflectedRepository[Repository.type]
}
