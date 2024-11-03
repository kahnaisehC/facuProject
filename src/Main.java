import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.function.Function;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
class Torneo{
    public int id_torneo;
    public String nombre_torneo;
    public String equipo_ganador;
}
public class Main {
    public static void main(String[] args) {
        
        Scanner input = new Scanner(System.in);

        String url = "jdbc:postgresql://localhost/facuproj";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "Welcome4$");
        String menuPrincipal =
                        "===========================================\n" +
                        "BIENVENIDO AL GESTOR DE TORNEOS DE FUTBOL!\n" +
                        "===========================================\n" +
                        "Elija una opcion: \n" +
                        "1.Revisar lista de jugadores\n" +
                        "2.Revisar lista de equipos\n" +
                        "3.Revisar lista de partidos\n" +
                        "4.Revisar lista de torneos\n" +
                        "5.Crear un torneo\n" +
                        "6.Crear un partido\n" +
                        "0.Salir\n";


        try {
            Connection conn = DriverManager.getConnection(url, props);
            Statement st = conn.createStatement();

            System.out.println(menuPrincipal);
            String option = input.next();
            switch (option){
                // Revisar lista de jugadores
                case "1":{

                    break;
                }
                // Revisar lista de equipos
                case "2":{

                    break;
                }
                //Revisar lista de partidos
                case "3":{

                    break;
                }
                // Revisar lista de torneos
                case "4":{
                    ResultSet torneos = st.executeQuery("SELECT id_torneo, nombre, equipo_ganador FROM torneo");
                    while(torneos.next()){
                        Torneo torneo = new Torneo();
                        torneo.id_torneo = torneos.getInt("id_torneo");
                        torneo.nombre_torneo = torneos.getString("nombre");
                        torneo.equipo_ganador = torneos.getString("equipo_ganador");
                        System.out.printf("%d es el id del torneo\n %s es el nombre del torneo\n %s es el equipo ganador\n", torneo.id_torneo, torneo.nombre_torneo, torneo.equipo_ganador);
                    }
                    torneos.close();

                    break;
                }
                // Crear un torneo
                case "5":{

                    break;
                }
                // Crear un partido
                case "6":{

                    break;
                }
                // Salir del menu Principal
                case "0":{

                    break;
                }
                // Input no valido
                default:{

                }
            }
            st.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}