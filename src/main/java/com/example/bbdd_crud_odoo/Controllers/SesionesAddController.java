package com.example.bbdd_crud_odoo.Controllers;

import com.example.bbdd_crud_odoo.DAO.PerfilDao;
import com.example.bbdd_crud_odoo.Interfaces.ApiService;
import com.example.bbdd_crud_odoo.modelos.Perfil;
import com.example.bbdd_crud_odoo.modelos.Perfiles;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.time.LocalDate;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Esta es la clase principal de la ventana secundaria.
 * Su función principal es crear o editar registros.
 * Al inicio de esta se declaran los componentes necesarios.
 * Estos son:
 * - cancel: Representa el botón de cancelar.
 * - acept: Representa el botón de aceptar.
 * - vb1: Es el componente contenedor padre VBox.
 * - button: Es una cadena de texto que guardará el valor del texto de cada botón,
 * de forma que se sabrá si es el de aceptar o cancelar.
 * - fecha: Representa la fecha de conexión a la base de datos.
 * - duracion: Representa el tiempo de conexión en minutos.
 * - nombre: Representa el nombre del usuario.
 * - id: Representa el ID de la conexión. Si es 0, se está creando un nuevo registro,
 * si no, se está editando uno.
 * - baseDatos: Representa la base de datos a la que se conectó el usuario.
 * - apiService: Es el objeto que conectará con el servicio API.
 *
 * @author Javier Campoy Lozano
 */
public class SesionesAddController {
  @FXML
  private Button cancel;
  @FXML
  private Button acept;
  @FXML
  private VBox vb1;

  private String button;
  @FXML
  private DatePicker fecha;
  @FXML
  private TextField duracion;
  @FXML
  private TextField nombre;
  @FXML
  private TextField baseDatos;

  int id;
  ApiService apiService;
  @FXML
  private Text title;


  /**
   * Esta función recogerá los datos de un perfil,
   * en concreto, los pasados por la ventana principal.
   * Se controla si es null.
   *
   * @param perfil Recoge los datos del perfil y los carga.
   */
  public void getDatos(Perfil perfil) {
    if (perfil != null) {
      id = perfil.getId();
      nombre.setText(perfil.getName() == null ? "" : perfil.getName());
      duracion.setText(String.valueOf(perfil.getDuration()));
      baseDatos.setText(perfil.getSql() == null ? "" : perfil.getSql());
      fecha.setValue(perfil.getCreateDate() == null
          ? null : LocalDate.parse(perfil.getCreateDate()));
      title.setText("Actualizar Datos");
    }
  }

  /**
   * Método de inicio que inicia el servicio API y realiza los bindings.
   */
  public void initialize() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://localhost:8069/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    apiService = retrofit.create(ApiService.class);

    //Es imprescindible que el campo de Tiempo de conexión no sea null y además sea numérico
    //Uso la función de PerfilDAO isNumeric para comprobar si se puede hacer un cast de tipo Double
    acept.disableProperty().bind(Bindings.createBooleanBinding(
        () -> duracion.getText().trim().isEmpty() || !PerfilDao.isNumeric(duracion.getText())
            || nombre.getText().trim().isEmpty() || baseDatos.getText().trim().isEmpty()
            || fecha.getValue() == null || fecha.getEditor().getText().trim().isEmpty(),
        duracion.textProperty(), nombre.textProperty(), baseDatos.textProperty(),
        fecha.valueProperty(), fecha.getEditor().textProperty()
    ));

    //Asigno el css obteniendo la ruta del Vbox raíz
    vb1.getStylesheets().add(getClass().getResource(
        "/com/example/bbdd_crud_odoo/CSS/styles.css").toExternalForm());
    //Edito el funcionamiento del datepicker para fecha
    fecha.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.isEmpty()) {
        fecha.setValue(null);
      }
    });
  }

  /**
   * Comprueba el clic en el botón de Aceptar o Cancelar.
   * Dependiendo de cuál se pulse, hará una cosa u otra.
   * Si se está editando, Aceptar modifica el registro.
   * Si se está creando, Aceptar creará el registro.
   *
   * @param actionEvent Objeto que permite la acción.
   */
  @FXML
  public void cancelOrAceptPressed(ActionEvent actionEvent) {
    Stage stage = (Stage) ((Scene) ((Node) actionEvent.getSource()).getScene()).getWindow();
    button = ((Button) actionEvent.getSource()).getText();
    if (button.equals("ACEPTAR")) {
      System.out.println("OK");

      //OBJETO JSON
      JsonObject values = new JsonObject();
      values.addProperty("name", nombre.getText());
      values.addProperty("sql", baseDatos.getText());
      values.addProperty("create_date", fecha.getValue().toString());
      values.addProperty("duration", duracion.getText());
      JsonArray fields = new JsonArray();
      fields.add("name");
      fields.add("sql");
      fields.add("create_date");
      fields.add("duration");
      JsonObject body = new JsonObject();
      body.add("fields", fields);
      body.add("values", values);
      //FIN OBJETO JSON

      if (id > 0) {
        //Si el ID del Perfil es 0 significa que se está creando,
        // ya que en la base de datos se usa el autoincremento
        //y, por lo tanto, un perfil existente siempre será mayor que 0
        Call<Perfiles> call = apiService.putPerfiles(
            id, PerfilDao.login, PerfilDao.password, PerfilDao.apiKey, body);

        call.enqueue(new Callback<Perfiles>() {
          @Override
          public void onResponse(Call<Perfiles> call, Response<Perfiles> response) {
            System.out.println("PUT{" + response.code() + ", " + response.message() + "}");
          }

          @Override
          public void onFailure(Call<Perfiles> call, Throwable t) {
            //System.err.println(t.getMessage());
                        /*
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setContentText("¡¡¡Error de conexión!!!");
                            alert.showAndWait();
                        });*/
          }
        });
      } else {

        Call<Perfiles> call = apiService.postPerfiles(
            PerfilDao.login, PerfilDao.password, PerfilDao.apiKey, body);

        call.enqueue(new Callback<Perfiles>() {
          @Override
          public void onResponse(Call<Perfiles> call, Response<Perfiles> response) {
            System.out.println("POST{" + response.code() + ", " + response.message() + "}");
          }

          @Override
          public void onFailure(Call<Perfiles> call, Throwable t) {
            //System.err.println(t.getMessage());
                        /*
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setContentText("¡¡¡Error de conexión!!!");
                            alert.showAndWait();
                        });*/
          }
        });
      }
      //Una vez hecha la acción de añadir o modificar, cierro la ventana
      stage.close();
    } else if (button.equals("CANCELAR")) {
      //En caso de que el botón pulsado sea el de cancelar, se cierra la ventana
      stage.close();
    }
  }
}
