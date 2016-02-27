package traditional


object Application extends App {

  class BankAccount {

    private var balance = 0

    def deposit(amount: Int) = this.synchronized {
      if (amount > 0) balance += amount
    }

    def withdraw(amount: Int): Int = this.synchronized {
      if (0 < amount && amount <= balance) {
        balance -= amount
        balance
      } else throw new Error("insufficient funds")
    }

    def getBalance = this.synchronized {
      balance
    }
  }

  def transfer(from: BankAccount, to: BankAccount, amount: Int) = {
    from.synchronized {
      to.synchronized {
        from.withdraw(amount)
        to.deposit(amount)
      }
    }
  }

  val accountA = new BankAccount()
  val accountB = new BankAccount()

  accountA.deposit(20)
  transfer(accountA, accountB, 10)
  println("account A balance: " + accountA.getBalance)
  println("account B balance: " + accountB.getBalance)

}
