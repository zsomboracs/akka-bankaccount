package akka

import BankAccount.Client
import BankAccount.Client.TestTransfer
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._


object Application extends App {

  import system.dispatcher

  implicit val timeout = Timeout(5 seconds)

  val system = ActorSystem("BankAccountActorSystem")
  val client = system.actorOf(Props[Client], "client")
  val future = client ? TestTransfer
  future.map {
    result =>
      println(s"Result of TestTransfer: $result.")
      system.shutdown
  }

}
