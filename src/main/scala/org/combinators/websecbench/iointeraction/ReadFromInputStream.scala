package org.combinators.websecbench.iointeraction

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.CodeGenerator
import org.combinators.websecbench.SemanticTypes.JavaInputStream
import org.combinators.cls.types.syntax._

class ReadFromInputStream {

  val readFromInputStream: MethodDeclaration = {
    Java(
      s"""
         |private InputStream readFromInputStream(InputStream is, HttpServletResponse response){
         |    try {
         |        byte[] b = new byte[1000];
         |        int size = is.read(b);
         |        response.getWriter().println();
         |        response.getWriter().println(
         |            org.owasp.esapi.ESAPI.encoder().encodeForHTML(new String(b,0,
         |              size))
         |          );
         |        response.getWriter().println();
         |        return is;
         |    } catch (IOException | NullPointerException e) {
         |        System.out.println("Couldn't read from InputStream");
         |        response.getWriter().println(
         |            "Problem reading from InputStream: "
         |               + org.owasp.esapi.ESAPI.encoder().encodeForHTML(e.getMessage())
         |         );
         |    }
         |    return null;
         |""".stripMargin).methodDeclarations().head
  }

  def apply(fileName: CodeGenerator[Expression]): CodeGenerator[Expression] = {
    fileName.copy(
      currentNode = Java(s"readFromInputStream(${fileName.currentNode}, ${CodeGenerator.responseExpr})").expression()
      )
  }

  val semanticType = JavaInputStream =>: JavaInputStream
}
