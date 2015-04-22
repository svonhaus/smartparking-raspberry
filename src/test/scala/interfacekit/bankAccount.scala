package actor

import akka.actor.{ActorSystem, Props, ActorRef, Actor}

object BankAccount
{
  case class Deposit(amount: BigInt)
  {
    require(amount > 0)
  }
  case class Withdraw(amount: BigInt)
  {
    require(amount > 0)
  }
  case object Done
  case object Failed
}

class BankAccount extends Actor
{
  import BankAccount._

  var balance:BigInt = 0

  def receive: Receive =
  {
    case Deposit(amount) =>
      balance += amount
      sender ! Done

    case Withdraw(amount) if amount <= balance =>
      balance -= amount
      sender ! Done

    case _ => sender ! Failed
  }
}

object WireTransfer
{
  case class Transfer(a: ActorRef, b: ActorRef, amount: Int)
  case object Done
  case object Failed
}

class WireTransfer extends Actor
{
  import WireTransfer._

  def receive :Receive =
  {
    case Transfer(from, to, amount) =>
      from ! BankAccount.Withdraw(amount)
      context.become(awaitFrom(to, amount, sender))
  }

  def awaitFrom(to: ActorRef, amount: Int, cl: ActorRef): Receive =
  {
    case BankAccount.Done =>
      to ! BankAccount.Deposit(amount)
      context.become(awaitTo(cl))

    case BankAccount.Failed =>
      cl ! Failed
      context.stop(self)
  }
  def awaitTo(client: ActorRef): Receive =
  {
    case BankAccount.Done =>
      client ! Done
      context.stop(self)
  }
}

class TransferMain extends Actor
{
  val accountA = context.actorOf(Props[BankAccount], "a")
  val accountB = context.actorOf(Props[BankAccount], "b")
  accountA ! BankAccount.Deposit(200)

  def receive: Receive =
  {
    case BankAccount.Done => transfer(150)
  }

  def transfer(amount: Int): Unit =
  {
    val transaction = context.actorOf(Props[WireTransfer], "transfer")
    transaction ! WireTransfer.Transfer(accountA, accountB, amount)

    context.become(
    {
      case WireTransfer.Done =>
        println("success")
        context.stop(self)

      case WireTransfer.Failed =>
        println("failed")
        context.stop(self)
    })
  }
}


object Main extends App
{
  val system = ActorSystem("CounterSystem")
  val master = system.actorOf(Props[TransferMain],"transfer")

  Thread.sleep(5000)
  /**
   * Ensure that the constructed ActorSystem is shut down when the JVM shuts down
   */
  sys.addShutdownHook(system.shutdown())
}

