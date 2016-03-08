package akka

import BankAccount.BankAccount
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest._

class BankAccountSpec(_system: ActorSystem) extends TestKit(_system)
  with FlatSpecLike
  with ImplicitSender
  with Matchers
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("BankAccountSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  it should "has 0 balance by default" in {
    val bankAccount = system.actorOf(Props[BankAccount])
    bankAccount ! BankAccount.GetBalance
    expectMsg(BankAccount.Balance(0))
  }

  it should "has deposit amount as balance" in {
    val bankAccount = system.actorOf(Props[BankAccount])
    deposit(bankAccount, 10)
    bankAccount ! BankAccount.GetBalance
    expectMsg(BankAccount.Balance(10))
  }

  it should "has reduced balance after withdraw in case of sufficient funds" in {
    val bankAccount = system.actorOf(Props[BankAccount])
    deposit(bankAccount, 10)
    bankAccount ! BankAccount.Withdraw(5)
    expectMsg(BankAccount.Done)
    bankAccount ! BankAccount.GetBalance
    expectMsg(BankAccount.Balance(5))
  }

  it should "should fail withdraw in case of insufficient funds" in {
    val bankAccount = system.actorOf(Props[BankAccount])
    bankAccount ! BankAccount.Withdraw(5)
    expectMsg(BankAccount.Failed)
  }


  def deposit(bankAccount: ActorRef, amount: Int) = {
    bankAccount ! BankAccount.Deposit(amount)
    expectMsg(BankAccount.Done)
  }


}
