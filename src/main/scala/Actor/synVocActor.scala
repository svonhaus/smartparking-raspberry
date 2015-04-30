package Actor

import akka.actor.Actor
import config.{Config, MyProperties}
import data.DataAdd
import t2s.son.LecteurTexte
import view.UtilConsole

import scala.util.{Failure, Success}

/**
 * Actor pour une synthèse vocale alertant du danger dans le parking
 */
class synVocActor extends Actor {
  //Classe de synthèse vocale

  private val Syn_vocale_temp=new String("Attention, attention! La température est devenue trop élevée et peut provoquer des dommages chez certaines personnes...")
  private val Syn_vocale_vib="Attention, attention! Le sol vibre de façon anormale. Un tremblement de terre est imminant..."
  private val Syn_vocale_in="Smartking vous souhaite la bienvenue"
  private val Syn_vocale_out="Smartking vous remercie! Bonne route et à bientôt"
  private val lt = new LecteurTexte()

  def receive = {
    case value: Double => {
      UtilConsole.showMessage("Attention " + value, getClass.getName, "INFORMATION_MESSAGE")

      this.lt.setTexte(Syn_vocale_vib)
      this.lt.playAll()
    }

    case _  => UtilConsole.showMessage("Actor error", "Problem from" + getClass.getName, "ERROR_MESSAGE")
  }

}
