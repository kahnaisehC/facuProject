import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
class Equipo{
    public String nombre;
}

class Jugador{
    public int dni;
    public String nombre;
    public String nombre_equipo;
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

    static void elegirListaTorneos(Connection conn, Scanner input){
        try{
            Statement st = conn.createStatement();
            String option;
            do{
                System.out.println("1.Mostrar torneos ya terminados");
                System.out.println("2.Mostrar torneos en juego");
                System.out.println("0.Volver hacia atras");
                option = input.next();
                switch (option){
                    case "1":{
                        // Revisar torneos terminados
                        ArrayList<Torneo> torneosTerminados = new ArrayList<>();
                        ResultSet torneosTerminadosQuery = st.executeQuery("SELECT id_torneo, nombre, equipo_ganador FROM torneo WHERE equipo_ganador IS NOT NULL");
                        while(torneosTerminadosQuery.next()){
                            Torneo torneo = new Torneo();
                            torneo.id_torneo = torneosTerminadosQuery.getInt("id_torneo");
                            torneo.nombre_torneo = torneosTerminadosQuery.getString("nombre");
                            torneo.equipo_ganador = torneosTerminadosQuery.getString("equipo_ganador");
                            torneosTerminados.add(torneo);
                        }
                        torneosTerminadosQuery.close();
                        break;
                    }
                    // Mostrar torneos en juego
                    case "2":{
                        ArrayList<Torneo> torneosEnJuego = new ArrayList<>();
                        ResultSet torneosEnJuegoQuery = st.executeQuery("SELECT id_torneo, nombre FROM torneo WHERE equipo_ganador IS NULL");
                        while(torneosEnJuegoQuery.next()){
                            Torneo torneo = new Torneo();
                            torneo.id_torneo = torneosEnJuegoQuery.getInt("id_torneo");
                            torneo.nombre_torneo = torneosEnJuegoQuery.getString("nombre");
                            torneosEnJuego.add(torneo);
                        }
                        torneosEnJuegoQuery.close();
                        break;
                    }
                    case "0":{
                        break;
                    }
                    default:{
                        System.out.println("Opcion no valida");
                    }
                }

            }while(!option.equals("0"));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    static void insertarTorneo(Connection conn, Scanner input){
        try{
            Statement st = conn.createStatement();
            String insertarTorneoMenu = "1.Elegir los equipos manualmente\n" +
                    "2.Crear un torneo con todos los equipos registrados\n" +
                    "0.Volver hacia atras\n";
            String option;
            ArrayList<String> equiposArray = new ArrayList<>();
            Boolean breakWhile;
            do{
                breakWhile = true;
                System.out.println(insertarTorneoMenu);
                option = input.next();
                switch (option){
                    // Elegir equipos manualmente
                    case "1":{
                        break;
                    }
                    // Elegir todos los equipos
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
                    // Volver hacia atras / Salir de la funcion
                    case "0":{
                       st.close();
                        return;

                    }
                    default:{
                        breakWhile = false;
                        System.out.println("opcion no valida");
                    }
                }
                if(equiposArray.size() < 2){
                    System.out.println("No hay suficientes equipos para crear torneo");
                    breakWhile = false;
                }
            }while(!breakWhile);
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
            st.close();

        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    static ArrayList<Jugador> obtenerListaJugadores(Connection conn){
        ArrayList<Jugador> Jugadores = new ArrayList<>();
        try {
            Statement st = conn.createStatement();
            ResultSet jugadoresQuery = st.executeQuery("SELECT dni, nombre, nombre_equipo FROM jugador WHERE dni IS NOT NULL");
            while(jugadoresQuery.next()){
                Jugador jugador = new Jugador();
                jugador.dni = jugadoresQuery.getInt("dni");
                jugador.nombre = jugadoresQuery.getString("nombre");
                jugador.nombre_equipo = jugadoresQuery.getString("nombre_equipo");
                Jugadores.add(jugador);
            }
            jugadoresQuery.close();
        }catch(Exception e){
            System.out.println("ERROR: No se pudo obtener lista de jugadores.");
        }
        return Jugadores;
    };

    static ArrayList<Equipo> obtenerListaEquipos(Connection conn){
        ArrayList<Equipo> Equipos = new ArrayList<>();
        try {
            Statement st = conn.createStatement();
            ResultSet equiposQuery = st.executeQuery("SELECT nombre FROM equipo WHERE nombre IS NOT NULL");
            while(equiposQuery.next()){
                Equipo equipo = new Equipo();
                equipo.nombre = equiposQuery.getString("nombre");
                Equipos.add(equipo);
            }
            equiposQuery.close();
        }catch(Exception e){
            System.out.println("ERROR: No se pudo obtener lista de equipos.");
        }
        return Equipos;
    }

    static void nombrarJugadores(ArrayList<Jugador> Jugadores){
        int Cantidad = 0;
        for(Jugador jugador : Jugadores){
            Cantidad += 1;
            String equipoNombre = jugador.nombre_equipo;
            if(jugador.nombre_equipo == null){
                equipoNombre = "Sin equipo";
            }
            System.out.println(Cantidad+". Nombre: "+jugador.nombre+" | DNI: "+jugador.dni+" | Equipo: "+equipoNombre);
        }
    }

    static void nombrarEquipos(ArrayList<Jugador> Jugadores, ArrayList<Equipo> Equipos){
        int Cantidad = 0;
        int jugadoresEnEquipo = 0;

        for(Equipo equipo : Equipos){
            Cantidad += 1;
            for(Jugador jugador : Jugadores){
                if(jugador.nombre_equipo.equals(equipo.nombre)){
                    jugadoresEnEquipo += 1;
                }
            }
            System.out.println(Cantidad+". Equipo: "+equipo.nombre+" | Cantidad de jugadores: "+jugadoresEnEquipo);
            jugadoresEnEquipo = 0;
        }
    }

    static  void nombrarJugadoresDeEquipo(ArrayList<Jugador> Jugadores, String nombre){
        int Cantidad = 0;
        for(Jugador jugador : Jugadores){
            if(jugador.nombre_equipo.equals(nombre)){
                Cantidad += 1;
                System.out.println(Cantidad+". Nombre: "+jugador.nombre+" | DNI: "+jugador.dni);
            }
        }
    }


    static void revistarListaJugadores(Connection conn, Scanner input){
        try{
            Statement st = conn.createStatement();
            String option;
            do{
                System.out.println("1. Mostrar todos los jugadores y sus equipos.");
                System.out.println("2. Crear jugador.");
                System.out.println("3. Borrar jugador.");
                System.out.println("0. Volver hacia atras.");
                option = input.next();
                switch (option){
                    case "1":{
                        ArrayList<Jugador> Jugadores = obtenerListaJugadores(conn);
                        System.out.println("Los jugadores y sus equipos son:");
                        nombrarJugadores(Jugadores);
                        break;
                    }

                    case "2":{
                        ArrayList<Jugador> Jugadores = obtenerListaJugadores(conn);
                        ArrayList<Equipo> Equipos = obtenerListaEquipos(conn);

                        System.out.println("Ingrese el nombre del nuevo jugador:");
                        String Nombre = input.next();
                        System.out.println("Ingrese el DNI del nuevo jugador:");
                        String DNItemp = input.next();
                        int DNI;
                        try{
                            DNI = Integer.parseInt(DNItemp);
                            if(DNI < 0){
                                System.out.println("DNI invalido.");
                                break;
                            };
                            for(Jugador jugador : Jugadores){
                                if(jugador.dni == DNI){
                                    System.out.println("Un jugador ya existe con ese DNI.");
                                    break;
                                }
                            }
                        } catch (NumberFormatException e){
                            System.out.println("DNI invalido.");
                            break;
                        };

                        if(Equipos.isEmpty()) {
                            System.out.println("No existen ningun equipo al cual se pueda asignar el jugador.");
                            break;
                        }

                        System.out.println("Ingrese el equipo existente al cual va a pertenecer el nuevo jugador, Ingrese 'VER' si quiere ver todos los equipos registrados primero.");
                        String equipoJugador = input.next();
                        if(equipoJugador.equals("VER")){
                            System.out.println("Los equipos registrados son:");
                            nombrarEquipos(Jugadores,Equipos);
                            System.out.println("Ahora ingrese el NOMBRE del equipo registrado al cual el jugador va a pertenecer.");
                            equipoJugador = input.next();
                        }

                        boolean Existe = false;

                        for(Equipo equipo : Equipos){
                           if(equipo.nombre.equals(equipoJugador)){
                               Existe = true;
                               break;
                           }
                        }

                        if(!Existe){
                            System.out.println("Ese equipo no esta registrado.");
                            break;
                        }

                        System.out.println("El jugador "+Nombre+" ha sido registrado.");

                        /* TODO: hacer QUERY para insertar el JUGADOR a la BD.
                               Las variables a utilizar son "Nombre", "DNI", y "equipoJugador".
                        */

                        break;
                    }

                    case "3":{
                        ArrayList<Jugador> Jugadores = obtenerListaJugadores(conn);
                        System.out.println("Ingrese el DNI del jugador a borrar:");
                        String DNItemp = input.next();
                        int DNI;
                        try{
                            DNI = Integer.parseInt(DNItemp);
                        } catch (NumberFormatException e){
                            System.out.println("DNI invalido.");
                            break;
                        };

                        Jugador jugadorAfectado = null;

                        for(Jugador jugador : Jugadores){
                            if(jugador.dni == DNI){
                                jugadorAfectado = jugador;
                                break;
                            }
                        }

                        if(jugadorAfectado == null){
                            System.out.println("No existe un jugador con ese DNI en el registro.");
                            break;
                        }

                        System.out.println("El jugador "+jugadorAfectado.nombre+" fue eliminado del registro.");

                        /* TODO: hacer QUERY para eliminar el JUGADOR de la BD.
                               La variable a utilizar es "DNI".
                        */

                        break;
                    }

                    case "0":{
                        break;
                    }

                    default:{
                        System.out.println("Opcion no valida.");
                    }
                }

            }while(!option.equals("0"));
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    static void revistarListaEquipos(Connection conn, Scanner input){
        try{
            Statement st = conn.createStatement();
            String option;
            do {
                System.out.println("1. Mostrar todos los equipos y sus jugadores.");
                System.out.println("2. Crear un equipo.");
                System.out.println("3. Añadir un jugador sin equipo a un equipo.");
                System.out.println("4. Quitar un jugador de un equipo.");
                System.out.println("5. Borrar un equipo.");
                System.out.println("0. Volver hacia atras.");
                option = input.next();
                switch (option){
                    case "1":{
                        ArrayList<Jugador> Jugadores = obtenerListaJugadores(conn);
                        ArrayList<Equipo> Equipos = obtenerListaEquipos(conn);

                        if(Equipos.isEmpty()){
                            System.out.println("Hay 0 equipos registrados.");
                            break;
                        }

                        System.out.println("Los equipos registrados son:");
                        nombrarEquipos(Jugadores,Equipos);

                        System.out.println("Ingrese el NOMBRE del equipo que quiera ver sus jugadores, o cualquier otra cosa para salir.");

                        String nombre = input.next();
                        boolean Existe = false;

                        for(Equipo equipo : Equipos){
                            if(equipo.nombre.equals(nombre)){
                                Existe = true;
                                break;
                            }
                        }

                        if(!Existe){
                            break;
                        }

                        System.out.println("Los jugadores del equipo "+nombre+" son:");
                        nombrarJugadoresDeEquipo(Jugadores,nombre);

                        break;
                    }

                    case "2":{
                        System.out.println("Ingrese el nombre del nuevo equipo:");
                        String Nombre = input.next();

                        ArrayList<Equipo> Equipos = obtenerListaEquipos(conn);

                        for(Equipo equipo : Equipos){
                            if(equipo.nombre.equals(Nombre)){
                                System.out.println("Ya existe un equipo con ese nombre.");
                                break;
                            }
                        }

                        System.out.println("El equipo "+Nombre+" ha sido registrado.");

                        /* TODO: hacer QUERY para insertar el EQUIPO a la BD.
                               La variable a utilizar es "Nombre".
                        */

                        break;
                    }

                    case "3":{
                        ArrayList<Jugador> Jugadores = obtenerListaJugadores(conn);
                        ArrayList<Equipo> Equipos = obtenerListaEquipos(conn);
                        ArrayList<Jugador> jugadoresSolos = new ArrayList<>();

                        for(Jugador jugador : Jugadores){
                            if(jugador.nombre_equipo == null){
                                jugadoresSolos.add(jugador);
                            }
                        }

                        if(jugadoresSolos.isEmpty()){
                            System.out.println("No hay jugadores sin equipo registrados.");
                            break;
                        }

                        System.out.println("Los jugadores sin equipo son:");
                        int Cantidad = 0;
                        for(Jugador jugador : jugadoresSolos){
                            Cantidad += 1;
                            System.out.println(Cantidad+". Nombre: "+jugador.nombre+" DNI: "+jugador.dni);
                        }

                        System.out.println("Ingrese el DNI del jugador que quiera añadir a un equipo.");
                        String DNItemp = input.next();
                        int DNI;
                        try{
                            DNI = Integer.parseInt(DNItemp);
                        } catch (NumberFormatException e){
                            System.out.println("DNI invalido.");
                            break;
                        };

                        Jugador jugadorSinEquipo = null;

                        for(Jugador jugador : jugadoresSolos){
                            if(jugador.dni == DNI){
                                jugadorSinEquipo = jugador;
                            }
                        }

                        if(jugadorSinEquipo == null){
                            System.out.println("Ese jugador no se encuentra en el registro.");
                            break;
                        }

                        System.out.println("Ingrese el equipo existente al cual va a pertenecer el jugador, Ingrese 'VER' si quiere ver todos los equipos registrados primero.");
                        String equipoJugador = input.next();
                        if(equipoJugador.equals("VER")){
                            System.out.println("Los equipos registrados son:");
                            nombrarEquipos(Jugadores,Equipos);
                            System.out.println("Ahora ingrese el NOMBRE del equipo registrado al cual el jugador va a pertenecer.");
                            equipoJugador = input.next();
                        }

                        Equipo equipoIntegrador = null;

                        for(Equipo equipo : Equipos){
                            if(equipo.nombre.equals(equipoJugador)){
                                equipoIntegrador = equipo;
                            }
                        }

                        if(equipoIntegrador == null){
                            System.out.println("Ese equipo no esta registrado.");
                            break;
                        }

                        System.out.println("El jugador "+jugadorSinEquipo.nombre+" ha sido integrado a "+equipoIntegrador.nombre+".");
                        /* TODO: hacer QUERY para cambiar los datos del JUGADOR en la BD.
                               El registro del JUGADOR puede ser obtenido con la variable "DNI".
                               El campo a cambiar es "equipo", el cual debe ser cambiado a equipoJugador.
                        */
                        break;
                    }

                    case "4":{
                        ArrayList<Jugador> Jugadores = obtenerListaJugadores(conn);
                        ArrayList<Equipo> Equipos = obtenerListaEquipos(conn);
                        ArrayList<Jugador> jugadoresEnEquipo = new ArrayList<>();

                        System.out.println("Ingrese el equipo existente al cual va a quitar un jugador, Ingrese 'VER' si quiere ver todos los equipos registrados primero.");
                        String equipoJugador = input.next();
                        if(equipoJugador.equals("VER")){
                            System.out.println("Los equipos registrados son:");
                            nombrarEquipos(Jugadores,Equipos);
                            System.out.println("Ahora ingrese el NOMBRE del equipo registrado del cual se va a borrar un jugador.");
                            equipoJugador = input.next();
                        }

                        Equipo equipoAfectado = null;

                        for(Equipo equipo : Equipos){
                            if(equipo.nombre.equals(equipoJugador)){
                                equipoAfectado = equipo;
                                break;
                            }
                        }

                        if(equipoAfectado == null){
                            System.out.println("Ese equipo no se encuentra en el registro.");
                            break;
                        }

                        for(Jugador jugador : Jugadores){
                            if(jugador.nombre_equipo.equals(equipoAfectado.nombre)){
                                jugadoresEnEquipo.add(jugador);
                            }
                        }

                        if(jugadoresEnEquipo.isEmpty()){
                            System.out.println("Ese equipo no posee ningun jugador para remover.");
                            break;
                        }

                        System.out.println("Ingrese el DNI del jugador que desee remover del equipo.");

                        String DNItemp = input.next();
                        int DNI;
                        try{
                            DNI = Integer.parseInt(DNItemp);
                        } catch (NumberFormatException e){
                            System.out.println("DNI invalido.");
                            break;
                        };

                        Jugador jugadorAfectado = null;
                        for(Jugador jugador : jugadoresEnEquipo){
                            if(jugador.dni == DNI){
                                jugadorAfectado = jugador;
                            }
                        }

                        if(jugadorAfectado == null){
                            System.out.println("No existe un jugador con ese DNI en el registro de este equipo.");
                            break;
                        }

                        System.out.println("El jugador "+jugadorAfectado.nombre+" ha sido removido del equipo "+equipoAfectado.nombre+".");
                        /* TODO: hacer QUERY para cambiar los datos del JUGADOR en la BD.
                               El registro del JUGADOR puede ser obtenido con la variable "DNI".
                               El campo a cambiar es "equipo", el cual debe ser asignado a null.
                        */
                        break;
                    }

                    case "5":{
                        ArrayList<Jugador> Jugadores = obtenerListaJugadores(conn);
                        ArrayList<Equipo> Equipos = obtenerListaEquipos(conn);
                        ArrayList<Jugador> jugadoresEnEquipo = new ArrayList<>();

                        System.out.println("Ingrese el equipo existente al cual va a eliminar, Ingrese 'VER' si quiere ver todos los equipos registrados primero.");
                        String equipoJugador = input.next();
                        if(equipoJugador.equals("VER")){
                            System.out.println("Los equipos registrados son:");
                            nombrarEquipos(Jugadores,Equipos);
                            System.out.println("Ahora ingrese el NOMBRE del equipo registrado a eliminar.");
                            equipoJugador = input.next();
                        }

                        Equipo equipoAfectado = null;

                        for(Equipo equipo : Equipos){
                            if(equipo.nombre.equals(equipoJugador)){
                                equipoAfectado = equipo;
                                break;
                            }
                        }

                        if(equipoAfectado == null){
                            System.out.println("Ese equipo no se encuentra en el registro.");
                            break;
                        }

                        for(Jugador jugador : Jugadores){
                            if(jugador.nombre_equipo.equals(equipoAfectado.nombre)){
                                jugadoresEnEquipo.add(jugador);
                            }
                        }

                        System.out.println("El equipo "+equipoAfectado.nombre+"posee "+jugadoresEnEquipo.size()+" jugador(es).");

                        if(jugadoresEnEquipo.isEmpty()){
                            System.out.println("El equipo fue eliminado del registro.");
                            /*TODO: hacer QUERY para eliminar el EQUIPO de la BD.
                               El campo a utilizar es equipoJugador.
                            */
                            break;
                        }

                        System.out.println("Ingrese 0 si quiere también eliminar a todos los jugadores del equipo, o cualquier otra cosa si quiere que los jugadores solo sean asignados sin equipo.");
                        String opcion = input.next();
                        if(opcion.equals("0")){
                            /*TODO: hacer QUERY para eliminar el EQUIPO de la BD.
                               El campo a utilizar es equipoJugador.
                            */

                            /*TODO: hacer QUERY para cambiar los datos de los JUGADOR(es) en la BD.
                               Todos los Jugador en "jugadoresEnEquipo" deben ser eliminados de la BD.
                            */

                            System.out.println("El equipo "+equipoAfectado.nombre+" y sus jugadores han sido eliminados.");
                            break;
                        }

                        System.out.println("El equipo "+equipoAfectado.nombre+" ha sido eliminado del registro.");
                        /*TODO: hacer QUERY para eliminar el EQUIPO de la BD.
                            El campo a utilizar es equipoJugador.
                        */

                        /*TODO: hacer QUERY para cambiar los datos de los JUGADOR(es) en la BD.
                           Todos los Jugador en "jugadoresEnEquipo" deben tener su campo "equipo" cambiado a null.
                        */

                        break;
                    }

                    case "0":{
                        break;
                    }

                    default:{
                        System.out.println("Opcion no valida.");
                    }
                }

            }while(!option.equals("0"));
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
                        "1.Revisar/Editar lista de jugadores\n" +
                        "2.Revisar/Editar lista de equipos\n" +
                        "3.Revisar/Editar lista de partidos\n" +
                        "4.Revisar/Editar lista de torneos\n" +
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
                        revistarListaJugadores(conn, input);
                        break;
                    }
                    // Revisar lista de equipos
                    case "2":{
                        revistarListaEquipos(conn, input);
                        break;
                    }
                    //Revisar lista de partidos
                    case "3":{

                        break;
                    }
                    // Revisar lista de torneos
                    case "4":{
                        elegirListaTorneos(conn, input);
                        break;
                    }
                    // Crear un torneo
                    case "5":{
                        insertarTorneo(conn, input);
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
                        System.out.println("Input no valido.");
                        break;
                    }
                }

            }while(!option.equals("0"));
            conn.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}