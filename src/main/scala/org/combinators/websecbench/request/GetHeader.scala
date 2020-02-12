package org.combinators.websecbench.request

import com.github.javaparser.ast.expr.Expression
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.{CodeGenerator, ComponentTag, PathTraversalVulnerability, Repository, SQLInjectionVulnerability, TaggedComponent, UncheckedString}
import org.combinators.websecbench.SemanticTypes.{Encoded, JavaString}
import SemanticTypes._
import com.github.javaparser.ast.body.MethodDeclaration
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._

object GetHeader extends TaggedComponent {
  val tags = Set(ComponentTag.ReadFromRequest)

  val getCookieMethod: MethodDeclaration = Java(
      s"""
         |public String getHeader(HttpServletRequest request) throws IOException {
         |  String param = "";
         |	if (request.getHeader("BenchmarkTest00008") != null) {
         |    param = request.getHeader("BenchmarkTest00008");
         |  }
         |  return param;
         |}
         |""".stripMargin).methodDeclarations().head

  def apply(): CodeGenerator[Expression] = {
    CodeGenerator(
      methods = List(getCookieMethod),
      currentNode = Java(s"getHeader(${CodeGenerator.requestExpr})").expression[Expression](),
      toMethodBody = expr => Java(s"${expr};").statements(),
      unitTests = Seq.empty,
      metaData =  Seq.empty,
      sourceData = Seq(UncheckedString())
      )
  }

  val semanticType: Type = RequestContent :&: JavaString :&: Encoded

  def addToRepository(repository: ReflectedRepository[Repository.type]): ReflectedRepository[Repository.type] =
    repository.addCombinator(this)
}
