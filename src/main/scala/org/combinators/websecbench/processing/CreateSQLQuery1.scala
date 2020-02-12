package org.combinators.websecbench.processing

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.{CodeGenerator, ComponentTag, MetaData, PathTraversalVulnerability, Repository, TaggedComponent}
import org.combinators.cls.types.syntax._
import org.combinators.websecbench.SemanticTypes._


object CreateSQLQuery1 extends TaggedComponent {
  val tags = Set(ComponentTag.Process)

  val relativeToBenchmarkDir: MethodDeclaration = {
    Java(s"""
            |public String relativeToBenchmarkDir(String param) {
            |   return "{call " + param + "}";
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

  val semanticType = JavaString :&: Decoded =>: JavaString :&: JavaSQL

  def addToRepository(
                       repository: ReflectedRepository[Repository.type]
                     ): ReflectedRepository[Repository.type] =
    repository.addCombinator(this)
}
