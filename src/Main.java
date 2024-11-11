import javax.swing.plaf.nimbus.State;
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
    public static void menuVerTorneos(Connection conn, Scanner input){
        System.out.println("Ingrese '0' para volver al menu principal");
        System.out.println("Ingrese el tipo de torneo que quiere listar");
        System.out.println("1. Torneos por jugarse");
        System.out.println("2. Torneos en juego");
        System.out.println("3. Torneos jugados");
        String option = input.next();

    }

    // TODO: consider using PREPARE to avoid SQLI
    public static void menuInsertarTorneo(Connection conn, Scanner input){
        // Ask user name of the tournament
        String tournamentName;
        Set<String> equiposDentro = new HashSet<>();
        while(true){
            System.out.println("Ingrese '0' para volver al menu principal");
            System.out.println("Ingrese el nombre del torneo");
            tournamentName = input.next();
            if(tournamentName.equals("0")){
                return;
            }
            Statement st;
            try{
                st = conn.createStatement();
            }catch (Exception e){
                System.out.println("Error conectando a la base de Datos");
                return;
            }
            try {
                ResultSet rs = st.executeQuery(String.format("SELECT COUNT(*) FROM torneo WHERE nombre='%s'", tournamentName));
                rs.next();
                if(rs.getInt("count") != 0){
                    System.out.println("Ya hay un torneo con ese nombre!");
                    continue;
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
                return;
            }
            // Seleccionar los equipos
            // Equipos no en el torneo || equipos ya en el torneo || volver hacia atras
            Set<String> equiposFuera = new HashSet<>();
            try{
                ResultSet equiposQuery = st.executeQuery("SELECT * FROM equipo WHERE nombre != 'bye'");
                while(equiposQuery.next()){
                    equiposFuera.add(equiposQuery.getString("nombre"));
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
                return;
            }
            while(true){
                System.out.println("Ingrese '0' para volver al menu principal");
                System.out.println("Ingrese '1' para confirmar equipos (se pueden anadir mas mas tarde");

                if(equiposFuera.size() != 0)
                    System.out.println("Equipos fuera del torneo. Escriba el nombre del equipo para anadirlo al torneo");
                for(String equipo : equiposFuera){
                    System.out.println(equipo);
                }
                if(equiposDentro.size() != 0)
                    System.out.println("Equipos en el torneo. Escriba el nombre del equipo para quitarlo del torneo");
                for(String equipo : equiposDentro){
                    System.out.println(equipo);
                }
                String option = input.next();
                if(option.equals("0")){
                    System.out.println("Cambios eliminados. Volviendo a menu principal");
                    return;
                }
                if(option.equals("1")){
                    if(equiposDentro.size() < 2){
                        System.out.println("Ingrese al menos dos equipos");
                        continue;
                    }
                    break;
                }
                if(equiposDentro.contains(option)){
                    equiposFuera.add(option);
                    equiposDentro.remove(option);
                }else if (equiposFuera.contains(option)){
                    equiposFuera.remove(option);
                    equiposDentro.add(option);
                }else{
                    System.out.println("No existe ningun equipo con ese nombre!!!");
                }

            }
            break;
        }
        insertarTorneo(conn, equiposDentro, tournamentName);
    }
    public static void insertarTorneo(Connection conn, Set<String> equipos, String tournamentName){
        try{
            Statement st = conn.createStatement();
            int id_torneo;
            st.executeUpdate(String.format("INSERT INTO torneo(nombre) VALUES('%s')", tournamentName));
            ResultSet query = st.executeQuery(String.format("SELECT id_torneo FROM torneo WHERE nombre='%s'", tournamentName));
            query.next();
            id_torneo = query.getInt("id_torneo");
            query.close();
            for(String equipo: equipos){
                st.executeUpdate(String.format("INSERT INTO equipo_torneo(equipo, torneo) VALUES('%s', %d)", equipo, id_torneo));
            }
            System.out.println("Torneo creado satisfactoriamente!!!!");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
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
                    // Revisar lista de torneos
                    case "4":{
                        // ver torneos por jugarse
                        // ver torneos en juego
                        // ver torneos finalizados
                        menuVerTorneos(conn, input);
                        break;
                    }
                    // Crear un torneo
                    case "5":{
                        menuInsertarTorneo(conn, input);
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
            }while(!option.equals("0"));
            input.close();
            conn.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}