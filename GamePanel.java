package plantpal.com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    //  screen dimensions
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 5;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75;

    // Arrays for the plant's parts
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];

    // Initial plant parts count
    int plantParts = 40;
    int waterTaken;

    // Coordinates for the water's position
    int waterX;
    int waterY;

    // Initial direction of the snake (Right)
    char direction = 'R';

    // Game status
    boolean running = false;

    // Timer for game and random generator for water position
    Timer timer;
    Random random;

    // Constructor for GamePanel
    GamePanel() {

        random = new Random();

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        //  KeyListener
        this.addKeyListener(new MyKeyAdapter());
        // Start the game
        startGame();
    }

    // Starts the game
    public void startGame() {
        newWater();
        running = true;

        timer = new Timer(DELAY, this);
        timer.start();
    }

    // Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }



    public void draw(Graphics g) {
        if (running) {
            //waterdroplet size
            int dropletWidth = UNIT_SIZE * 2;
            int dropletHeight = UNIT_SIZE * 3;
            g.setColor(Color.blue);
            g.fillOval(waterX - dropletWidth / 4, waterY - dropletHeight / 4, dropletWidth, dropletHeight);

            // plant's head based on waterTaken value
            g.setColor(Color.green);
            g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);

            if (waterTaken > 2) {
                // flower dimensions
                int flowerSize = UNIT_SIZE * 3;
                g.setColor(new Color(255, 105, 180));


                for (int i = 0; i < 8; i++) {
                    double angle = Math.PI / 4 * i;
                    int petalX = (int) (x[0] - flowerSize / 2 + flowerSize / 1.5 * Math.cos(angle));
                    int petalY = (int) (y[0] - flowerSize / 2 + flowerSize / 1.5 * Math.sin(angle));
                    g.fillOval(petalX, petalY, flowerSize / 2, flowerSize / 2); // Petal size
                }
                // center of the flower
                g.setColor(Color.yellow);
                g.fillOval(x[0] - flowerSize / 6, y[0] - flowerSize / 6, flowerSize / 3, flowerSize / 3); // Center of the flower
            } else if (waterTaken > 1) {
                // bud
                int budSize = UNIT_SIZE * 2;
                g.setColor(new Color(0, 128, 0));

                //  petals around the bud center
                for (int i = 0; i < 5; i++) {
                    double angle = Math.PI / 2.5 * i;
                    int petalX = (int) (x[0] - budSize / 2 + budSize / 1.5 * Math.cos(angle));
                    int petalY = (int) (y[0] - budSize / 2 + budSize / 1.5 * Math.sin(angle));
                    g.fillOval(petalX, petalY, budSize / 2, budSize / 2);
                }
                // center of the bud
                g.setColor(Color.green);
                g.fillOval(x[0] - budSize / 4, y[0] - budSize / 4, budSize / 2, budSize / 2);
            } else {
                // seed
                int seedSize = UNIT_SIZE*2;
                g.setColor(new Color(139, 69, 19));
                g.fillOval(x[0] - seedSize / 2, y[0] - seedSize / 2, seedSize, seedSize);
            }

            // plant's body with wiggling effect
            for (int i = 1; i < plantParts; i++) {

                double angle = i * 0.5;
                int offsetX = (int) (Math.sin(angle) * UNIT_SIZE / 2);
                int offsetY = (int) (Math.cos(angle) * UNIT_SIZE / 2);

                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i] + offsetX, y[i] + offsetY, UNIT_SIZE, UNIT_SIZE);

                if (i % 2 == 0) {
                    g.setColor(Color.green);
                    g.fillOval(x[i] + UNIT_SIZE / 2 + offsetX, y[i] + offsetY, UNIT_SIZE / 3, UNIT_SIZE / 5); // Left leaf
                    g.fillOval(x[i] + offsetX, y[i] + UNIT_SIZE / 2 + offsetY, UNIT_SIZE / 3, UNIT_SIZE / 5); // Right leaf
                }
            }


            if (underwater) {
                g.setColor(Color.cyan);
                g.setFont(new Font("Ink Free", Font.BOLD, 30));
                g.drawString("Underwater Level!", SCREEN_WIDTH / 3, 50);
            }


            // Display score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    /* //flower is nice and funny idc anymore
    public void draw(Graphics g) {
        if (running) {
            // Draw water drop with a larger, oval shape for a more droplet-like appearance
            int dropletWidth = UNIT_SIZE * 2;  // Make the droplet wider
            int dropletHeight = UNIT_SIZE * 3; // Make the droplet taller for a teardrop effect
            g.setColor(Color.blue);
            g.fillOval(waterX - dropletWidth / 4, waterY - dropletHeight / 4, dropletWidth, dropletHeight);

            // Draw the plant's head with a flower/bud if waterTaken > 10
            g.setColor(Color.green);
            g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
            if (waterTaken > 2) {
                int flowerSize = UNIT_SIZE * 3; // Flower size
                g.setColor(new Color(255, 105, 180)); // Color for the flower (pink)

                // Draw petals around the flower center
                for (int i = 0; i < 8; i++) { // Create 8 petals
                    double angle = Math.PI / 4 * i; // Angle for each petal
                    int petalX = (int) (x[0] - flowerSize / 2 + flowerSize / 1.5 * Math.cos(angle));
                    int petalY = (int) (y[0] - flowerSize / 2 + flowerSize / 1.5 * Math.sin(angle));
                    g.fillOval(petalX, petalY, flowerSize / 2, flowerSize / 2); // Petal size
                }
                // Draw the center of the flower
                g.setColor(Color.yellow);
                g.fillOval(x[0] - flowerSize / 6, y[0] - flowerSize / 6, flowerSize / 3, flowerSize / 3); // Center of the flower
            }

            // Draw the plant's body with a wiggling effect and leaves
            for (int i = 1; i < plantParts; i++) {
                // Create a sine wave offset for the body parts
                double angle = i * 0.5;  // Adjust the frequency of the wiggle here
                int offsetX = (int) (Math.sin(angle) * UNIT_SIZE / 2); // Adjust amplitude if needed
                int offsetY = (int) (Math.cos(angle) * UNIT_SIZE / 2);

                // Draw each segment with the oscillating offset
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i] + offsetX, y[i] + offsetY, UNIT_SIZE, UNIT_SIZE);

                // Draw leaves on alternating segments
                if (i % 2 == 0) {
                    g.setColor(Color.green);
                    // Draw leaf shapes (more elongated and leaf-like)
                    g.fillOval(x[i] + UNIT_SIZE / 2 + offsetX, y[i] + offsetY, UNIT_SIZE / 3, UNIT_SIZE / 5); // Left leaf
                    g.fillOval(x[i] + offsetX, y[i] + UNIT_SIZE / 2 + offsetY, UNIT_SIZE / 3, UNIT_SIZE / 5); // Right leaf
                }
            }

            // Display score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }
*/
  /*  //deformed flower its so funny =it looks like a round dotted flower
    public void draw(Graphics g) {
        if (running) {
            // Draw water drop with a larger, oval shape for a more droplet-like appearance
            int dropletWidth = UNIT_SIZE * 2;  // Make the droplet wider
            int dropletHeight = UNIT_SIZE * 3; // Make the droplet taller for a teardrop effect
            g.setColor(Color.blue);
            g.fillOval(waterX - dropletWidth / 4, waterY - dropletHeight / 4, dropletWidth, dropletHeight);

            // Draw the plant's head with a flower/bud if waterTaken > 10
            g.setColor(Color.green);
            g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
            if (waterTaken > 2) {
                int flowerSize = UNIT_SIZE * 3; // Flower size
                g.setColor(new Color(255, 105, 180)); // Color for the flower (pink)

                // Draw petals around the flower center
                for (int i = 0; i < 8; i++) { // Create 8 petals
                    double angle = Math.PI / 4 * i; // Angle for each petal
                    int petalX = (int) (x[0] - flowerSize / 2 + flowerSize / 1.5 * Math.cos(angle));
                    int petalY = (int) (y[0] - flowerSize / 2 + flowerSize / 1.5 * Math.sin(angle));
                    g.fillOval(petalX, petalY, flowerSize / 2, flowerSize / 2); // Petal size
                }
                // Draw the center of the flower
                g.setColor(Color.yellow);
                g.fillOval(x[0] - flowerSize / 6, y[0] - flowerSize / 6, flowerSize / 3, flowerSize / 3); // Center of the flower
            }

            // Draw the plant's body with a wiggling effect and leaves
            for (int i = 1; i < plantParts; i++) {
                // Create a sine wave offset for the body parts
                double angle = i * 0.5;  // Adjust the frequency of the wiggle here
                int offsetX = (int) (Math.sin(angle) * UNIT_SIZE / 2); // Adjust amplitude if needed
                int offsetY = (int) (Math.cos(angle) * UNIT_SIZE / 2);

                // Draw each segment with the oscillating offset
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i] + offsetX, y[i] + offsetY, UNIT_SIZE, UNIT_SIZE);

                // Draw leaves on alternating segments
                if (i % 2 == 0) {
                    g.setColor(Color.green);
                    // Draw leaf shapes (more elongated and leaf-like)
                    g.fillOval(x[i] + UNIT_SIZE / 2 + offsetX, y[i] + offsetY, UNIT_SIZE / 3, UNIT_SIZE / 5); // Left leaf
                    g.fillOval(x[i] + offsetX, y[i] + UNIT_SIZE / 2 + offsetY, UNIT_SIZE / 3, UNIT_SIZE / 5); // Right leaf
                }
            }

            // Display score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }
*/
    /*
    //flower looks like a round helmet attached to its head
    public void draw(Graphics g) {
        if (running) {
            // Draw water drop with a larger, oval shape for a more droplet-like appearance
            int dropletWidth = UNIT_SIZE * 2;  // Make the droplet wider
            int dropletHeight = UNIT_SIZE * 3; // Make the droplet taller for a teardrop effect
            g.setColor(Color.blue);
            g.fillOval(waterX - dropletWidth / 4, waterY - dropletHeight / 4, dropletWidth, dropletHeight);

            // Draw the plant's head with a flower/bud if waterTaken > 10
            g.setColor(Color.green);
            g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
            if (waterTaken > 2) {
                int flowerSize = UNIT_SIZE * 3; // Make the flower larger than the body segments
                g.setColor(new Color(255, 105, 180)); // Color for the flower (pink)
                g.fillOval(x[0] - flowerSize / 4, y[0] - flowerSize / 4, flowerSize, flowerSize);
            }

            // Draw the plant's body with a wiggling effect and leaves
            for (int i = 1; i < plantParts; i++) {
                // Create a sine wave offset for the body parts
                double angle = i * 0.5;  // Adjust the frequency of the wiggle here
                int offsetX = (int) (Math.sin(angle) * UNIT_SIZE / 2); // Adjust amplitude if needed
                int offsetY = (int) (Math.cos(angle) * UNIT_SIZE / 2);

                // Draw each segment with the oscillating offset
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i] + offsetX, y[i] + offsetY, UNIT_SIZE, UNIT_SIZE);

                // Draw leaves on alternating segments
                if (i % 2 == 0) {
                    g.setColor(Color.green);
                    g.fillOval(x[i] + UNIT_SIZE / 2 + offsetX, y[i] + offsetY, UNIT_SIZE / 2, UNIT_SIZE / 4); // Left leaf
                    g.fillOval(x[i] + offsetX, y[i] + UNIT_SIZE / 2 + offsetY, UNIT_SIZE / 2, UNIT_SIZE / 4); // Right leaf
                }
            }

            // Display score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }
*/

/*
//small water dot
    public void draw(Graphics g) {
        if (running) {
            // Draw water drop
            g.setColor(Color.blue);
            g.fillOval(waterX, waterY, UNIT_SIZE, UNIT_SIZE);

            // Draw the plant's head with a flower/bud if waterTaken > 10
            g.setColor(Color.green);
            g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
            if (waterTaken > 2) {
                int flowerSize = UNIT_SIZE * 3; // Make the flower larger than the body segments
                g.setColor(new Color(255, 105, 180)); // Color for the flower (pink)
                g.fillOval(x[0] - flowerSize / 4, y[0] - flowerSize / 4, flowerSize, flowerSize);

            }


            // Draw the plant's body with a wiggling effect and leaves
            for (int i = 1; i < plantParts; i++) {
                // Create a sine wave offset for the body parts
                double angle = i * 0.5;  // Adjust the frequency of the wiggle here
                int offsetX = (int) (Math.sin(angle) * UNIT_SIZE / 2); // Adjust amplitude if needed
                int offsetY = (int) (Math.cos(angle) * UNIT_SIZE / 2);

                // Draw each segment with the oscillating offset
                g.setColor(new Color(45, 180, 0));
                g.fillRect(x[i] + offsetX, y[i] + offsetY, UNIT_SIZE, UNIT_SIZE);

                // Draw leaves on alternating segments
                if (i % 2 == 0) {
                    g.setColor(Color.green);
                    g.fillOval(x[i] + UNIT_SIZE / 2 + offsetX, y[i] + offsetY, UNIT_SIZE / 2, UNIT_SIZE / 4); // Left leaf
                    g.fillOval(x[i] + offsetX, y[i] + UNIT_SIZE / 2 + offsetY, UNIT_SIZE / 2, UNIT_SIZE / 4); // Right leaf
                }
            }

            // Display score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }
*/

    /*
    //graphics for body which looks like a dot or increasing straight line
     public void draw(Graphics g) {
         if (running) {
             // Draw the apple
             g.setColor(Color.blue);
             g.fillOval(waterX, waterY, UNIT_SIZE, UNIT_SIZE);

             // Draw the plant's parts
             for (int i = 0; i < plantParts; i++) {
                 if (i == 0) {
                     g.setColor(Color.green); // Head
                     g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                 } else {
                     // Random color for each segment of the body
                     g.setColor(new Color(45, 180, 0));
                     //g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                     g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                 }
             }

             // Display the score on the screen
             g.setColor(Color.red);
             g.setFont(new Font("Ink Free", Font.BOLD, 40));
             FontMetrics metrics = getFontMetrics(g.getFont());
             g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
         } else {
             // Show Game Over screen if the game is not running
             gameOver(g);
         }
     }
 */
    public void newWater() {
        waterX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        waterY = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
    }

    // Move
    public void move() {
        for (int i = plantParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    boolean underwater = false;

    public void checkWater() {
        if ((x[0] == waterX) && (y[0] == waterY)) {
            plantParts++;
            waterTaken++;
            newWater();

            if (waterTaken == 3) {
                underwater = true; // activate underwater mode
                setBackground(new Color(0, 0, 128)); // Change background to underwater color
            } else if (waterTaken == 4) {
                running = false; // Stop the game
            }
        }
    }


    public void checkCollisions() {
        // Check collision with body
        for (int i = plantParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        // Check collision borders
        if (x[0] < 0 || x[0] > SCREEN_WIDTH || y[0] < 0 || y[0] > SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }
    }

    // Displays Game Over on the screen
    public void gameOver(Graphics g) {
        if (waterTaken == 4) {
            g.setColor(Color.blue);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("You've reached the water limit!", (SCREEN_WIDTH - metrics.stringWidth("You've reached the water limit!")) / 2, SCREEN_HEIGHT / 2);
        } else {
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 75));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        }
    }
  /*  public void gameOver(Graphics g) {
  //score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + waterTaken, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + waterTaken)) / 2, g.getFont().getSize());
        // Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }
*/
    // Action performed(move, check water, check collisions)
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkWater();
            checkCollisions();
        }
        repaint();
    }

    // Inner class
    public class MyKeyAdapter extends KeyAdapter {
        // Change direction based on arrow key input
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
            }
        }
    }
}
