package org.combinators.websecbench.processing

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.CodeGenerator
import org.combinators.cls.types.syntax._
import org.combinators.websecbench.SemanticTypes._

class AttachDirectoryName {

  val relativeToBenchmarkDir: MethodDeclaration = {
    Java(
      s"""
         |public String relativeToBenchmarkDir(String filename) {
         |   return org.owasp.benchmark.helpers.Utils.testfileDir + filename;
         |}
         |""".stripMargin).methodDeclarations().head
  }

  def apply(fileName: CodeGenerator[Expression]): CodeGenerator[Expression] = {
    fileName.copy(
      currentNode = Java(s"relativeToBenchmarkDir(${fileName.currentNode})").expression()
    )
  }

  val semanticType = JavaString =>: JavaFilename :&: JavaString
}
