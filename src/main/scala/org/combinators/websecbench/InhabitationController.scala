package org.combinators.websecbench
import java.nio.file.Paths

import cats.effect.{ExitCode, IO, IOApp}
import com.github.javaparser.ast.expr.Expression
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.cls.types.Type
import org.combinators.jgitserv.{BranchTransaction, GitService, ResourcePersistable}
import org.combinators.templating.persistable.BundledResource
import org.combinators.websecbench.SemanticTypes.JavaVoid
import org.eclipse.jgit.lib.BranchConfig

case class BenchmarkSelector(
  tags: Set[ComponentTag],
  targetType: Type,
  maximalNumberOfResults: Int
)

class BenchmarkController(
  selectedBenchmarks: Set[BenchmarkSelector],
  benchmarkName: String,
  shuffleSolutions: Boolean = true,
  port: Int = 9000) extends IOApp {

  lazy val buildDotSbt: BundledResource =
    BundledResource("/build.sbt", Paths.get("build.sbt"), getClass)
  lazy val owaspUtils: BundledResource =
    BundledResource("/org/owasp/benchmark/helpers/Utils.java",
      Paths.get("src", "main", "java", "org", "owasp", "benchmark", "helpers", "Utils.java"),
      getClass)
  lazy val storeResource = ResourcePersistable.apply
  
  lazy val emptyBenchmark: BranchTransaction =
    BranchTransaction
      .empty(benchmarkName)
      .persist(buildDotSbt)(storeResource)
      .persist(owaspUtils)(storeResource)
      .commit("Add shared resources")

  lazy val numberFormat: String = {
    val maxBenchmarks = selectedBenchmarks.map(_.maximalNumberOfResults).sum
    s"%0${maxBenchmarks.toString.length}d"    
  }

  def transactionFor(benchmarkSelector: BenchmarkSelector): Seq[Int => BranchTransaction] = { 
    val Gamma = Repository.repository(benchmarkSelector.tags)
    val results = Gamma.inhabit[CodeGenerator[Expression]](benchmarkSelector.targetType)
    val toStore = results.size.map(s => 
          Math.min(benchmarkSelector.maximalNumberOfResults, s.toInt)
        ).getOrElse(benchmarkSelector.maximalNumberOfResults)

    (0 until toStore).foldLeft(Seq.empty[Int => BranchTransaction]) { case (transactions, resultNumber) => 
      val nextTransaction = (nextNumber : Int) => {
        val currentName = s"%s_$numberFormat".format(benchmarkName, nextNumber)
        val storeCompilationUnit = CodeGenerator.compilationUnitPersistable[Expression](currentName)
        val storeVulnerabilityReport = CodeGenerator.vulnerabilityReportPersistable[Expression](currentName)
        val result = results.interpretedTerms.index(BigInt(resultNumber))
        BranchTransaction
          .checkout(benchmarkName)
          .persist(result)(storeCompilationUnit)
          .persist(result)(storeVulnerabilityReport)
          .commit(s"Add benchmark ${currentName}")
      }
      nextTransaction +: transactions
    }
  }
  
  def computeTransactions: Seq[BranchTransaction] = {
    val transactions =
      selectedBenchmarks.toSeq.flatMap(transactionFor)
    val suffledTransactions =
      if (shuffleSolutions) scala.util.Random.shuffle(transactions)
      else transactions
    emptyBenchmark +:
      suffledTransactions
      .zipWithIndex
      .map { case (transaction, number) => transaction(number) }
  }

  def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- IO { println(s"Computing solutions") }
      transactions = computeTransactions
      _ <- IO { println(s"Use: git clone http://127.0.0.1:${port}/$benchmarkName $benchmarkName") }
      exitCode <- new GitService(transactions, benchmarkName, port).run(args)
    } yield exitCode
  }
}

object Benchmark42
  extends BenchmarkController(
    Set(
      BenchmarkSelector(
        tags = Set(
          ComponentTag.FileIO,
          ComponentTag.Process,
          ComponentTag.ReadFromRequest,
          ComponentTag.DatabaseIO
        ),
        targetType = JavaVoid,
        maximalNumberOfResults = 100
      )
    ),
    benchmarkName = "benchmark42",
    shuffleSolutions = false
  )


