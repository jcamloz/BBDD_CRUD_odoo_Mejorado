package com.example.bbdd_crud_odoo.DAO;

/**
 * Clase que gestiona datos del login a odoo y funciones de utilidad.
 *
 * @author Javier Campoy Lozano
 */
public class PerfilDao {
  public static final String login = "jcamloz553@g.educaand.es";
  public static final String password = "odoo";
  public static final String apiKey = "42ab1e1d-bbcb-4ac8-9435-fabe2b3d4d0c";

  /**
   * Método estático que devuelve un booleano.
   * true si la cadena es un número o false en caso contrario.
   *
   * @param str Recibe una cadena para indicar si es un número o no.
   *
   * @return Devuelve un booleano para indicar si es un número o no.
   */
  public static boolean isNumeric(String str) {
    //Esta función devuelve true si el String puede ser "casteado" a Double, si no, devolverá false
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
