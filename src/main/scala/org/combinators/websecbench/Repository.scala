package org.combinators.websecbench

import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.{InhabitationResult, ReflectedRepository}
import org.combinators.cls.types.Type


object Repository {
  val components: Seq[TaggedComponent] =
    Seq(
      iointeraction.components,
      request.components,
      processing.components,
      databaseinteraction.components
    ).flatten

  def repository(componentTags: Set[ComponentTag]): ReflectedRepository[Repository.type] = {
    val selectedComponents = components.filter(comp => comp.tags.intersect(componentTags).nonEmpty)

    selectedComponents.foldLeft(ReflectedRepository(this, classLoader = getClass.getClassLoader,substitutionSpace = SemanticTypes.kinding)) { case (repo, component) =>
      component.addToRepository(repo)
    }
  }
}
