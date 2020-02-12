package org.combinators.websecbench.databaseinteraction

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.templating.twirl.Java
import org.combinators.websecbench.{CodeGenerator, ComponentTag, Repository, SQLInjectionVulnerability, TaggedComponent}
import org.combinators.websecbench.SemanticTypes.{JavaSQL, JavaString, JavaVoid}
import org.combinators.cls.types.syntax._

object ReadFromDatabase extends TaggedComponent{
  override val tags: Set[ComponentTag] = Set(ComponentTag.DatabaseIO)


  val readFromDatabase: MethodDeclaration = {
    Java(
      s"""
         |private void readFromDatabase(String sql, HttpServletResponse response) {
         |   try {
		     |    java.sql.Connection connection = org.owasp.benchmark.helpers.DatabaseHelper.getSqlConnection();
         |    java.sql.CallableStatement statement = connection.prepareCall( sql );
         |    java.sql.ResultSet rs = statement.executeQuery();
         |    org.owasp.benchmark.helpers.DatabaseHelper.printResults(rs, sql, response);
         |   } catch (java.sql.SQLException e) {
         |    if (org.owasp.benchmark.helpers.DatabaseHelper.hideSQLErrors) {
         |	    response.getWriter().println("Error processing request.");
         |	    return;
         |    }
         |  }
         |}
         |""".stripMargin).methodDeclarations().head
  }

  def apply(sql: CodeGenerator[Expression]): CodeGenerator[Expression] = {
    sql.copy(
      methods = readFromDatabase +: sql.methods,
      currentNode = Java(s"readFromDatabase(${sql.currentNode}, ${CodeGenerator.responseExpr})").expression[Expression](),
      metaData = sql.metaData :+ SQLInjectionVulnerability(true)
      )
  }

  val semanticType = JavaSQL :&: JavaString =>: JavaVoid
  override def addToRepository(repository: ReflectedRepository[Repository.type])
  : ReflectedRepository[Repository.type] = repository.addCombinator(this)
}
