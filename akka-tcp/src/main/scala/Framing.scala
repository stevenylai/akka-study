import akka.actor.ActorSystem
import akka.stream.scaladsl.{JsonFraming, Source}
import akka.util.ByteString


implicit val actorSystem: ActorSystem = ActorSystem("stream")

val input =
  """
    |[
    | { "name" : "john" },
    | { "name" : "Ég get etið gler án þess að meiða mig" },
    | { "name" : "jack" },
    |]
    |""".stripMargin // also should complete once notices end of array

val result =
  Source.single(ByteString(input)).via(JsonFraming.objectScanner(Int.MaxValue)).runFold(Seq.empty[String]) {
    case (acc, entry) => acc ++ Seq(entry.utf8String)
  }