package kumoshi

import cats.Show
import cats.effect.ExitCode
import cats.implicits._
import com.monovore.decline._
import kumoshi.authz.RegisterReq
import sttp.model.StatusCode
import sttp.tapir.DecodeResult
import zio._
import zio.console._

object KumoshiCLI extends App {
  implicit val helpShow: Show[Help] = Show.fromToString[Help]

  val commandDescription: Command[Unit] = Command(
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

  def commandParser(args: List[String]): ZIO[Console, ExitCode, Unit] =
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
      _ <- ZIO.effectTotal {
            import sttp.client._
            import sttp.tapir.client.sttp._

            implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()

            val req = authz.registerDevice
              .toSttpRequest(uri"https://my.remarkable.com")
              .apply(RegisterReq("12345678", "test", "poop"))

            val result: DecodeResult[Either[(StatusCode, String), String]] = req.send().body

            /* responses:
              Value(Left((400,Failed to decode POST body as JSON
              Value(Left((400,Code has the wrong length, it should be 8
              Value(Left((400,Missing required field (DeviceDesc and/or DeviceID)
              Value(Left((400,Invalid One-time-code
             */
            result
          } >>= (jj => putStrLn(jj.toString))
      x <- ZIO.fromEither("".asRight[String])
    } yield (x)
}
