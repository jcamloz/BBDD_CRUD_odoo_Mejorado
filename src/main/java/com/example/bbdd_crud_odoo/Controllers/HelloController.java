package com.example.bbdd_crud_odoo.Controllers;

import com.example.bbdd_crud_odoo.DAO.PerfilDao;
import com.example.bbdd_crud_odoo.HelloApplication;
import com.example.bbdd_crud_odoo.Interfaces.ApiService;
import com.example.bbdd_crud_odoo.modelos.Perfil;
import com.example.bbdd_crud_odoo.modelos.Perfiles;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Clase principal del programa.
 * Esta clase representa la ventana principal.
 * Los valores son:
 * - tbData: representa la tabla que muestra los datos.
 * - nombre: Nombre del usuario.
 * - duracion: Tiempo conectado en minutos.
 * - baseDatos: Indica a qué base de datos se conectó el usuario.
 * - fecha: Indica la fecha en la cual se conectó el usuario.
 * - edit: Representa al botón de Editar.
 * - delete: Representa al botón de Eliminar.
 * - clean: Representa al botón de Limpiar filtros.
 * - sql: Es el nombre de la columna de la tabla que representa a la base de datos.
 * - duration: Es el nombre de la columna de la tabla que representa a los minutos conectado.
 * - name: Es el nombre de la columna de la tabla que representa el nombre del usuario.
 * - id: Es el nombre de la columna de la tabla que representa el identificador de la conexión.
 * - createDate: Es el nombre de la columna de la tabla que representa la fecha de la conexión.
 * - apiService: Es el objeto que conectará con el servicio API.
 *
 * @author Javier Campoy Lozano
 */
public class HelloController {

  @FXML
  private TableView tbData;
  @FXML
  private TextField nombre;
  @FXML
  private TextField duracion;
  @FXML
  private TextField baseDatos;
  @FXML
  private DatePicker fecha;
  @FXML
  private Button edit;
  @FXML
  private Button delete;
  @FXML
  private Button clean;
  @FXML
  private TableColumn sql;
  @FXML
  private TableColumn duration;
  @FXML
  private TableColumn name;
  @FXML
  private TableColumn id;
  @FXML
  private TableColumn createDate;

  private ApiService apiService;

  /**
   * Método que realiza los bindings necesarios y establece el apiService.
   */
  public void initialize() {

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("http://localhost:8069/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    apiService = retrofit.create(ApiService.class);

    //Bind botones editar, borrar y limpiar para activarlos cuando sea necesario
    //EDIT Y DELETE se desactivan cuando no haya seleccionado un Item en la tabla
    edit.disableProperty().bind(tbData.getSelectionModel().selectedItemProperty().isNull());
    delete.disableProperty().bind(tbData.getSelectionModel().selectedItemProperty().isNull());
    //Si todos los campos están vacios (Y el campo de fecha no
    // contiene texto válido) se deshabilita el botón de borrar filtros
    clean.disableProperty().bind(Bindings.createBooleanBinding(
        () -> nombre.getText().trim().isEmpty() && baseDatos.getText().trim().isEmpty()
            && duracion.getText().trim().isEmpty() && fecha.getValue() == null,
        nombre.textProperty(), baseDatos.textProperty(), duracion.textProperty(),
        duracion.textProperty(), fecha.valueProperty()
    ));

    //Configuro las columnas para mostrar el valor de la propiedad indicada.
    id.setCellValueFactory(new PropertyValueFactory<>("id"));
    name.setCellValueFactory(new PropertyValueFactory<>("name"));
    sql.setCellValueFactory(new PropertyValueFactory<>("sql"));
    duration.setCellValueFactory(new PropertyValueFactory<>("duration"));
    createDate.setCellValueFactory(new PropertyValueFactory<>("createDate"));

    //Edito el funcionamiento del datepicker para fecha
    fecha.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.isEmpty()) {
        fecha.setValue(null);
      }
    });

    //Añado un Handler a la tabla para comprobar el doble clic
    tbData.setOnMouseClicked(event -> {
      //Verifico si se hizo doble clic
      if (event.getClickCount() == 2) {
        Perfil perfilSeleccionado = (Perfil) tbData.getSelectionModel().getSelectedItem();
        try {
          // Llama al método de onAddOrEditPressed con parámetro de entrada
          onAddOrEditPressed(new ActionEvent(tbData, null));
        } catch (IOException | SQLException e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Método que gestiona el clic en el botón de búsqueda.
   * Usa la interfaz APIService para obtener los datos.
   * Los filtros de datos se llevan a cabo aquí.
   *
   * @param actionEvent Objeto que permite la acción.
   */
  @FXML
  public void onSearchPressed(ActionEvent actionEvent) {
    //OBJETO JSON
    JsonArray fields = new JsonArray();
    fields.add("name");
    fields.add("sql");
    fields.add("create_date");
    fields.add("duration");

    JsonObject body = new JsonObject();
    body.add("fields", fields);
    //FIN OBJETO JSON

    Call<Perfiles> call = apiService.getPerfiles(PerfilDao.login,
        PerfilDao.password, PerfilDao.apiKey, body);

    call.enqueue(new Callback<Perfiles>() {
      @Override
      public void onResponse(Call<Perfiles> call, Response<Perfiles> response) {
        //Si se busca por una cosa u otra se va sumando al idFiltrado 1, 2, 3 o 4, generando así
        //casos únicos y pudiendo filtrarlos más adelante.
        int idFiltrado = 0;
        idFiltrado += nombre.getText().trim().isEmpty() ? 0 : 1;
        idFiltrado += fecha.getValue() == null ? 0 : 2;
        idFiltrado += (PerfilDao.isNumeric(duracion.getText()) ? 4 : 0);
        idFiltrado += baseDatos.getText().trim().isEmpty() ? 0 : 8;

        Perfiles perfiles = response.body();
        ObservableList<Perfil> datos = FXCollections.observableArrayList();

        //Filtra según los datos escritos en los filtros usando un ID de filtrado.
        //Para cada caso, editará una cosa u otra.
        //Es mejorable, pero funciona muy bien.
        for (Perfil perfil : perfiles.getPerfiles()) {
          switch (idFiltrado) {
            case 0 -> datos.add(perfil);
            case 1 -> {
              if (perfil.getName().equalsIgnoreCase(nombre.getText())) {
                datos.add(perfil);
              }
            }
            case 2 -> {
              if (perfil.getCreateDate().equals(fecha.getValue().toString())) {
                datos.add(perfil);
              }
            }
            case 3 -> {
              if (perfil.getCreateDate().equals(fecha.getValue().toString())
                  && perfil.getName().equalsIgnoreCase(nombre.getText())) {
                datos.add(perfil);
              }
            }
            case 4 -> {
              if (perfil.getDuration() == Double.parseDouble(duracion.getText())) {
                datos.add(perfil);
              }
            }
            case 5 -> {
              if (perfil.getName().equalsIgnoreCase(nombre.getText())
                  && perfil.getDuration() == Double.parseDouble(duracion.getText())) {
                datos.add(perfil);
              }
            }
            case 6 -> {
              if (perfil.getDuration() == Double.parseDouble(duracion.getText())
                  && perfil.getCreateDate().equals(fecha.getValue().toString())) {
                datos.add(perfil);
              }
            }
            case 7 -> {
              if (perfil.getDuration() == Double.parseDouble(duracion.getText())
                  && perfil.getCreateDate().equals(fecha.getValue().toString())
                  && perfil.getName().equalsIgnoreCase(nombre.getText())) {
                datos.add(perfil);
              }
            }
            case 8 -> {
              if (perfil.getSql().equalsIgnoreCase(baseDatos.getText())) {
                datos.add(perfil);
              }
            }
            case 9 -> {
              if (perfil.getSql().equalsIgnoreCase(baseDatos.getText())
                  && perfil.getName().equalsIgnoreCase(nombre.getText())) {
                datos.add(perfil);
              }
            }
            case 10 -> {
              if (perfil.getSql().equalsIgnoreCase(baseDatos.getText())
                  && perfil.getCreateDate().equals(fecha.getValue().toString())) {
                datos.add(perfil);
              }
            }
            case 11 -> {
              if (perfil.getSql().equalsIgnoreCase(baseDatos.getText())
                  && perfil.getCreateDate().equals(fecha.getValue().toString())
                  && perfil.getName().equalsIgnoreCase(nombre.getText())) {
                datos.add(perfil);
              }
            }
            case 12 -> {
              if (perfil.getSql().equalsIgnoreCase(baseDatos.getText())
                  && perfil.getDuration() == Double.parseDouble(duracion.getText())) {
                datos.add(perfil);
              }
            }
            case 13 -> {
              if (perfil.getSql().equalsIgnoreCase(baseDatos.getText())
                  && perfil.getDuration() == Double.parseDouble(duracion.getText())
                  && perfil.getName().equalsIgnoreCase(nombre.getText())) {
                datos.add(perfil);
              }
            }
            case 14 -> {
              if (perfil.getSql().equalsIgnoreCase(baseDatos.getText())
                  && perfil.getCreateDate().equals(fecha.getValue().toString())
                  && perfil.getDuration() == Double.parseDouble(duracion.getText())) {
                datos.add(perfil);
              }
            }
            case 15 -> {
              if (perfil.getSql().equalsIgnoreCase(baseDatos.getText())
                  && perfil.getName().equalsIgnoreCase(nombre.getText())
                  && perfil.getDuration() == Double.parseDouble(duracion.getText())
                  && perfil.getCreateDate().equals(fecha.getValue().toString())) {
                datos.add(perfil);
              }
            }
            default -> System.out.println("Error");
          }
        }

        System.out.println("GET{" + response.code() + ", " + response.message() + "}");

        Platform.runLater(() -> {
          tbData.setItems(datos);
        });
      }

      @Override
      public void onFailure(Call<Perfiles> call, Throwable t) {
        System.out.println(t.getMessage());
        Platform.runLater(() -> {
          Alert alert = new Alert(Alert.AlertType.WARNING);
          alert.setContentText("¡¡¡Error de conexión!!!");
          alert.showAndWait();
        });
      }
    });
  }

  /**
   * Este método se encarga de controlar el clic en el botón de añadir o editar.
   * Crea una ventana emergente referenciando al controlador SesionesAddController
     para escribir los datos a generar o sobreescribir. En este último caso, los
     datos del elemento seleccionado se pasarán por parámetro a dicho controlador.
   *
   * @param actionEvent Objeto que permite la acción.
   *
   * @throws IOException  Devuelve una excepción en caso de que no encuentre el fxml.
   *
   * @throws SQLException Excepción que controla errores al intentar añadir o editar un registro.
   */
  @FXML
  public void onAddOrEditPressed(ActionEvent actionEvent) throws IOException, SQLException {
    String btId;
    //Si la instancia del evento es un Botón, buscará su id,
    //si no, automáticamente le asigno el valor edit,
    //ya que lo único que llegará por parámetros será la tabla
    //Si el btId es null, significa que la ventana será para
    // crear un Perfil, si no, será para editarlo
    if (actionEvent.getTarget() instanceof Button) {
      btId = ((Button) actionEvent.getTarget()).getId();
    } else {
      btId = "edit";
    }

    //Cargo el FXML y creo la escena Modal sin posibilidad de aumentar o disminuir el tamaño
    FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("sesionesAdd.fxml"));
    Scene scene = new Scene(loader.load(), 400, 300);
    Stage stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setResizable(false);

    //Comprueba el valor de btId
    if (btId == null) {
      stage.setTitle("Nueva Sesión");
      stage.setScene(scene);
      stage.showAndWait();
      onCleanPressed(null);

    } else {
      //Obtengo el contenido de la fila seleccionada de la tabla y hago un cast al tipo "Perfil"
      Perfil perfilSeleccionado = (Perfil) tbData.getSelectionModel().getSelectedItem();

      //Creo una instancia del controlador secundario "SesionesAddController" y cargo el FXML
      SesionesAddController controller2 = loader.getController();
      //Llamo al método del controlador secundario "getDatos()" y le paso por parámetro
      // el valor de la tabla seleccionada en ese instante
      controller2.getDatos(perfilSeleccionado);

      stage.setTitle("Modificar Sesión");
      stage.setScene(scene);
      stage.showAndWait();
      //Quito la selección de dicha fila de la tabla
      tbData.getSelectionModel().clearSelection();

    }
  }

  /**
   * Este método controla el clic sobre el botón de borrar.
   * Se encarga de borrar el elemento de la tabla seleccionado.
   *
   *  @param actionEvent Objeto que permite la acción.
   *
   *  @throws SQLException Devuelve una excepción de tipo SQL
      en caso de error al borrar un registro.
   */

  @FXML
  public void onDeletePressed(ActionEvent actionEvent) throws SQLException {
    //Obtengo el contenido de la fila seleccionada de la tabla y hago un cast al tipo "Perfil"
    Perfil perfilSeleccionado = (Perfil) tbData.getSelectionModel().getSelectedItem();

    //Creo un alert de tipo confirmación y reviso que el botón de
    // confirmación se ha pulsado para borrar
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
        "¿Está seguro de que quiere borrar al usuario \"" + perfilSeleccionado.getName() + "\"?");
    getCssAlert(alert);

    Optional<ButtonType> result = alert.showAndWait();
    //Si el botón que se ha pulsado tiene texto y es
    // un botón de cofirmación se eliminará el registro
    if (result.isPresent() && result.get() == ButtonType.OK) {
      Call<Perfiles> call = apiService.deletePerfiles(perfilSeleccionado.getId(),
          PerfilDao.login, PerfilDao.password, PerfilDao.apiKey);

      call.enqueue(new Callback<Perfiles>() {
        @Override
        public void onResponse(Call<Perfiles> call, Response<Perfiles> response) {
          System.out.println("DELETE{" + response.code() + ", " + response.message() + "}");
          Platform.runLater(() -> {
            onSearchPressed(null);
          });
        }

        @Override
        public void onFailure(Call<Perfiles> call, Throwable t) {
          System.out.println(t.getMessage());
          Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("¡¡¡Error de conexión!!!");
            alert.showAndWait();
          });
        }
      });
    }
  }

  /**
   * Sirve para crear y mostrar alertas simples.
   *
   * @param alertType Recibe el tipo de alerta (confirmación, aviso, etc.).
   *
   * @param title Recibe un String que indicará el título de la alerta.
   *
   * @param header Recibe un String que indicará la cabecera de la alerta.
   *
   * @param content Recibe un String que indicará el mensaje de la alerta.
   */
  public void showAlert(Alert.AlertType alertType, String title, String header, String content) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);

    getCssAlert(alert);

    alert.showAndWait();
  }

  //

  /**
   * Sirve para asignar el css a los alerts, ya que al ser
   * una ventana aparte, no cogerán el css asignado al Controller.
   *
   * @param alert Recibe el objeto de ventana de alerta para asignarle el css.
   */
  public void getCssAlert(Alert alert) {
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.getStylesheets().add(getClass().getResource(
        "/com/example/bbdd_crud_odoo/CSS/styles.css").toExternalForm());
    dialogPane.getStyleClass().add("custom-alert"); // Añado una clase personalizada
  }

  /**
   * Este método borra el contenido de los filtros.
   *
   * @param actionEvent Objeto que permite la acción.
   */
  @FXML
  public void onCleanPressed(ActionEvent actionEvent) {
    nombre.clear();
    duracion.clear();
    baseDatos.clear();
    fecha.setValue(null);
  }
}