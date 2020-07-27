import akka.stream._
import akka.stream.scaladsl._

import akka.{ Done, NotUsed }
import akka.actor.ActorSystem
import akka.util.ByteString
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.CanAwait
import java.nio.file.Paths

object Stream extends App {
  implicit val system = ActorSystem("QuickStart")

  val sink = Sink.fold[Int, Int](0)(_ + _)
  val runnable: RunnableGraph[Future[Int]] =
    Source(1 to 10).toMat(sink)(Keep.right)

  // get the materialized value of the FoldSink
  val sum1: Future[Int] = runnable.run()
  val sum2: Future[Int] = runnable.run()

  println(Await.result(sum1, 1.second) + " " + Await.result(sum2, 1.second))
}