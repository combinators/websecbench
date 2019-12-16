package org.combinators.websecbench.iointeraction

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.CodeGenerator
import org.combinators.websecbench.SemanticTypes.JavaInputStream
import org.combinators.websecbench.SemanticTypes.JavaFilename
import org.combinators.cls.types.syntax._

class CreateFileInputStream {

  val createFileInputStream: MethodDeclaration = {
    Java(
      s"""
         |public InputStream openFileInputStream(String filename, HttpServletResponse response) {
         |   try {
         |      java.io.FileInputStream fis = null;
         |      fis = new java.io.FileInputStream(new java.io.File(fileName));
         |      response.getWriter().println("The beginning of file: '" + org.owasp.esapi.ESAPI
         |          .encoder().encodeForHTML(fileName) + "' is:");
         |      return fis;
         |   catch (Exception e) {
         |        System.out.println("Couldn't open FileInputStream on file: '" + fileName +
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
      currentNode = Java(s"openFileInputStream(${fileName.currentNode}, ${CodeGenerator.responseExpr})").expression()
      )
  }

  val semanticType = JavaFilename =>: JavaInputStream
}
