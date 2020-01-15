package org.combinators.websecbench.request

import com.github.javaparser.ast.expr.Expression
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.{CodeGenerator, ComponentTag, Repository, TaggedComponent}
import org.combinators.websecbench.SemanticTypes.JavaString
import SemanticTypes._
import com.github.javaparser.ast.body.MethodDeclaration
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._

object GetCookie extends TaggedComponent {
  val tags = Set(ComponentTag.ReadFromRequest)

  val getCookieMethod: MethodDeclaration = {
    Java(
      s"""
         |public String getCookie(HttpServletRequest request) throws IOException {
         |   javax.servlet.http.Cookie[] theCookies = request.getCookies();
         |
         |		String param = "noCookieValueSupplied";
         |		if (theCookies != null) {
         |			for (javax.servlet.http.Cookie theCookie : theCookies) {
         |				if (theCookie.getName().equals("BenchmarkTest00001")) {
         |					param = java.net.URLDecoder.decode(theCookie.getValue(), "UTF-8");
         |					break;
         |				}
         |			}
         |		}
         |    return param;
         |}
         |""".stripMargin).methodDeclarations().head
  }

  def apply(): CodeGenerator[Expression] = {
    CodeGenerator(
      methods = List(getCookieMethod),
      currentNode = Java(s"getCookie(${CodeGenerator.requestExpr})").expression[Expression](),
      toMethodBody = expr => Java(s"${expr};").statements(),
      unitTests = Seq.empty
    )
  }

  val semanticType: Type = RequestContent :&: JavaString

  def addToRepository(repository: ReflectedRepository[Repository.type]): ReflectedRepository[Repository.type] =
    repository.addCombinator(this)
}
