package kumoshi

import sttp.model.{HeaderNames, StatusCode}
import sttp.tapir._
import sttp.tapir.Codec.JsonCodec

object authz {
  case class RegisterReq(
      code: String,
      deviceDesc: String,
      deviceId: String
  )
  implicit val codddd: JsonCodec[RegisterReq] = Codec.json { _ => DecodeResult.Missing } { v =>
    s"""
      | {
      |   "code": "${v.code}",
      |   "deviceDesc": "${v.deviceDesc}",
      |   "deviceId": "${v.deviceId}"
      | }
      |""".stripMargin
  }

  // pass Register info as the input.
  // output and error output are just simple string.
  val registerDevice: Endpoint[RegisterReq, (StatusCode, String), String, Nothing] =
    endpoint.post
      .in("token/json/2/device/new")
      .in(header(HeaderNames.Authorization, "Bearer "))
      .in(anyJsonBody[RegisterReq])
      .errorOut(statusCode)
      .errorOut(stringBody)
      .out(stringBody)

  // pass current Token as the input.
  // output and error output are just simple string.
  val refreshToken: Endpoint[String, (StatusCode, String), String, Nothing] =
    endpoint.post
      .in("token/json/2/user/new")
      .in(auth.bearer)
      .errorOut(statusCode)
      .errorOut(stringBody)
      .out(stringBody)

}
