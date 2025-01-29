package com.example.bbdd_crud_odoo;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase que ejecuta el programa.
 *
 * @author Javier Campoy Lozano
 */
public class HelloApplication extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("principal.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 1041, 950);
    scene.getStylesheets().add(getClass().getResource("CSS/styles.css").toExternalForm());
    stage.setTitle("Conexión a Base de datos de sesiones");
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  /**
   * Clase main; lleva a cabo la ejecución del programa.
   *
   * @param args Son los posibles argumentos de entrada.
   */
  public static void main(String[] args) {
    launch();
  }
}