package model

/**
 * Modèle permettant le stockage temporaires des informations d'un utilisateur.
 * @param id : l'identifiant de l'utilisateur
 * @param firstName : le prénom de l'utilisateur
 * @param lastName : le nom de l'utilisateur
 * @param mail : l'adresse e-mail de l'utilisateur
 * @param inTheParking : true si l'utilisateur est dans le parking, false sinon
 */
class Person(val id:String, val firstName: String, val lastName: String, val mail: String, val inTheParking : Boolean)
{
  def this(firstName: String, lastName: String, mail: String, inTheParking : Boolean) {this(null, firstName, lastName, mail, inTheParking)}
}