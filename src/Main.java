import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

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
public class Main {
    static void updatePartidosDeTorneo(Statement st, int id_torneo){
        try {
            ResultSet partidosTerminadosQuery = st.executeQuery(String.format("SELECT id_partido, equipo1, equipo2, id_partido_torneo, equipo_ganador FROM partido WHERE id_torneo = %d AND (equipo_ganador != NULL OR equipo1 ='bye' OR equipo2 = 'bye')", id_torneo));
            while(partidosTerminadosQuery.next()){
                String equipo1 = partidosTerminadosQuery.getString("equipo1");
                String equipo2 = partidosTerminadosQuery.getString("equipo2");
                String equipoGanador= partidosTerminadosQuery.getString("equipo_ganador");
                int idPartidoTorneo = partidosTerminadosQuery.getInt("id_partido_torneo");
                String numeroEquipo = (1&idPartidoTorneo) == 1 ? "equipo2" : "equipo1";
                if(equipo1.equals("bye")){
                    equipoGanador = equipo2;
                }else if(equipo2.equals("bye")){
                    equipoGanador = equipo1;
                }else if(equipoGanador.equals("bye")){
                    throw new RuntimeException("el resultado es un empate!");
                }

                Connection conn = st.getConnection();
                Statement st2 = conn.createStatement();

                st2.executeUpdate(String.format("UPDATE partido SET %s = '%s' WHERE id_partido_torneo = %d AND id_torneo = %d", numeroEquipo, equipoGanador, idPartidoTorneo/2, id_torneo));
                st2.close();
            }
            partidosTerminadosQuery.close();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    static void insertarTorneo(Statement st, Scanner input){
        try{
            String insertarTorneoMenu = "1.Elegir los equipos manualmente\n" +
                    "2.Crear un torneo con todos los equipos registrados\n" +
                    "0.Volver a menu principal\n";
            String option;
            do{
                System.out.println(insertarTorneoMenu);
                option = input.next();
                ArrayList<String> equiposArray = new ArrayList<>();
                switch (option){
                    case "1":{
                        break;
                    }
                    case "2":{
                        ResultSet equiposQuery = st.executeQuery("SELECT nombre FROM equipo WHERE nombre != 'bye'");
                        while(equiposQuery.next()){
                            String equipo = "";
                            equipo= equiposQuery.getString("nombre");
                            equiposArray.add(equipo);
                            System.out.printf("nombre del equipo: %s\n", equipo);
                        }
                        equiposQuery.close();
                        break;
                    }
                    case "0":{
                        return;
                    }
                    default:{
                        System.out.println("opcion no valida");
                        return;
                    }
                }
                if(equiposArray.size() < 2){
                    System.out.println("No hay suficientes equipos para crear torneo");
                    return;
                }
                // TODO: handle if nombreYaUtilizado
                // TODO: handle if size of equipos == 1
                System.out.println("Ingrese el nombre del torneo: ");
                String nombreDelTorneo = input.next();
                st.executeUpdate(String.format("INSERT INTO torneo(nombre) VALUES('%s')", nombreDelTorneo));
                ResultSet id_torneoResultSet = st.executeQuery(String.format("SELECT id_torneo FROM torneo WHERE nombre = '%s'", nombreDelTorneo));
                id_torneoResultSet.next();

                int id_torneo = id_torneoResultSet.getInt("id_torneo");
                id_torneoResultSet.close();

                int base = 1;
                while(base < equiposArray.size()){
                    base <<= 1;
                }
                String[] pareos = new String[base];
                int equiposArrayPointer = 0;
                for(int i = 0; i < base; i+=2){
                    pareos[i] = equiposArray.get(equiposArrayPointer++);
                }
                for(int i = 1; i < base; i+=2) {
                    if (equiposArrayPointer >= equiposArray.size()){
                        pareos[i] = "bye";
                    }else{
                        pareos[i] = equiposArray.get(equiposArrayPointer++);
                    }
                }

                StringBuilder partidosString = new StringBuilder();
                for(int i = 0; i < base; i+=2){
                    // base-i/2
                    String byeResult = "'" + pareos[i] + "'";
                    if(pareos[i+1].equals("bye"))
                        byeResult = "NULL";

                    partidosString.append(String.format("('%s', '%s', %d, %d, %s), ", pareos[i], pareos[i + 1], id_torneo, base - i / 2 -1, byeResult));
                }

                for(int i = base/2 -1; i>1; i--){
                    partidosString.append(String.format("(%s, %s, %d, %d, %s), ", "NULL", "NULL", id_torneo, i, "NULL"));
                }

                // resultado => 1 = equipo1 gana; 2 = equipo2 gana; 3 = empate; NULL/0 = no definido;
                if(equiposArray.size() != 2)
                    partidosString.append(String.format("(%s, %s, %d, %d, %s)", "NULL", "NULL", id_torneo, 1, "NULL"));

                System.out.println(partidosString.toString());
                st.executeUpdate(String.format("INSERT INTO partido(equipo1, equipo2, id_torneo, id_partido_torneo, equipo_ganador) VALUES%s", partidosString.toString()));
                updatePartidosDeTorneo(st, id_torneo);
                System.out.println("opciones de torneo");
                return;
            }while(true);

        }
        catch (Exception e){
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
            Statement st = conn.createStatement();
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
                        insertarTorneo(st, input);
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
            conn.close();
            st.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}