import akka.actor.ActorSystem
import akka.stream.scaladsl.Tcp.{IncomingConnection, ServerBinding}
import akka.stream.scaladsl.{Flow, Framing, Source, Tcp}
import akka.util.ByteString

import scala.concurrent.Future

implicit val actorSystem: ActorSystem = ActorSystem("stream")

val host = "localhost"
val port = 8888

val connections: Source[IncomingConnection, Future[ServerBinding]] =
  Tcp().bind(host, port)

val echo = Flow[ByteString]
  .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
  .map(_.utf8String)
  .map(_ + "!!!\n")
  .map(ByteString(_))

connections.runForeach { connection =>
  // server logic, parses incoming commands
  val commandParser = Flow[String].takeWhile(_ != "BYE").map(_ + "!")

  import connection._
  val welcomeMsg = s"Welcome to: $localAddress, you are: $remoteAddress!"
  val welcome = Source.single(welcomeMsg)

  val serverLogic = Flow[ByteString]
    .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
    .map(_.utf8String)
    .via(commandParser)
    // merge in the initial banner after parser
    .merge(welcome)
    .map(_ + "\n")
    .map(ByteString(_))
  connection.handleWith(serverLogic)
}
