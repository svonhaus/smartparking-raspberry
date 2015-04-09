package view

object Main
{
  def main(args:Array[String])
  {
    val context = 0

    if(context == 0)
      new UserInterfaceDisplay().initialize()
    else
      new ConsoleDisplay().initialize()
  }
}
