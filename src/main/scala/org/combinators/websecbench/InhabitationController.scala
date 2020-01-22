package org.combinators.websecbench
import java.nio.file.Paths

import cats.effect.{ExitCode, IO, IOApp}
import com.github.javaparser.ast.expr.Expression
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.cls.types.Type
import org.combinators.jgitserv.{BranchTransaction, GitService, ResourcePersistable}
import org.combinators.templating.persistable.BundledResource
import org.combinators.websecbench.SemanticTypes.JavaVoid


class BenchmarkController(
  tags: Set[ComponentTag],
  targetType: Type,
  benchmarkName: String,
  maximalNumberOfResults: Int = 100,
  port: Int = 9000) extends IOApp {

  lazy val buildDotSbt: BundledResource =
    BundledResource("/build.sbt", Paths.get("build.sbt"), getClass)
  lazy val owaspUtils: BundledResource =
    BundledResource("/org/owasp/benchmark/helpers/Utils.java",
      Paths.get("src", "main", "java", "org", "owasp", "benchmark", "helpers", "Utils.java"),
      getClass)
  lazy val storeResource = ResourcePersistable.apply
  lazy val storeCompilationUnit = CodeGenerator.compilationUnitPersistable[Expression](benchmarkName)
  lazy val storeVulnerabilityReport = CodeGenerator.vulnerabilityReportPersistable[Expression](benchmarkName)


  lazy val Gamma = Repository.repository(tags)
  lazy val results =
    Gamma.inhabit[CodeGenerator[Expression]](targetType)

  lazy val emptyBenchmarkBranch: String = "empty_benchmark"
  lazy val emptyBenchmark: BranchTransaction =
    BranchTransaction
      .empty(emptyBenchmarkBranch)
      .persist(buildDotSbt)(storeResource)
      .persist(owaspUtils)(storeResource)
      .commit("Add shared resources")

  def transactionForResult(resultNumber: Int): BranchTransaction = {
    val result = results.interpretedTerms.index(BigInt(resultNumber))
    BranchTransaction
      .checkout(emptyBenchmarkBranch)
      .fork(s"variation_${resultNumber}")
      .persist(result)(storeCompilationUnit)
      .persist(result)(storeVulnerabilityReport)
      .commit(s"Add benchmark ${benchmarkName} variation ${resultNumber}")
  }

  def transactions: Seq[BranchTransaction] =
    emptyBenchmark +:
    Seq.tabulate[BranchTransaction](
      results.size
        .map(n => if (n > maximalNumberOfResults) maximalNumberOfResults else n.toInt)
        .getOrElse(maximalNumberOfResults)
    )(transactionForResult)

  def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- IO { println(s"Computing solutions") }
      branches = transactions
      _ <- IO { println(s"Use: git clone http://127.0.0.1:${port}/$benchmarkName $benchmarkName") }
      exitCode <- new GitService(branches, benchmarkName, port).run(args)
    } yield exitCode
  }
}

object Benchmark42
  extends BenchmarkController(
    tags = Set(ComponentTag.FileIO, ComponentTag.Process, ComponentTag.ReadFromRequest),
    targetType = JavaVoid,
    benchmarkName = "benchmark42"
  )


