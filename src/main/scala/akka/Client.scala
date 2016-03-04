package BankAccount

import BankAccount.{Balance, GetBalance}
import Client.{TestDone, TestTransfer}
import WireTransfer.Transfer
import akka.actor.{ActorRef, Actor, Props}

object Client {

  case object TestTransfer

  case object TestDone

}

class Client extends Actor {

  var originalSender = ActorRef.noSender

  val hcomAccount = context.actorOf(Props[BankAccount], "hcomAcc")
  val epamAccount = context.actorOf(Props[BankAccount], "epamAcc")


  def receive = {
    case TestTransfer =>
      originalSender = sender
      context.become(awaitDeposit)
      hcomAccount ! BankAccount.Deposit(177000)
  }

  def awaitDeposit(): Receive = {
    case BankAccount.Done =>
      println("Deposit done.")
      val transfer = context.actorOf(Props[WireTransfer], "transfer")
      context.become(awaitTransfer)
      transfer ! Transfer(hcomAccount, epamAccount, 60000)
  }

  def awaitTransfer(): Receive = {
    case WireTransfer.Done =>
      println("Transfer succeeded.")
      context.become(awaitBalance(2))
      hcomAccount ! GetBalance
      epamAccount ! GetBalance
  }

  def awaitBalance(balanceNr: Int): Receive = {
    case Balance(balance) =>
      println(s"Balance of $sender: $balance.")
      val balanceNrLeft = balanceNr - 1
      if (balanceNrLeft == 0)
        originalSender ! TestDone
      else
        context.become(awaitBalance(balanceNrLeft))
  }
}
