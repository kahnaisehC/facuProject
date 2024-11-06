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
            ResultSet partidosTerminadosQuery = st.executeQuery(String.format("SELECT id_partido, equipo1, equipo2, id_partido_torneo, resultado FROM partido WHERE id_torneo = %d AND (resultado != NULL OR equipo1 ='bye' OR equipo2 = 'bye')", id_torneo));
            while(partidosTerminadosQuery.next()){
                System.out.println("entre!");
                String equipo1 = partidosTerminadosQuery.getString("equipo1");
                String equipo2 = partidosTerminadosQuery.getString("equipo2");
                int resultado = partidosTerminadosQuery.getInt("resultado");
                int idPartidoTorneo = partidosTerminadosQuery.getInt("id_partido_torneo");
                String equipoGanador;
                String numeroEquipo = (1&idPartidoTorneo) == 1 ? "equipo2" : "equipo1";
                if(resultado == 2 || equipo1.equals("bye")){
                    equipoGanador = equipo2;
                }else if(resultado == 1 || equipo2.equals("bye")){
                    equipoGanador = equipo1;
                }else{
                    throw new RuntimeException("Resultado incompatible");
                }
                Connection conn = st.getConnection();
                Statement st2 = conn.createStatement();

                st2.executeUpdate(String.format("UPDATE partido SET %s = '%s' WHERE id_partido_torneo = %d AND id_torneo = %d", numeroEquipo, equipoGanador, idPartidoTorneo/2, id_torneo));
                // UPDATE partido SET equipo1/2(modulo) = "equipoGanador" WHERE id_partido_torneo = idPartidoTorneo/2 AND id_torneo = id_torneo
                st2.close();
                conn.close();

            }
            partidosTerminadosQuery.close();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    static void insertarTorneo(Statement st, Scanner input){
        try{
        System.out.println("1.Elegir los equipos manualmente");
        System.out.println("2.Crear un torneo con todos los equipos registrados");
        String option = input.next();
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
            default:{
                System.out.println("opcion no valida");
            }
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
            int[] segTree = new int[base];
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
                String byeResult = "1";
                if(pareos[i+1] != "bye")
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
            st.executeUpdate(String.format("INSERT INTO partido(equipo1, equipo2, id_torneo, id_partido_torneo, resultado) VALUES%s", partidosString.toString()));
            updatePartidosDeTorneo(st, id_torneo);

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
            st.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}