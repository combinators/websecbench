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
import org.combinators.websecbench.{CodeGenerator, ComponentTag, Repository, TaggedComponent}
import org.combinators.websecbench.SemanticTypes.{JavaOutputStream, JavaVoid, Unused, UsageStatus}

object CloseOutputStream extends TaggedComponent {
  val tags = Set(ComponentTag.FileIO)
  val closeInputStream: MethodDeclaration = {
    Java(
      s"""
         |private void closeOutputStream(java.io.OutputStream os) {
         |    try {
         |    	if(is != null) {
         |        is.close();
         |      }
         |    } catch (Exception e) {
         |        System.out.println("Couldn't close OutStream");
         |    }
         |}
         |""".stripMargin).methodDeclarations().head
  }

  def apply(inputStreamGenerator: CodeGenerator[Expression]): CodeGenerator[Expression] = {
    inputStreamGenerator.copy(
      methods = closeInputStream +: inputStreamGenerator.methods,
      currentNode = Java(s"closeOutputStream(${inputStreamGenerator.currentNode})").expression[Expression]()
      )
  }
  
  val semanticType = JavaOutputStream(UsageStatus)   =>: JavaVoid

  def addToRepository(repository: ReflectedRepository[Repository.type]): ReflectedRepository[Repository.type] =
    repository.addCombinator(this)
}
