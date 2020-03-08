package HotPlate;

import java.util.*;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author ricjo
 */
public class Element implements Runnable {

    /* DECLARE FIELDS */
    private List<Element> neighbours;
    private double currentTemp;
    // Static field that is shared by all Element objects.
    public static double heatConstant;
    private boolean stopRequested;

    /* CLASS METHODS */
    // Constructor for the GUI.
    public Element(double currentTemp) {
        this.currentTemp = currentTemp;
        this.heatConstant = 0.5;
        neighbours = new ArrayList<>();
    }

    // Overload constructor for the main method of this class.
    public Element(double currentTemp, double heatConstant) {
        this.currentTemp = currentTemp;
        // Set heatConstant to 0.05 for this instance.
        this.heatConstant = 0.05;
        neighbours = new ArrayList<>();
    }

    // Start method runs an Element instance in its own thread.
    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    // Grabs current temperature. Accesses temperature so method is syncd.
    public synchronized double getTemperature() {
        return currentTemp;
    }

    // Stops process.
    public void requestStop() {
        stopRequested = true;
    }

    // Run method compares average temperatures of its neighbours with its own
    // temperature and adjusts its current temperature using formulas given before
    // sleeping for a small period of time.
    @Override
    public void run() {
        stopRequested = false;
        while (!stopRequested) {
            // Handle temperature.
            double averageTemps;
            double totalTemps = 0.0;
            int totalElements;

            // Find element size
            totalElements = neighbours.size();

            // Find total temperature in neighbours list.
            for (int i = 0; i < totalElements; i++) {
                totalTemps += neighbours.get(i).getTemperature();
            }

            averageTemps = totalTemps / totalElements;
            currentTemp += (averageTemps - currentTemp) * heatConstant;

            try {
                Thread.sleep(40);
            } catch (Exception e) {
                System.out.println("Error!");
            }
        }
    }

    // Method used to add a neighbouring Element object.
    public void addNeighbour(Element e) {
        neighbours.add(e);
    }

    // "Heaten" up or "cool down" plate.
    public synchronized void applyTemperature(double appliedTemp) {
        currentTemp += (appliedTemp - currentTemp) * heatConstant;
    }

    // Method for drawing rectangular representations of Element objects. 
    public void drawElement(Graphics g, int x, int y, int boxWidth, int boxHeight) {

        // Check if temperature is within under 255.
        int redTemp = Math.min(255, (int) (getTemperature()));
        // Set red values and blue values of the colour.
        g.setColor(new Color(redTemp, 0, 255 - redTemp));
        // Draw rectangle
        g.fillRect(x, y, boxWidth, boxHeight);
    }

    // Driver method.
    public static void main(String[] args) {

        // Instantiate two elements with custom values.
        Element e1 = new Element(300, 0.05);
        Element e2 = new Element(0, 0.05);
        // Add e1 as e2's neighbour & vice-versa.
        e1.addNeighbour(e2);
        e2.addNeighbour(e1);
        // Start their own threads.
        e1.start();
        e2.start();

        // Instantiate timer for periodically showing output of temperature.
        Timer timer = new Timer();
        // Create new timertask w/ anonymous class to start its own run method.
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Current temp: " + e1.getTemperature());
                System.out.println("Current temp: " + e2.getTemperature());
                System.out.println("\n");
                
                // Check when both temperatures are equal around 2 decimal points.
                if ((e1.getTemperature() - e2.getTemperature()) < 0.01) {
                    timer.cancel();
                    e1.requestStop();
                    e2.requestStop();
                    System.out.println("Temperature balanced");
                }
            }

        };
        timer.scheduleAtFixedRate(task, 0, 400);
    }
}
