package com.example.bbdd_crud_odoo.modelos;

/**
 * Clase que actua de modelo de objeto, en este caso de un perfil de usuario.
 * Contiene las variables que representan los valores necesarios y sus métodos
 * para modificar o recibir sus valores.
 *
 * @author Javier Campoy Lozano
 */
public class Perfil {
  public int id;
  public String name;
  public String sql;

  //Como no me ha sido posible arreglar esto sin modificar la base de datos,
  //impido que muestre el aviso.
  @SuppressWarnings("checkstyle:MemberName")
  public String create_date;

  public double duration;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public String getCreateDate() {
    return create_date.substring(0, 10);
  }

  public void setCreateDate(String createDate) {
    this.create_date = createDate;
  }

  public double getDuration() {
    return duration;
  }

  public void setDuration(double duration) {
    this.duration = duration;
  }
}