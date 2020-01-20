package org.combinators.websecbench.processing

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.{CodeGenerator, ComponentTag, Repository, TaggedComponent}
import org.combinators.cls.types.syntax._
import org.combinators.websecbench.SemanticTypes._


object ReplaceFilenameWithStaticString extends TaggedComponent {
  val tags = Set(ComponentTag.Process)

  val relativeToBenchmarkDir: MethodDeclaration = {
    Java(s"""
         |public String relativeToBenchmarkDir(String filename) {
         |   // Simple ? condition that assigns constant to bar on true condition
         |   int num = 106;
         |   return (7*18) + num > 200 ? "This_should_always_happen" : filename;
         |}
         |""".stripMargin).methodDeclarations().head
  }

  def apply(fileName: CodeGenerator[Expression]): CodeGenerator[Expression] = {
    fileName.copy(
      methods = relativeToBenchmarkDir +: fileName.methods,
      currentNode = Java(s"relativeToBenchmarkDir(${fileName.currentNode})")
        .expression[Expression]()
    )
  }

  val semanticType = JavaString =>: JavaFilename

  def addToRepository(
    repository: ReflectedRepository[Repository.type]
  ): ReflectedRepository[Repository.type] =
    repository.addCombinator(this)
}
