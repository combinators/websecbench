package org.combinators.websecbench.iointeraction

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.{CodeGenerator, ComponentTag, Repository, TaggedComponent}
import org.combinators.websecbench.SemanticTypes.JavaInputStream
import org.combinators.websecbench.SemanticTypes.JavaFilename
import org.combinators.websecbench.SemanticTypes.Unused
import org.combinators.cls.types.syntax._

object CreateFileInputStream extends TaggedComponent {
  val tags = Set(ComponentTag.FileIO)

  val createFileInputStream: MethodDeclaration = {
    Java(
      s"""
         |public java.io.InputStream openFileInputStream(String filename, HttpServletResponse response) throws java.io.IOException {
         |   try {
         |      java.io.FileInputStream fis = null;
         |      fis = new java.io.FileInputStream(new java.io.File(filename));
         |      response.getWriter().println("The beginning of file: '" + org.owasp.esapi.ESAPI
         |          .encoder().encodeForHTML(filename) + "' is:");
         |      return fis;
         |   } catch (Exception e) {
         |        System.out.println("Couldn't open FileInputStream on file: '" + filename +
         |        "'");
         |        response.getWriter().println(
         |        "Problem getting FileInputStream: "
         |            + org.owasp.esapi.ESAPI.encoder().encodeForHTML(e.getMessage())
         |       );
         |   }
         |   return null;
         |}
         |""".stripMargin).methodDeclarations().head
  }

  def apply(fileName: CodeGenerator[Expression]): CodeGenerator[Expression] = {
    fileName.copy(
      methods = createFileInputStream +: fileName.methods,
      currentNode = Java(s"openFileInputStream(${fileName.currentNode}, ${CodeGenerator.responseExpr})").expression[Expression]()
      )
  }

  val semanticType = JavaFilename =>: JavaInputStream(Unused)

  def addToRepository(repository: ReflectedRepository[Repository.type]): ReflectedRepository[Repository.type] =
    repository.addCombinator(this)
}
