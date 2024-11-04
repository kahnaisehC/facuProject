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
                    System.out.println("1.Elegir los equipos manualmente");
                    System.out.println("2.Crear un torneo con todos los equipos registrados");
                    option = input.next();
                    switch (option){
                        case "1":{
                            break;
                        }
                        case "2":{
                            ResultSet equiposQuery = st.executeQuery("SELECT nombre FROM equipo");
                            ArrayList<String> equiposArray = new ArrayList<>();
                            while(equiposQuery.next()){
                                String equipo = "";
                                equipo= equiposQuery.getString("nombre");
                                equiposArray.add(equipo);
                                System.out.printf("nombre del equipo: %s\n", equipo);
                            }
                            equiposQuery.close();



                            // TODO: fadd UNIQUE constraint to nombre_de_torneo
                            // TODO: handle if nombreYaUtilizado
                            System.out.println("Ingrese el nombre del torneo: ");
                            String nombreDelTorneo = input.next();
                            st.executeUpdate(String.format("INSERT INTO torneo(nombre) VALUES('%s')", nombreDelTorneo));
                            ResultSet id_torneoResultSet = st.executeQuery("SELECT id_torneo FROM torneo WHERE nombre == nombreDelTorneo");
                            id_torneoResultSet.next();

                            int id_torneo = id_torneoResultSet.getInt("id_torneo");
                            id_torneoResultSet.close();


                            for(int i = 0; i < equiposArray.size(); i++){

                            }




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
                                    pareos[i] = "NULL";
                                }else{
                                    pareos[i] = equiposArray.get(equiposArrayPointer);
                                }
                            }

                            StringBuilder partidosString = new StringBuilder();
                            for(int i = 0; i < base-2; i+=2){
                                // base-i/2
                                partidosString.append(String.format("('%s', '%s', %d, %d), ", pareos[i], pareos[i + 1], id_torneo, base - i / 2));
                            }
                            partidosString.append(String.format("('%s', '%s', %d, %d), ", pareos[pareos.length-2], pareos[pareos.length-1], id_torneo, (base)/2));

                            // resultado => 1 = equipo1 gana; 2 = equipo2 gana; 3 = empate; NULL/0 = no definido;


                            st.executeQuery(String.format("INSERT INTO partido(equipo1, equipo2, id_torneo, id_partido_torneo) VALUES%s", partidosString.toString()));
                            break;
                        }
                        default:{
                            System.out.println("opcion no valida");
                        }
                    }

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