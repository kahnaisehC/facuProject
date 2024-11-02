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
public class Main {
    public static void main(String[] args) {
        Map<String, Runnable> mapOfFunctions = new HashMap<>();
        mapOfFunctions.put("0", () -> {
            System.out.println("Elegiste el 0!!!!");
        });
        mapOfFunctions.put("1", () -> {
            System.out.println("Elegiste el 1!!!!");
        });
        mapOfFunctions.put("2", () -> {
            System.out.println("Elegiste el 02!!!!");
        });
        mapOfFunctions.put("3", () -> {
            System.out.println("Elegiste el 0!3!!!");
        });
        mapOfFunctions.put("4", () -> {
            System.out.println("Elegiste el 0!!4!!");
        });
        
        Scanner input = new Scanner(System.in);

        String url = "jdbc:postgresql://localhost/test";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "Welcome4$");
        String menu =
                        "===========================================\n" +
                        "BIENVENIDO AL GESTOR DE TORNEOS DE FUTBOL!\n" +
                        "===========================================\n" +
                        "Elija una opcion: \n" +
                        "1.Ingresar un jugador\n" +
                        "2.Ingresar un equipo\n" +
                        "3.Crear un partido\n" +
                        "4.Crear un torneo\n" +
                        "0.Salir\n";


        try {
            Connection conn = DriverManager.getConnection(url, props);
            Statement st = conn.createStatement();

            System.out.println(menu);
            String option = input.next();
            mapOfFunctions.get(option).run();

            ResultSet rs = st.executeQuery("SELECT * FROM ropas");
            while (rs.next()) {
                System.out.print("Column 1 returned ");
                System.out.println(rs.getString(2));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}