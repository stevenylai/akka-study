package akka

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.Greeter.Greet
import akka.Greeter.Greeted
import org.scalatest.wordspec.AnyWordSpecLike

class GreeterSuite extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  "A Greeter" must {
    "reply to greeted" in {
      val replyProbe = createTestProbe[Greeted]()
      val underTest = spawn(Greeter())
      underTest ! Greet("Santa", replyProbe.ref)
      replyProbe.expectMessage(Greeted("Santa", underTest.ref))
    }
  }

}
