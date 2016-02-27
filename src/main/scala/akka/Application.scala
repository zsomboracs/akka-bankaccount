package akka

import BankAccount.{BankAccount, Transaction}
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

object Application extends App {

  val system = ActorSystem("BankAccountActorSystem")

  val accountA = system.actorOf(Props[BankAccount], "accountA")
  val accountB = system.actorOf(Props[BankAccount], "accountB")

  implicit val timeout = Timeout(5 seconds)
  Await.result(accountA ? BankAccount.Deposit(100), timeout.duration)

  val transactor = system.actorOf(Props[Transaction], "transfer")
  println("Transfer from A to B: " + Await.result(transactor ? Transaction.Transfer(accountA, accountB, 70), timeout.duration))

  println("Account A balance: " + Await.result(accountA ? BankAccount.GetBalance, timeout.duration))
  println("Account B balance: " + Await.result(accountB ? BankAccount.GetBalance, timeout.duration))

  system.shutdown
  system.awaitTermination

}


