import org.checkerframework.checker.units.qual.A;

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
    private static void iniciarTorneo(Connection conn, int idTorneo){
        ArrayList<String> equipos = new ArrayList<>();
        try{
            Statement st = conn.createStatement();
            ResultSet equiposQuery = st.executeQuery(String.format("SELECT equipo FROM equipo_torneo WHERE torneo = %d", idTorneo));
            while(equiposQuery.next()){
                String equipo = equiposQuery.getString("equipo");
                equipos.add(equipo);
            }
            if (equipos.size() < 2){
                throw new RuntimeException("Necesitas al menos dos equipos para iniciar un torneo");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        ArrayList<Partido> partidosTorneo = new ArrayList<>();
        int base = 1;
        while(base < equipos.size()){
            base <<= 1;
        }
        for(int i = 0, count = base-1; i < base >> 1; i++, count--){
            Partido partido = new Partido();
            partido.id_partido_torneo = count;
            partido.equipo1 = equipos.get(i);
            partidosTorneo.add(partido);
        }
        int i = base >> 1;
        ArrayList<Partido> siguientesPartidos = new ArrayList<>();
        for(Partido partido: partidosTorneo){
            if(i == equipos.size()){
                // procesar como ganador
                partido.equipo2 = "bye";
                partido.equipo_ganador = partido.equipo1;
                Partido siguientePartido = new Partido();
                siguientePartido.id_partido_torneo = partido.id_partido_torneo/2;
                siguientePartido.equipo1 = partido.equipo1;
                siguientesPartidos.add(siguientePartido);
                continue;
            }
            partido.equipo2 = equipos.get(i);
            i++;
        }
        try{
            Statement st = conn.createStatement();
            for(Partido partido: partidosTorneo){
                if(partido.equipo2.equals("bye"))
                    continue;
                st.executeUpdate(String.format("INSERT INTO partido(equipo1, equipo2, id_torneo, id_partido_torneo) VALUES('%s', '%s', %d, %d)", partido.equipo1, partido.equipo2, idTorneo, partido.id_partido_torneo));
            }
            for(Partido partido: siguientesPartidos){
                st.executeUpdate(String.format("INSERT INTO partido(equipo1, id_torneo, id_partido_torneo) VALUES('%s', %d, %d)", partido.equipo1, idTorneo, partido.id_partido_torneo));
            }
            st.close();

        }catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Torneo iniciado con exito!!!");


    }
    private static void torneosPorJugarse(Connection conn, Scanner input){
        HashMap<Integer, String> torneos = new HashMap<>();
        int idTorneo;
        try{
            Statement st =  conn.createStatement();
            ResultSet tournamentsQuery = st.executeQuery("SELECT id_torneo, nombre FROM torneo WHERE id_torneo NOT IN (SELECT DISTINCT(id_torneo) FROM partido)");
            while(tournamentsQuery.next()){
                int id_torneo = tournamentsQuery.getInt("id_torneo");
                String nombre = tournamentsQuery.getString("nombre");
                torneos.put(id_torneo, nombre);
            }
            tournamentsQuery.close();
            st.close();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(torneos.isEmpty()){
            System.out.println("No hay ningun torneo por jugarse");
            return;
        }
        while(true){
            System.out.println("Ingrese '0' para volver al menu principal");
            System.out.println("Elija el numero del torneo que quiera modificar");
            for(Map.Entry<Integer, String> entry: torneos.entrySet()){
                int id_torneo= entry.getKey();
                String nombre_torneo = entry.getValue();
                System.out.println(id_torneo + "." + nombre_torneo);
            }
            String option = input.next();
            if(option.equals("0")){
                return;
            }
            if(!isNumeric(option)){
                System.out.println("Ingrese un numero!");
                continue;
            }
            if(torneos.get(Integer.parseInt(option)) == null){
                System.out.println("Numero de torneo no encontrado");
                continue;
            }
            idTorneo = Integer.parseInt(option);
            break;
        }
        while(true){
            System.out.println("Ingrese '0' para volver al menu principal");
            System.out.println("1. Añadir/Eliminar equipos de torneo");
            System.out.println("2. Iniciar torneo");
            String option = input.next();
            switch (option){
                case "0":{
                    return;
                }
                case "1":{
                    HashSet<String> equiposAdentro = new HashSet<>();
                    HashSet<String> equiposAfuera = new HashSet<>();
                    try {
                        Statement st = conn.createStatement();
                        ResultSet equiposEnTorneoQuery = st.executeQuery(String.format("SELECT equipo FROM equipo_torneo WHERE torneo = %d", idTorneo));
                        while(equiposEnTorneoQuery.next()){
                            String equipo = equiposEnTorneoQuery.getString("equipo");
                            equiposAdentro.add(equipo);
                        }
                        equiposEnTorneoQuery.close();
                        st.close();
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                        return;
                    }
                    try{
                        Statement st = conn.createStatement();
                        ResultSet equiposFueraTorneoQuery = st.executeQuery("SELECT nombre FROM equipo");
                        while(equiposFueraTorneoQuery.next()){
                            String equipo = equiposFueraTorneoQuery.getString("nombre");
                            if(equiposAdentro.contains(equipo)){
                                continue;
                            }
                            equiposAfuera.add(equipo);
                        }
                        equiposFueraTorneoQuery.close();
                        st.close();

                    }catch (Exception e){
                        System.out.println(e.getMessage());
                        return;
                    }
                    while(true){
                        System.out.println("Ingrese el nombre del equipo a añadir/eliminar");
                        if (!equiposAdentro.isEmpty())
                            System.out.println("Equipos en el torneo");
                        for(String equipo : equiposAdentro){
                            System.out.println(equipo);
                        }
                        if(!equiposAfuera.isEmpty())
                            System.out.println("Equipos fuera del torneo");
                        for(String equipo : equiposAfuera){
                            System.out.println(equipo);
                        }
                        System.out.println("0. Guardar cambios");
                        option = input.next();
                        if(option.equals("0")){
                            try{
                                Statement st = conn.createStatement();
                                st.executeUpdate(String.format("DELETE FROM equipo_torneo WHERE torneo = %d", idTorneo));
                                for (String equipo: equiposAdentro){
                                    st.executeQuery(String.format("INSERT INTO equipo_torneo(equipo, torneo) values('%s', %d) ON CONFLICT DO NOTHING", equipo, idTorneo));
                                }
                                st.close();
                            }catch (Exception e){
                                System.out.println(e.getMessage());
                                return;
                            }
                            break;
                        }
                        if(equiposAdentro.contains(option)){
                            equiposAdentro.remove(option);
                            equiposAfuera.add(option);
                        }else if(equiposAfuera.contains(option)){
                            equiposAfuera.remove(option);
                            equiposAdentro.add(option);
                        }else{
                            System.out.println("Equipo no existe!");
                        }
                    }
                }
                case "2":{
                    try {
                        iniciarTorneo(conn, idTorneo);
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                        return;
                    }
                    break;
                }
                default:{
                    System.out.println("Opcion invalida!");
                    continue;
                }
            }
            break;
        }
    }

    private static void torneosEnJuego(Connection conn, Scanner input){

        Integer idTorneo;
        HashMap<Integer, String> torneosEnJuego = new HashMap<>();
        try{
            Statement st = conn.createStatement();
            ResultSet torneosEnJuegoQuery = st.executeQuery(String.format("SELECT id_torneo, nombre FROM torneo WHERE id_torneo NOT IN (SELECT DISTINCT(id_torneo) FROM torneo WHERE equipo_ganador IS NOT NULL)"));
            while(torneosEnJuegoQuery.next()){
                Integer id_torneo = torneosEnJuegoQuery.getInt("id_torneo");
                String nombre = torneosEnJuegoQuery.getString("nombre");
                torneosEnJuego.put(id_torneo, nombre);
            }
            torneosEnJuegoQuery.close();
            st.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        while(true){
            System.out.println("Elija el numero de torneo");
            for(Integer id_torneo : torneosEnJuego.keySet()){
                System.out.println(id_torneo + "." + torneosEnJuego.get(id_torneo));
            }
            System.out.println("0. Salir al menu principal");
            String option = input.next();

            if(option.equals("0")){
                return;
            }
            if(!isNumeric(option)){
                System.out.println("Ingrese un numeroo");
                continue;
            }
            if(torneosEnJuego.get(Integer.parseInt(option)) == null){
                System.out.println("No existe un torneo con ese numero");
                continue;
            }
            idTorneo = Integer.parseInt(option);
            HashMap<Integer, Partido> partidosDeTorneo = new HashMap<>();
            // get partidos
            try {
                Statement st = conn.createStatement();
                System.out.println(String.format("SELECT id_partido, equipo1, equipo2, id_partido_torneo FROM partido WHERE id_torneo = %d", idTorneo));
                ResultSet partidosDeTorneoQuery = st.executeQuery(String.format("SELECT id_partido, equipo1, equipo2, id_partido_torneo, equipo_ganador FROM partido WHERE id_torneo = %d", idTorneo));
                while(partidosDeTorneoQuery.next()){
                    Partido partido = new Partido();
                    partido.id_partido = partidosDeTorneoQuery.getInt("id_partido");
                    partido.equipo1 = partidosDeTorneoQuery.getString("equipo1");
                    partido.equipo2 = partidosDeTorneoQuery.getString("equipo2");
                    partido.equipo_ganador = partidosDeTorneoQuery.getString("equipo_ganador");
                    partido.id_partido_torneo = partidosDeTorneoQuery.getInt("id_partido_torneo");
                    partidosDeTorneo.put(partido.id_partido_torneo, partido);
                    System.out.println("fdioskjflksdfjklds");
                }
                partidosDeTorneoQuery.close();
                st.close();
            }catch (Exception e){
                System.out.println(e.getMessage());
                return;
            }
            // ask to modify them
            while(true){
                System.out.println("Ingrese el numero del partido a modificar");
                for(Integer id_partido_torneo : partidosDeTorneo.keySet()){
                    Partido partido = partidosDeTorneo.get(id_partido_torneo);
                    Partido siguientePartido = partidosDeTorneo.get((id_partido_torneo/2));
                    System.out.println(partido.equipo1);
                    System.out.println(partido.equipo2);
                    
                    if((siguientePartido == null || siguientePartido.equipo_ganador == null) && (partido.equipo1 != null) && (partido.equipo2 != null)){

                        int avos = 1;
                        while(avos < partido.id_partido_torneo){
                            avos <<= 1;
                        }
                        if(avos == 1){
                            System.out.printf("Final: ");
                        }
                        else if(avos == 2){
                            System.out.printf("Semi-final: ");
                        }
                        else if(avos == 4){
                            System.out.printf("4tos: ");
                        }else{
                            System.out.printf("%dvos: ", avos);
                        }

                        System.out.println(partido.id_partido_torneo + "." + partido.equipo1 + " vs " + partido.equipo2);

                    }
                    option = input.next();

                }
                if(option.equals("0")){
                    break;
                }

            }

            // update partidos


            break;
        }
    }

    public static void menuVerTorneos(Connection conn, Scanner input){
        while(true){
            System.out.println("Ingrese '0' para volver al menu principal");
            System.out.println("Ingrese el tipo de torneo que quiere listar");
            System.out.println("1. Torneos por jugarse");
            System.out.println("2. Torneos en juego");
            System.out.println("3. Torneos jugados");
            String option = input.next();
            switch (option){
                case ("1"):{
                    try {
                        torneosPorJugarse(conn, input);
                    }
                    catch (Exception e){
                        System.out.println(e.getMessage());
                        return;
                    }
                    break;

                }
                case "2":{
                    try{
                        torneosEnJuego(conn, input);
                    }
                    catch (Exception e){
                        System.out.println(e.getMessage());
                        return;
                    }

                    break;
                }
                case "3":{

                    break;
                }
                case "0":{
                    return;
                }
                default:{
                    System.out.println("opcion invalida");
                    continue;
                }
            }
            break;
        }

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
                        // ver torneos por jugarse TICK
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