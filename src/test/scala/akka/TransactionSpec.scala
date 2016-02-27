package akka

import BankAccount.{BankAccount, Transaction}
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest._

class TransactionSpec(_system: ActorSystem) extends TestKit(_system)
  with FlatSpecLike
  with ImplicitSender
  with Matchers
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("TransactionSpec"))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  it should "transfer money from A to B when there is enough funds on A" in {
    val accountA = TestProbe()
    val accountB = TestProbe()
    val transaction = system.actorOf(Props[Transaction])
    transaction ! Transaction.Transfer(accountA.ref, accountB.ref, 20)
    accountA.expectMsg(BankAccount.Withdraw(20))
    accountA.reply(BankAccount.Done)
    accountB.expectMsg(BankAccount.Deposit(20))
    accountB.reply(BankAccount.Done)
    expectMsg(Transaction.Done)
  }

  it should "not transfer money from A to B when there is not enough funds on A" in {
    val accountA = TestProbe()
    val accountB = TestProbe()
    val transaction = system.actorOf(Props[Transaction])
    transaction ! Transaction.Transfer(accountA.ref, accountB.ref, 20)
    accountA.expectMsg(BankAccount.Withdraw(20))
    accountA.reply(BankAccount.Failed)
    expectMsg(Transaction.Failed)
  }


}
