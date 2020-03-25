package org.combinators.websecbench.processing

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.{CodeGenerator, ComponentTag, MetaData, PathTraversalVulnerability, Repository, TaggedComponent}
import org.combinators.cls.types.syntax._
import org.combinators.websecbench.SemanticTypes._


object URLDecoder extends TaggedComponent {
  val tags = Set(ComponentTag.Process)

  val relativeToBenchmarkDir: MethodDeclaration = {
    Java(s"""
            |public String urlDecoding(String param) throws java.io.UnsupportedEncodingException {
            |   return java.net.URLDecoder.decode(param, "UTF-8");
            |}
            |""".stripMargin).methodDeclarations().head
  }

  def apply(fileName: CodeGenerator[Expression]): CodeGenerator[Expression] = {
    fileName.copy(
      methods = relativeToBenchmarkDir +: fileName.methods,
      currentNode = Java(s"urlDecoding(${fileName.currentNode})")
        .expression[Expression]()
      )
  }

  val semanticType = JavaString :&: Encoded =>: JavaString :&: Decoded

  def addToRepository(
                       repository: ReflectedRepository[Repository.type]
                     ): ReflectedRepository[Repository.type] =
    repository.addCombinator(this)
}
