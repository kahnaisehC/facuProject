import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Menu Interface
interface Menu {
    void display();
    void navigate();
}

// Concrete Menu Class
class MainMenu implements Menu {
    private List<MenuItem> items;
    
    MainMenu() {
        items = new ArrayList<>();
        items.add(new MenuItem("1. Start Game", () -> startGame()));
        items.add(new MenuItem("2. Settings", () -> goToSettings()));
        items.add(new MenuItem("3. Exit", () -> exit()));
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
    
    MenuItem(String label, Runnable action) {
        this.label = label;
        this.action = action;
    }
    
    public String getLabel() { return label; }
    public Runnable getAction() { return action; }
}

// Main Class
public class GestorTorneos{
    public static void main(String[] args) {
        Menu mainMenu = new MainMenu();
        while (true) {
            mainMenu.display();
            mainMenu.navigate();
        }
    }
}
