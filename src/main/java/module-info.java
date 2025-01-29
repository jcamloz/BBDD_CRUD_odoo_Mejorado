module com.example.bbdd_crud_odoo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive retrofit2.converter.gson;
    requires transitive retrofit2;
    requires com.google.gson;


  opens com.example.bbdd_crud_odoo to javafx.fxml;
    exports com.example.bbdd_crud_odoo;
    exports com.example.bbdd_crud_odoo.Controllers;
    exports com.example.bbdd_crud_odoo.DAO;
    exports com.example.bbdd_crud_odoo.modelos;
    opens com.example.bbdd_crud_odoo.Controllers to javafx.fxml;
    opens com.example.bbdd_crud_odoo.modelos to javafx.base;
    opens com.example.bbdd_crud_odoo.DAO to javafx.fxml;
}