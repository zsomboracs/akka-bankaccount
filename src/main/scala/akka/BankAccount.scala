package BankAccount

import akka.actor.Actor


object BankAccount {

  case class Deposit(amount: Int) {
    require(amount > 0)
  }

  case class Withdraw(amount: Int) {
    require(amount > 0)
  }

  case class Balance(balance: Int)

  case object GetBalance

  case object Done

  case object Failed

}

class BankAccount extends Actor {

  import BankAccount._

  var balance = 0

  def receive() : Receive = {

    case GetBalance => sender ! Balance(balance)

    case Deposit(amount) =>
      balance += amount
      sender ! Done

    case Withdraw(amount) if amount <= balance =>
      balance -= amount
      sender ! Done

    case _ => sender ! Failed
  }

}
