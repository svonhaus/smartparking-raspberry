package model

class Person(val id:String, val firstName: String, val lastName: String, val mail: String) 
{
  def this(firstName: String, lastName: String, mail: String) {this(null, firstName, lastName, mail)}
}