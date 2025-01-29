package com.example.bbdd_crud_odoo.Interfaces;

import com.example.bbdd_crud_odoo.modelos.Perfiles;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Interfaz para manejar datos de una base de datos a través de una REST API.
 *
 * @author Javier Campoy Lozano
 */
public interface ApiService {

  /**
   * Este método usa @HTTP para obtener el cuerpo del json y así rescatar sus datos.
   *
   * @param login Recibe un usuario para iniciar sesión en odoo.
   *
   * @param password Recibe el password del usuario para iniciar sesión.
   *
   * @param apiKey Es necesaria la clave API para obtener los datos.
   *
   * @param body Recibe un cuerpo en Json donde rescatar los datos solicitados en este.
   *
   * @return .
   */
  @HTTP(method = "get", path = "send_request?model=ir.profile", hasBody = true)
  Call<Perfiles> getPerfiles(@Header("login") String login,
                             @Header("password") String password,
                             @Header("api-key") String apiKey,
                             @Body JsonObject body);

  /**
   * Este método elimina el elemento seleccionado por su id.
   *
   * @param id Solicita el ID del elemento a borrar.
   *
   * @param login Solicita el usuario con el que iniciar sesión en odoo.
   *
   * @param password Solicita el password del usuario.
   *
   * @param apiKey Solicita la clave API.
   *
   * @return .
   */
  @DELETE("send_request?model=ir.profile")
  Call<Perfiles> deletePerfiles(@Query("Id") int id,
                                @Header("login") String login,
                                @Header("password") String password,
                                @Header("api-key") String apiKey);

  /**
   * Este método actualiza el elemento seleccionado.
   *
   * @param id Solicita el ID del elemento a editar.
   *
   * @param login Solicita el usuario con el que iniciar sesión en odoo.
   *
   * @param password Solicita el password del usuario.
   *
   * @param apiKey Solicita la clave API.
   *
   * @param body Recibe un cuerpo en Json que obtiene los datos a modificar.
   *
   * @return .
   */
  @PUT("send_request?model=ir.profile")
  Call<Perfiles> putPerfiles(@Query("Id") int id,
                             @Header("login") String login,
                             @Header("password") String password,
                             @Header("api-key") String apiKey,
                             @Body JsonObject body);

  /**
   * Este método crea un nuevo elemento y lo añade a la base de datos.
   *
   * @param login Solicita el usuario con el que iniciar sesión en odoo.
   *
   * @param password Solicita el password del usuario.
   *
   * @param apiKey Solicita la clave API.
   *
   * @param body Recibe un cuerpo en Json que obtiene los datos del elemento a añadir.
   *
   * @return .
   */
  @POST("send_request?model=ir.profile")
  Call<Perfiles> postPerfiles(@Header("login") String login,
                              @Header("password") String password,
                              @Header("api-key") String apiKey,
                              @Body JsonObject body);
}
