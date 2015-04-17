package controller

/**
 * Created by Steven on 18-03-15.
 */
object Sensors
{
  val functionSharpSensor = (x:Int) =>
  {
    if(x!=20)
    {
      val result = 4800 / (x - 20)
      if(result > 10 && result < 100) Some(result) else None
    }
    else None
  }

  val functionIrSensor = (x:Int) =>
  {
    if(x < 500) Some(1) else Some(0)
  }

  val functionTouchSensor = (x: Int) =>
  {
    if(x>0) Some(x) else None
  }

  val functionTempSensor = (x: Int) =>
  {
    Some(((x*0.2222) - 61.111).toInt)
  }

  val listAnalogSensors= List(
    (0, (((x:Int) => functionSharpSensor(x)), (3, 200.toLong))), // (indexSensor, (functionToComputeAnalogValue, (treshold, interval)))
    (1, (((x:Int) => functionSharpSensor(x)), (3, 200.toLong))),
    (2, (((x:Int) => functionTempSensor(x)), (3, 200.toLong))),
    (3, (((x:Int) => functionTouchSensor(x)), (3, 200.toLong))),
    (4, (((x:Int) => functionTouchSensor(x)), (3, 200.toLong))),
    (5, (((x:Int) => functionTouchSensor(x)), (3, 200.toLong))))
}
