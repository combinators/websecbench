package org.combinators.websecbench.iointeraction

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.{CodeGenerator, ComponentTag, PathTraversalVulnerability, Repository, TaggedComponent}
import org.combinators.websecbench.SemanticTypes.{JavaFilename, JavaOutputStream, Unused}

object CreateFileOutputStream extends TaggedComponent{
  val tags = Set(ComponentTag.FileIO)

  val createFileInputStream: MethodDeclaration = {
    Java(
      s"""
         |public java.io.FileOutputStream openFileOutputStream(String filename, HttpServletResponse response) throws java.io.IOException {
         |    java.io.FileOutputStream fos = null;
         |    try {
         |        fos = new FileOutputStream(fileName, false);
         |        response.getWriter.println("Now ready to write to file: " +
         |          org.owasp.esapi.ESAPI.encoder.encodeForHTML(fileName));
         |        return fos;
         |    }
         |    catch (Exception e) {
         |			System.out.println("Couldn't open FileOutputStream on file: '" + fileName + "'");
         |		}
         |
         |}
         |""".stripMargin).methodDeclarations().head
  }

  def apply(fileName: CodeGenerator[Expression]): CodeGenerator[Expression] = {
    fileName.copy(
      methods = createFileInputStream +: fileName.methods,
      currentNode = Java(s"openFileOutputStream(${fileName.currentNode}, ${CodeGenerator.responseExpr})").expression[Expression](),
      metaData  = fileName.metaData :+ PathTraversalVulnerability(true)
      )
  }

  val semanticType = JavaFilename =>: JavaOutputStream(Unused)

  def addToRepository(repository: ReflectedRepository[Repository.type]): ReflectedRepository[Repository.type] =
    repository.addCombinator(this)
}
