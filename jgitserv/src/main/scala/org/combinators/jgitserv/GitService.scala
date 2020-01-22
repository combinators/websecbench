package org.combinators.jgitserv

import java.io.FileInputStream
import java.nio.file.{Files, Paths}

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import com.twitter.finagle.{Http, ListeningServer}
import com.twitter.io.{Buf, Reader}
import com.twitter.util.Future
import io.finch._
import io.finch.Encode._
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git

import scala.jdk.CollectionConverters._

/** A simple endpoint to host Git repositories.
  *
  * Uses the [[https://git-scm.com/book/en/v2/Git-Internals-Transfer-Protocols "dumb" protocol]],
  * which means repositories cannot be written to externally.
  * The repository is stored in the system's temporary directory and its contents will be lost when the server stops.
  *
  * @constructor Create a new git repository.
  *
  * @see This [[https://examples.javacodegeeks.com/core-java/io/java-io-tmpdir-example/ tutorial]] on
  *      how to set the temporary directory for repositories.
  */
class GitService(transactions: Seq[BranchTransaction], repositoryName: String = "", port: Int = 8081) extends IOApp {
  /** The Git repository in which files are stored while the server is active. */
  private lazy val git: Resource[IO, Git] = {
    def acquire: IO[Git] = {
      for {
        git <- IO {
          val directory = Files.createTempDirectory("jgitserv_hosted_git")
          Git.init().setDirectory(directory.toFile).call()
        }
        _ <- transactions.map(t => t.materialize(git)).toList.sequence
      } yield git
    }
    def release(git: Git): IO[Unit] = IO {
      val directory = git.getRepository.getWorkTree
      git.close()
      FileUtils.deleteDirectory(directory)
    }
    Resource.make(acquire)(release)
  }

  class Endpoints(repo: Git) extends Endpoint.Module[IO] {
    val dumbProtocol: Endpoint[IO, String] =
      get("info" :: "refs") {
        IO {
          Ok(repo
            .branchList()
            .call()
            .asScala
            .map (ref => s"${ref.getObjectId.getName}\t${ref.getName}")
            .mkString("", "\n", "\n"))
        }
      }

    val content: Endpoint[IO, Buf] =
      get(paths[String]) { pathSegments: List[String] =>
        Reader.fromStream(
          new FileInputStream(Paths.get(repo.getRepository.getDirectory.toString, pathSegments: _*).toFile)
        ).read().map(res => Ok(res.get))
      } handle {
        case fnf: java.io.FileNotFoundException => NotFound(fnf)
        case ex: Exception => BadRequest(ex)
      }

    val gitEndpoint = {
      if (!repositoryName.isEmpty) {
        get(repositoryName) :: (dumbProtocol :+: content)
      } else {
        (dumbProtocol :+: content)
      }
    }

    def serve: IO[ListeningServer] = IO(
      Http.server
        .withStreaming(enabled = true)
        .serve(s":$port",
          Bootstrap.configure().serve[Text.Plain](gitEndpoint).toService
        )
    )
  }

  def run(args: List[String]): IO[ExitCode] = {
    git.use(git => {
      val server = Resource.make(new Endpoints(git).serve){ s =>
        IO.suspend(implicitly[ToAsync[Future, IO]].apply(s.close()))
      }
      server
        .use(_ => IO {
          println("Press enter to stop the server")
          scala.io.StdIn.readLine()
        })
        .as(ExitCode.Success)
    })
  }
}
