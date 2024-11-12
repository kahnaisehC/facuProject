import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


// Menu Interface
interface Menu {
    void display();
    void navigate();
}

// Concrete Menu Class
class MainMenu implements Menu {
    private List<MenuItem> items;
    private Connection connection;
    
    MainMenu(Connection connection) {
        items = new ArrayList<>();
        items.add(new MenuItem("0. Salir", this::exit, connection));
        items.add(new MenuItem("1. Revisar lista de jugadores", this::startGame, connection));
        items.add(new MenuItem("2. Revisar lista de equipos", this::goToSettings, connection));
        items.add(new MenuItem("3. Revisar lista de partidos", this::exit, connection));
        items.add(new MenuItem("4. Crear un partido", this::exit, connection));
    }
    
    @Override
    public void display() {
        System.out.println("Main Menu:");
        for (MenuItem item : items) {
            System.out.println(item.getLabel());
        }
    }

    @Override
    public void navigate() {
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        int index = Integer.parseInt(choice) - 1;
        if (index >= 0 && index < items.size()) {
            items.get(index).getAction().run();
        }
    }

    private void startGame() { /* Start game logic */ }
    private void goToSettings() { /* Go to settings */ }
    private void exit() { /* Exit app */ }
}

// MenuItem Class
class MenuItem {
    private String label;
    private Runnable action;
    private Connection connection;
    
    MenuItem(String label, Runnable action, Connection connection) {
        this.label = label;
        this.action = action;
        this.connection = connection;
    }
    
    public String getLabel() { return label; }
    public Runnable getAction() { return action; }
    public Connection getConnection() { return connection; }
}

// Main Class
public class GestorTorneos{
    public static void main(String[] args) {
        try{
            Dotenv dotenv = Dotenv.load();
            Properties props = new Properties();
            props.setProperty("user", dotenv.get("PG_USER"));
            props.setProperty("password", dotenv.get("PG_PASS"));
            String url = dotenv.get("PG_URL");
            Connection connection = DriverManager.getConnection(url, props);
            Menu mainMenu = new MainMenu(connection);
            while (true) {
                mainMenu.display();
                mainMenu.navigate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
