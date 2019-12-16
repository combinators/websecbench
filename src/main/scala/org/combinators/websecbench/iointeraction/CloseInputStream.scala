package org.combinators.websecbench.iointeraction

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.CodeGenerator
import org.combinators.websecbench.SemanticTypes.JavaInputStream
import org.combinators.websecbench.SemanticTypes.JavaVoid
import org.combinators.cls.types.syntax._

class CloseInputStream {
	val closeInputStream: MethodDeclaration = {
		Java(
				s"""
         |private void closeInputStream(InputStream is, HttpServletResponse response){
         |    try {
         |    	if(is != null){
         |        is.close();
         |      }
         |    } catch (Exception e) {
         |        System.out.println("Couldn't close InputStream");
         |    }
         |""".stripMargin).methodDeclarations().head
	}

	def apply(fileName: CodeGenerator[Expression]): CodeGenerator[Expression] = {
		fileName.copy(
				currentNode = Java(s"closeInputStream(${fileName.currentNode})").expression()
		)
	}

	val semanticType = JavaInputStream =>: JavaVoid
}
