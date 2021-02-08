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

package org.combinators.websecbench.processing

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.{CodeGenerator, ComponentTag, MetaData, PathTraversalVulnerability, Repository, StaticString, TaggedComponent, TaintSource, UncheckedString}
import org.combinators.cls.types.syntax._
import org.combinators.websecbench.SemanticTypes._


object ReplaceFilenameWithStaticString extends TaggedComponent {
  val tags = Set(ComponentTag.Process)

  val relativeToBenchmarkDir: MethodDeclaration = {
    Java(s"""
         |public String relativeToBenchmarkDir(String filename) {
         |   // Simple ? condition that assigns constant to bar on true condition
         |   int num = 106;
         |   return (7*18) + num > 200 ? "This_should_always_happen" : filename;
         |}
         |""".stripMargin).methodDeclarations().head
  }

  def apply(fileName: CodeGenerator[Expression]): CodeGenerator[Expression] = {
    fileName.copy(
      methods = relativeToBenchmarkDir +: fileName.methods,
      currentNode = Java(s"relativeToBenchmarkDir(${fileName.currentNode})")
        .expression[Expression](),
      sourceData = fileName.sourceData.map{
        case UncheckedString() => StaticString()
        case x:TaintSource => x
      }
    )
  }

  val semanticType = JavaString =>: JavaFilename

  def addToRepository(
    repository: ReflectedRepository[Repository.type]
  ): ReflectedRepository[Repository.type] =
    repository.addCombinator(this)
}
