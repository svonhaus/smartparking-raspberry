package view

/**
 * Objet et méthode principale du projet permettant de lancer l'application en mode console ou fenêtre
 */
object Main
{
  def main(args:Array[String])
  {
    val context = 1

    if(context == 0)
      new UserInterfaceDisplay().initialize()
    else
      new ConsoleDisplay().initialize()
  }
}
