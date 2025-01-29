package com.example.bbdd_crud_odoo.modelos;

import java.util.ArrayList;

/**
 * Clase que se ajusta a los datos que devuelve la interfaz ApiService,
 * en este caso, un ArrayList de Perfil.
 *
 * @author Javier Campoy Lozano
 */
public class Perfiles {
  public ArrayList<Perfil> records;

  public ArrayList<Perfil> getPerfiles() {
    return records;
  }
}
