package main;


import javax.swing.JFrame;
import javax.swing.JOptionPane;

import main.rules.Chess960Rule;
import main.rules.GameRule;
import main.rules.HazardArenaRule;
import main.rules.KingOfTheHillRule;
import main.rules.StandardRule;

public class main {
    public static void main(String[] args) {


        // Demonstrate dynamic binding before launching the game
        System.out.println("Running OOP principle demonstrations...");
        PolymorphismDemo.runDemo();

        // NEW: Demonstrate multityping
        MultitypingDemo.runDemo();

        System.out.println("\nRunning Coercion Polymorphism...");
        // Create a position
        Position knightPos = new Position(3, 3);

        // Move using decimal values that will be coerced to integers
        Position newPos = knightPos.offset(1.7, 2.3);  // Will become (1+3=4 , 2+3=5) - decimals are truncated

        // The values 1.7 and 2.3 are automatically truncated to integers 1 and 2
        System.out.println("Knight moves from " + knightPos + " to " + newPos);



// =====================================================================================================================
        System.out.println("\nLaunching chess game...");

        //Create a JFrame Window
        JFrame window = new JFrame("Dorsa Chess Game");

        //Set Close Operation
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Prevent Resizing
        window.setResizable(false);

        // Customization layer: let the player pick a rule variant /
        // scenario before the board is built. This is also what gives
        // the project the "levels / scenarios" the course asked for.
        // Every entry here is stored through the GameRule interface -
        // main() never needs to know which concrete class it picked.
        GameRule[] availableRules = {
                new StandardRule(),
                new KingOfTheHillRule(),
                new Chess960Rule(),
                new HazardArenaRule()
        };
        String[] ruleLabels = new String[availableRules.length];
        for (int i = 0; i < availableRules.length; i++) {
            ruleLabels[i] = availableRules[i].getName() + "  -  " + availableRules[i].getDescription();
        }
        // A dropdown (showInputDialog with an options array) instead of a
        // row of buttons: with four fairly long labels, a button-per-row
        // dialog can end up wider than the screen and hide the later
        // options. A dropdown always fits regardless of label count/length.
        Object selectedLabel = JOptionPane.showInputDialog(
                null,
                "Choose a rule variant / scenario:",
                "Dorsa Chess Game - Setup",
                JOptionPane.QUESTION_MESSAGE,
                null,
                ruleLabels,
                ruleLabels[0]
        );
        int choice = -1;
        if (selectedLabel != null) {
            for (int i = 0; i < ruleLabels.length; i++) {
                if (ruleLabels[i].equals(selectedLabel)) {
                    choice = i;
                    break;
                }
            }
        }
        GameRule chosenRule = (choice >= 0) ? availableRules[choice] : availableRules[0];
        System.out.println("Selected rule variant: " + chosenRule.getName());

        //Add main.GamePanel to the window [ composition with GamePanel class ]
        GamePanel gp = new GamePanel(chosenRule);
        window.add(gp);

        //Automatically sizes the window
        window.pack();

        //Center the Window
        window.setLocationRelativeTo(null);

        //Show the Window
        window.setVisible(true);

        //Start the Game
        gp.launchGame();


    }
}