package model

class Person(val id:String, val firstName: String, val lastName: String, val mail: String, val inTheParking : Boolean)
{
  def this(firstName: String, lastName: String, mail: String, inTheParking : Boolean) {this(null, firstName, lastName, mail, inTheParking)}
}