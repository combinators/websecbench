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

package org.combinators.websecbench.iointeraction

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.{
  CodeGenerator,
  ComponentTag,
  PathTraversalVulnerability,
  Repository,
  TaggedComponent
}
import org.combinators.websecbench.SemanticTypes.{
  JavaFilename,
  JavaInputStream,
  Unused
}

object CreateFileInputStream extends TaggedComponent {
  val tags = Set(ComponentTag.FileIO)

  val createFileInputStream: MethodDeclaration = {
    Java(s"""
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
      currentNode = Java(
        s"openFileInputStream(${fileName.currentNode}, ${CodeGenerator.responseExpr})"
      ).expression[Expression](),
      metaData = fileName.metaData :+ PathTraversalVulnerability(true)
    )
  }

  val semanticType = JavaFilename =>: JavaInputStream(Unused)

  def addToRepository(
      repository: ReflectedRepository[Repository.type]
  ): ReflectedRepository[Repository.type] =
    repository.addCombinator(this)
}
