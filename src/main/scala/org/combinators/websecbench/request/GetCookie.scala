/*
 * Websecbench is a suite of web security benchmarks generated by (CL)S.
 * Copyright (C) 2021  Jan Bessai and Malte Mues
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.combinators.websecbench.request

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.{
  CodeGenerator,
  ComponentTag,
  Repository,
  TaggedComponent,
  UncheckedString
}
import org.combinators.websecbench.SemanticTypes.JavaString
import org.combinators.websecbench.request.SemanticTypes._

object GetCookie extends TaggedComponent {
  val tags = Set(ComponentTag.ReadFromRequest)

  val getCookieMethod: MethodDeclaration = {
    Java(s"""
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
      currentNode = Java(s"getCookie(${CodeGenerator.requestExpr})")
        .expression[Expression](),
      toMethodBody = expr => Java(s"${expr};").statements(),
      unitTests = Seq.empty,
      metaData = Seq.empty,
      sourceData = Seq(UncheckedString())
    )
  }

  val semanticType: Type = RequestContent :&: JavaString

  def addToRepository(
      repository: ReflectedRepository[Repository.type]
  ): ReflectedRepository[Repository.type] =
    repository.addCombinator(this)
}
