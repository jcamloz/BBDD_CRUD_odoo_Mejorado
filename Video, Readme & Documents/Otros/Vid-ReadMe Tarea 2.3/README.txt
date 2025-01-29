Javier Campoy Lozano		2ºDAM

      --DESARROLLO DE INTERFACES--

-------FUNCIONAMIENTO BÁSICO
El programa genera una ventana con varios TextFields que invitarán al usuario a insertar datos que serán usados como filtros para la búsqueda en la base de datos. 

Hay 5 botones. 2 de búsqueda, uno que permite búsqueda sin Inyección SQL y otro que sí lo permite, 1 de añadir usuario y editar, ambos llaman a una misma ventana, 
con la diferencia que el de editar envía a través de un método del controlador secundario información, y por eso sabrá que debe editar, y un botón de eliminar.
 
Para editar y eliminar es imprescindible seleccionar una fila en la tabla. Tras añadir, eliminar o modificar se realizará una búsqueda con los datos que habían 
en ese momento en los TextField en un intento de actualizar la tabla.

La base de datos usa id auto-incrementales, por lo que será más sencillo trabajar con ellos.
Esto, en mi caso, ya que no necesito acceder otras bases de datos y añadir o modificar datos a partir de el id del elemento que sea requerido para la tabla principal.


Todos los campos son opcionales, pero el tiempo de conexión es imprescindible. Por eso, si se intenta añadir o modificar un perfil de manera que quede sin ese campo rellenado correctamente, 
mediante el uso de alert, mostraré un mensaje que notifique de ello al usuario.

Si al editar o borrar no hay seleccionada ninguna fila de la tabla, notificaré de ello con un alert.

Cuando se intenta borrar un elemento, se pedirá confirmación a través de un alert. Si se selecciona “Aceptar”, se procederá con la eliminación.

Si al realizar una consulta con la Inyección SQL algo sale mal, un alert mostrará un mensaje de error que muestre a que se debe dicho error.

-------MÉTODOS DE DESARROLLO
Uso filtros, Sobre-escritura y Handler y onAction para llevar a cabo esta tarea.
Uso de hilo y run later para la lectura en la base de datos, ya que es una tarea que puede tardar mucho más que eliminar, por ejemplo.

Uso la estructura MVC, creando en resources los fxml, imágenes usadas y Css, y en main/java carpetas para los modelos, los DAO y los controllers usados.

-------CSS
Uso un mismo Css para todos.
Uso de un identificador para el Text que hace de título para darle estilo en el Css.
Uso de clases generales para dar estilo a todos los elementos visuales.

-------EXTRA
Para ejecutar el programa no será necesario tener ningún dato en la base de datos.
La base de datos soporta nulos, sin embargo, asignaré valores automáticos en caso de que no se decida asignar ninguno.
Para llevar a cabo esta tarea he usado el archivo yml de docker antiguo. Está adjunto en la raíz del proyecto.