package kumoshi

import cats.Show
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
    commandDescription
      .parse(args, sys.env)
      .fold(
        help => putStrLn(help.show).map(_ => 1),
        parsedArgs => program(parsedArgs).map(_ => 0)
      )
  }

  def program(args: Unit): ZIO[Console, Nothing, Unit] =
    for {
      _ <- putStrLn("kumoshi working...")
    } yield ()
}
