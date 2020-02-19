package org.combinators.websecbench.iointeraction

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.{CodeGenerator, ComponentTag, Repository, TaggedComponent}
import org.combinators.websecbench.SemanticTypes.JavaInputStream
import org.combinators.websecbench.SemanticTypes.JavaVoid
import org.combinators.websecbench.SemanticTypes.{Used, Unused, UsageStatus}
import org.combinators.cls.types.syntax._

object CloseInputStream extends TaggedComponent {
	val tags = Set(ComponentTag.FileIO)
	val closeInputStream: MethodDeclaration = {
		Java(
				s"""
         |private void closeInputStream(java.io.InputStream is) {
         |    try {
         |    	if(is != null) {
         |        is.close();
         |      }
         |    } catch (Exception e) {
         |        System.out.println("Couldn't close InputStream");
         |    }
         |}
         |""".stripMargin).methodDeclarations().head
	}

	def apply(inputStreamGenerator: CodeGenerator[Expression]): CodeGenerator[Expression] = {
		inputStreamGenerator.copy(
			methods = closeInputStream +: inputStreamGenerator.methods,
			currentNode = Java(s"closeInputStream(${inputStreamGenerator.currentNode})").expression[Expression]()
		)
	}

	val semanticType = JavaInputStream(Used) =>: JavaVoid

	def addToRepository(repository: ReflectedRepository[Repository.type]): ReflectedRepository[Repository.type] =
		repository.addCombinator(this)
}
