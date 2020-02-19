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
