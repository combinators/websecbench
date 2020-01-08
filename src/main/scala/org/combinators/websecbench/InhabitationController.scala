package org.combinators.websecbench
import java.nio.file.Paths

import com.github.javaparser.ast.expr.Expression
import com.google.inject.Inject
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, InhabitationController, Results, RoutingEntries}
import org.combinators.cls.interpreter.CombinatorInfo
import org.combinators.websecbench.CodeGenerator
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.templating.persistable.ResourcePersistable
import org.combinators.cls.types.Type
import org.combinators.templating.persistable.BundledResource
import org.combinators.websecbench.SemanticTypes.JavaVoid
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle


class BenchmarkController(
  webJars: WebJarsUtil,
  lifeCycle: ApplicationLifecycle,
  tags: Set[ComponentTag],
  targetType: Type,
  benchmarkName: String)
  extends InhabitationController(webJars, lifeCycle)
    with RoutingEntries {

  lazy val buildDotSbt: BundledResource =
    BundledResource("/build.sbt", Paths.get("build.sbt"), getClass)
  lazy val owaspUtils: BundledResource =
    BundledResource("/org/owasp/benchmark/helpers/Utils.java",
      Paths.get("src/main/java/org/owasp/benchmark/helpers/Utils.java"),
      getClass)
  implicit val resourcePersistable = ResourcePersistable.apply

  lazy val Gamma = Repository.repository(tags)
  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy implicit val persistable = CodeGenerator.persistable[Expression](benchmarkName)
  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma)
        .addJob[CodeGenerator[Expression]](targetType)
        .compute()
        .addExternalArtifact(buildDotSbt)
        .addExternalArtifact(owaspUtils)
  lazy val controllerAddress: String = benchmarkName
}

class Benchmark42 @Inject()(webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle)
  extends BenchmarkController(
    webJars = webJars,
    lifeCycle = lifeCycle,
    tags = Set(ComponentTag.FileIO, ComponentTag.Process, ComponentTag.ReadFromRequest),
    targetType = JavaVoid,
    benchmarkName = "benchmark42"
  )


