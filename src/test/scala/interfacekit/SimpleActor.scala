package interfacekit

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

// (1) changed the constructor here
class SimpleActor(myName: String) extends Actor {
  def receive = {
    // (2) changed these println statements
    case "hello" => println("hello from %s".format(myName))
    case _       => println("'huh?', said %s".format(myName))
  }
}

object Main extends App {
  val system = ActorSystem("HelloSystem")
  // (3) changed this line of code
  val helloActor = system.actorOf(Props(new SimpleActor("Fred")), name = "helloactor")
  helloActor ! "hello"
  helloActor ! "buenos dias"
  //system.shutdown()
}