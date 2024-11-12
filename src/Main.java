import java.sql.*;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
class Torneo{
    public int id_torneo;
    public String nombre_torneo;
    public String equipo_ganador;
}
class Equipo{
    public String nombre;
}
class Partido{
    public int id_partido;
    public String equipo1;
    public String equipo2;
    public int id_partido_torneo;
    public String equipo_ganador;
}
public class Main {
    static boolean isNumeric(String str){
        for(int i =0; i < str.length(); i++){
            if(str.charAt(i) < '0' || str.charAt(i) > '9')
                return false;
        }
        return true;
    }

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
            String option;
            do{
                System.out.println(menuPrincipal);
                option = input.next();
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
                    // Crear un partido
                    case "4":{
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
            }while(!option.equals("0"));
            input.close();
            conn.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}