package kumoshi

import cats.Show
import cats.effect.ExitCode
import com.monovore.decline._
import zio._
import zio.console._
import cats.implicits._

object KumoshiCLI extends App {
  implicit val helpShow: Show[Help] = Show.fromToString[Help]

  val commandDescription = Command(
    "kumoshi",
    "A client to the remarkable hardware"
  )(
    Opts.unit
  )

  def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    (for {
      parsedArgs <- commandParser(args)
      exit       <- program(parsedArgs).fold(_ => ExitCode(2), _ => ExitCode.Success)
    } yield exit).merge.map(_.code)
  }

  def commandParser(args: List[String]) =
    ZIO
      .fromEither(
        // sys.env is an effect. ZIO has System getEnv for a single value,
        // but not for all env Map.
        commandDescription.parse(args, sys.env)
      )
      .flatMapError { help =>
        putStrLn(help.show).map { _ =>
          // if --help flag called explicitly, it's not really an error state...
          if (help.errors.isEmpty) {
            ExitCode.Success
          } else {
            ExitCode.Error
          }
        }
      }

  def program(args: Unit): ZIO[Console, String, String] =
    for {
      _ <- putStrLn("kumoshi working...")
      x <- ZIO.fromEither("".asRight[String])
    } yield (x)
}
