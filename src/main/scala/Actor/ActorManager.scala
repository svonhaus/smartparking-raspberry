package Actor

import akka.actor.{Props, ActorSystem}
import view.UtilConsole

/**
 * Created by Steven on 25-04-15.
 */
object ActorManager
{
  //val MagneticActor = ActorSystem("MagneticActor").actorOf
  val actorSystem = ActorSystem("mySystem")
  val tempListenerActor = actorSystem.actorOf(Props[TempListenerActor])
  val tempCheckingActor = actorSystem.actorOf(Props[TempCheckingActor])
  val iRListenerActor = actorSystem.actorOf(Props[IRListenerActor])
  val iRCheckingActor = actorSystem.actorOf(Props[IRCheckingActor])
  val vibrationCheckingActor = actorSystem.actorOf(Props[VibrationCheckingActor])
  val vibrationListenerActor = actorSystem.actorOf(Props[VibrationListenerActor])
  val touchListenerActor = actorSystem.actorOf(Props[TouchListenerActor])
  val waitCarToPassActor = actorSystem.actorOf(Props[ManagerSharpSensorActor])

  def initialize() =
  {
    UtilConsole.showMessage("Actors have been initialized", getClass.getName, "INFORMATION_MESSAGE")
  }
}
