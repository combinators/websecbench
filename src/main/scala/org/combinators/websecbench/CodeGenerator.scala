package org.combinators.websecbench

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.Statement
import org.combinators.templating.twirl.Java

case class CodeGenerator[NodeType](
  methods: List[MethodDeclaration],
  currentNode: NodeType,
  toMethodBody: NodeType => Seq[Statement]
) {
  def toCode(benchmarkName: String): CompilationUnit = {
    Java(
      s"""
         |import javax.servlet.http.HttpServlet;
         |import javax.servlet.http.HttpServletRequest;
         |import javax.servlet.http.HttpServletResponse;
         |
         |public class ${benchmarkName} extends HttpServlet {
         |${methods.mkString("\n")}
         |
         |    public void doPost(HttpServletRequest ${CodeGenerator.requestExpr}, HttpServletResponse ${CodeGenerator.responseExpr}) {
         |      ${toMethodBody(currentNode).mkString("\n")}
         |    }
         |
         |}
         |
         |""".stripMargin).compilationUnit()
  }
}


object CodeGenerator {
  def requestExpr: Expression =
    Java(s"request").expression()

  def responseExpr: Expression =
    Java(s"response").expression()
}
