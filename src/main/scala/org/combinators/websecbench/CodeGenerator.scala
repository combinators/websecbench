package org.combinators.websecbench

import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Path, Paths}
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.stmt.Statement
import org.combinators.templating.persistable.{JavaPersistable, Persistable}
import org.combinators.templating.twirl.Java

case class CodeGenerator[NodeType](
  methods: List[MethodDeclaration],
  currentNode: NodeType,
  toMethodBody: NodeType => Seq[Statement],
  unitTests : Seq[CompilationUnit],
  metaData: Seq[MetaData],
  sourceData: Seq[TaintSource]
) {
  def toCode(benchmarkName: String): CompilationUnit = {
    Java(
      s"""
         |import javax.servlet.http.HttpServlet;
         |import javax.servlet.http.HttpServletRequest;
         |import javax.servlet.http.HttpServletResponse;
         |import javax.servlet.ServletException;
         |import java.io.IOException;
         |
         |public class ${benchmarkName} extends HttpServlet {
         |${methods.mkString("\n")}
         |
         |    public void doPost(HttpServletRequest ${CodeGenerator.requestExpr}, HttpServletResponse ${CodeGenerator.responseExpr}) throws ServletException, IOException {
         |        ${toMethodBody(currentNode).mkString("\n")}
         |    }
         |
         |}
         |
         |""".stripMargin).compilationUnit()
  }

  def vulnerabilityReport(benchmarkName: String): String = {
    metaData.map(n =>{
      n.getTaintSources.intersect(sourceData).isEmpty match {
        case true => n.makeSafe.toReportElement(benchmarkName)
        case false => n.toReportElement(benchmarkName)
      }
    }).mkString("\n")
  }
}


object CodeGenerator {
  def requestExpr: Expression =
    Java(s"request").expression()

  def responseExpr: Expression =
    Java(s"response").expression()



  def compilationUnitPersistable[A](benchmarkName: String)(implicit javaPersistable: Persistable.Aux[CompilationUnit]): Persistable.Aux[CodeGenerator[A]] =
    new Persistable {
      type T = CodeGenerator[A]
      def rawText(elem: CodeGenerator[A]) =
        javaPersistable.rawText(elem.toCode(benchmarkName))

      def path(elem: CodeGenerator[A]) =
        javaPersistable.path(elem.toCode(benchmarkName))
    }

  def vulnerabilityReportPersistable[A](benchmarkName: String): Persistable.Aux[CodeGenerator[A]] =
    new Persistable {
      type T = CodeGenerator[A]
      def rawText(elem: CodeGenerator[A]): Array[Byte] =
        elem.vulnerabilityReport(benchmarkName).getBytes(StandardCharsets.UTF_8)

      def path(elem: CodeGenerator[A]): Path = Paths.get(".", "vulnerabilityreport.xml")
    }
}
