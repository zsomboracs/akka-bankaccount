package BankAccount

import akka.actor.{Actor, ActorRef}

object Transaction {

  case class Transfer(from: ActorRef, to: ActorRef, amount: Int)

  case object Done

  case object Failed

}

class Transaction extends Actor {

  import Transaction._

  def receive = {
    case Transfer(from, to, amount) =>
      from ! BankAccount.Withdraw(amount)
      context.become(awaitFrom(to, amount, sender))
  }

  def awaitFrom(to: ActorRef, amount: Int, customer: ActorRef): Receive = {
    case BankAccount.Done =>
      to ! BankAccount.Deposit(amount)
      context.become(awaitTo(customer))
    case BankAccount.Failed =>
      customer ! Failed
      context.stop(self)
  }

  def awaitTo(customer: ActorRef): Receive = {
    case BankAccount.Done =>
      customer ! Done
      context.stop(self)
  }
}
