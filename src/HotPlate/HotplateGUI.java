package HotPlate;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.Timer;

/**
 *
 * @author ricjo
 */
public class HotplateGUI extends JPanel implements ActionListener, ChangeListener, MouseListener, MouseMotionListener {

    // GUI
    private DrawPanel drawPanel;
    private JSlider tempSlider;
    private JSlider heatConstSlider;

    // Initial temperature and heat constant.
    private int initialSliderTemp = 500;
    private double heatConst;

    // Rectangle coordinates.
    private int X;
    private int Y;
    
    // Rows and cols;
    private int NUM_ROWS = 20;
    private int NUM_COLS = 20;

    // Variable width and height of rectangle representations.
    private int rectangleWidth = 0;
    private int rectangleHeight = 0;
    private Timer timer;

    // Instantiate 2D array that will hold objects of type Element.
    private Element[][] elementArray;

    // Constructor
    public HotplateGUI() {

        // Inherit all JPanel fields/methods.
        super();

        // Set GUI layout to BorderLayout.
        setLayout(new BorderLayout());

        // Instantiate element objects, add neighbours and start their own threads.
        this.populateArray();
        this.initialiseElements();

        // Instantiate drawPanel object.
        drawPanel = new DrawPanel();

        // Temp slider.
        tempSlider = new JSlider();
        tempSlider.setMaximum(1000);
        tempSlider.setValue(500);
        tempSlider.setMajorTickSpacing(100);
        tempSlider.setMinorTickSpacing(20);
        tempSlider.setPaintTicks(true);

        // Heat constant slider.
        heatConstSlider = new JSlider();
        heatConstSlider.setMaximum(100); // divide by 100
        heatConstSlider.setMinimum(1); // divide by 100

        heatConstSlider.setMajorTickSpacing(10);
        heatConstSlider.setMinorTickSpacing(2);
        heatConstSlider.setPaintTicks(true);

        // Add event listeners.
        tempSlider.addChangeListener(this);
        heatConstSlider.addChangeListener(this);
        drawPanel.addMouseMotionListener(this);
        drawPanel.addMouseListener(this);

        // Instantiate panels.
        // Parent panel.
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Children panel.
        // Panel for temperature slider.
        JPanel panel1 = new JPanel();
        panel1.setBorder(BorderFactory.createTitledBorder
        ("Temperature slider 0-1000"));
        // Panel for heat constant slider.
        JPanel panel2 = new JPanel();
        panel2.setBorder(BorderFactory.createTitledBorder
        ("Heat constant slider 0.01-1"));
        
        // Add panels.
        panel1.add(tempSlider);
        panel2.add(heatConstSlider);
        panel.add(panel1);
        panel.add(panel2);

        // Construct panels.
        add(panel, BorderLayout.SOUTH);
        add(drawPanel, BorderLayout.CENTER);

        // Start timer.
        timer = new Timer(20, this);
        timer.start();

    }

    // Event handlers.
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();

        if (source == tempSlider) {
            initialSliderTemp = source.getValue();
            System.out.println(initialSliderTemp);
        } else {
            heatConst = source.getValue();
            heatConst /= 100;
            Element.heatConstant = heatConst;
            System.out.println(heatConst);
        }

    }

    /* MOUSE MOTION */
    @Override
    public void mouseDragged(MouseEvent e) {

        // Change temperature of elements while dragging.
        X = (e.getX() / rectangleWidth);
        Y = (e.getY() / rectangleHeight);
        try {
            elementArray[X][Y].applyTemperature(initialSliderTemp);
            System.out.println("X: " + X + " Y: " + Y);

        } catch (ArrayIndexOutOfBoundsException err) {
            System.out.println("OUT OF BOUNDS!");
        }

        drawPanel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    /* MOUSE ACTIONS */
    @Override
    public void mousePressed(MouseEvent e) {

        // Change temperature of elements when pressed.
        this.mouseDragged(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /* TIMER */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == timer) {
            drawPanel.repaint();
        }
    }

    // Populate array with Element objects.
    public void populateArray() {

        // Populate and instantiate elements.
        elementArray = new Element[NUM_ROWS][NUM_COLS];
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                elementArray[i][j] = new Element(0.0);
            }
        }
    }

    // Add neighbours to individual Elements in array and start individual threads.
    public void initialiseElements() {

        // Add neighbours 
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {

                // Corner - top left
                if (i == 0 && j == 0) {
                    elementArray[i][j].addNeighbour(elementArray[i][j + 1]);
                    elementArray[i][j].addNeighbour(elementArray[i + 1][j]);
                    elementArray[i][j].start();

                    // Corner - bottom left
                } else if (i == 0 && j == 19) {
                    elementArray[i][j].addNeighbour(elementArray[i + 1][j]);
                    elementArray[i][j].addNeighbour(elementArray[i][j - 1]);
                    elementArray[i][j].start();

                    // Corner - top right
                } else if (i == 19 && j == 0) {
                    elementArray[i][j].addNeighbour(elementArray[i][j + 1]);
                    elementArray[i][j].addNeighbour(elementArray[i - 1][j]);
                    elementArray[i][j].start();

                    // Corner - bottom right    
                } else if (i == 19 && j == 19) {
                    elementArray[i][j].addNeighbour(elementArray[i][j - 1]);
                    elementArray[i][j].addNeighbour(elementArray[i - 1][j]);
                    elementArray[i][j].start();

                    // Sides - top  
                } else if (i != 0 && j == 0) {
                    elementArray[i][j].addNeighbour(elementArray[i - 1][j]);
                    elementArray[i][j].addNeighbour(elementArray[i][j + 1]);
                    elementArray[i][j].addNeighbour(elementArray[i + 1][j]);
                    elementArray[i][j].start();
                    // Sides - left    
                } else if (i == 0 && j != 0) {
                    elementArray[i][j].addNeighbour(elementArray[i][j - 1]);
                    elementArray[i][j].addNeighbour(elementArray[i][j + 1]);
                    elementArray[i][j].addNeighbour(elementArray[i + 1][j]);
                    elementArray[i][j].start();

                    // Sides - bottom
                } else if (i != 0 && j == 19) {
                    elementArray[i][j].addNeighbour(elementArray[i - 1][j]);
                    elementArray[i][j].addNeighbour(elementArray[i + 1][j]);
                    elementArray[i][j].addNeighbour(elementArray[i][j - 1]);
                    elementArray[i][j].start();

                    // Sides - right
                } else if (i == 19 && j != 0) {
                    elementArray[i][j].addNeighbour(elementArray[i][j + 1]);
                    elementArray[i][j].addNeighbour(elementArray[i][j - 1]);
                    elementArray[i][j].addNeighbour(elementArray[i - 1][j]);
                    elementArray[i][j].start();
                } else {
                    elementArray[i][j].addNeighbour(elementArray[i][j + 1]);
                    elementArray[i][j].addNeighbour(elementArray[i][j - 1]);
                    elementArray[i][j].addNeighbour(elementArray[i + 1][j]);
                    elementArray[i][j].addNeighbour(elementArray[i - 1][j]);
                    elementArray[i][j].start();
                }
            }
        }
    }

    // Drawing panel inner-class.
    public class DrawPanel extends JPanel {

        public DrawPanel() {
            setPreferredSize(new Dimension(500, 500));
            setBackground(Color.RED);
        }

        @Override
        public void paintComponent(Graphics g) {

            super.paintComponent(g);
            rectangleWidth = (getWidth() / NUM_ROWS);
            rectangleHeight = (getHeight() / NUM_COLS);

            // Draw elements.
            for (int i = 0; i < NUM_ROWS; i++) {
                for (int j = 0; j < NUM_COLS; j++) {
                    elementArray[i][j].drawElement(g, i * rectangleWidth
                            , j * rectangleHeight, rectangleWidth - 1
                            , rectangleHeight - 1);
                }
            }
        }
    }

    // App entry point.
    public static void main(String[] args) {
        JFrame frame = new JFrame("HotplateGUI - 16938857");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new HotplateGUI());
        frame.pack();
        frame.setVisible(true);

    }
}
