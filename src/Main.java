import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost/test";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "Welcome4$");
        try {
            Connection conn = DriverManager.getConnection(url, props);

            System.out.println("===========================================");
            System.out.println("BIENVENIDO AL GESTOR DE TORNEOS DE FUTBOL!!!!!!!!!!!!");
            System.out.println("===========================================");




            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM ropas");
            while (rs.next()) {
                System.out.print("Column 1 returned ");
                System.out.println(rs.getString(2));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println("Todo funciona!!! (en teoria)");
            throw new RuntimeException(e);
        }
    }
}