package micromouse2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author viljinsky
 */
enum Direction {
    WE, NS, EW, SN;

    public Direction left() {
        switch (this) {
            case WE:
                return SN;
            case SN:
                return EW;
            case EW:
                return NS;
            case NS:
                return WE;
        }
        return this;
    }

    public Direction right() {
        switch (this) {
            case WE:
                return NS;
            case NS:
                return EW;
            case EW:
                return SN;
            case SN:
                return WE;
        }
        return this;
    }
};

class Node extends Point {

    public Node(Point point) {
        super(point);
    }

    public Node(int i, int i1) {
        super(i, i1);
    }

    @Override
    public String toString() {
        return "node = " + x + " " + y;
    }

}

class Edge {

    Node node1;
    Node node2;

    public Edge(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    @Override
    public String toString() {
        return "edge = " + node1.x + " " + node1.y + " " + node2.x + " " + node2.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof Edge) {
            Edge e = (Edge) obj;
            return (node1.equals(e.node1) && node2.equals(e.node2)) || (node1.equals(e.node2) && node2.equals(e.node1));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.node1);
        hash = 37 * hash + Objects.hashCode(this.node2);
        return hash;
    }

    public boolean contain(Node node) {
        return node != null && (node.equals(node1) || node.equals(node2));
    }

}

public class Graph extends ArrayList<Node> {

    public int edge_size = 30;

    public int node_size = 4;

    public int xOffset = 0;

    public int yOffset = 0;

    public Color edgeColor = Color.LIGHT_GRAY;

    public Color nodeColor = Color.LIGHT_GRAY;

    public boolean drawNode = true;

    public boolean drawEdge = true;

    List<Edge> edges = new ArrayList<>();

    @Override
    public void clear() {
        edges.clear();
        super.clear();
    }

    Edge edge(Node node1, Node node2) {
        for (Edge e : edges) {
            if ((e.node1.equals(node1) && e.node2.equals(node2)) || (e.node1.equals(node2) && e.node2.equals(node1))) {
                return e;
            }
        }
        return null;
    }

    public void paint(Graphics g) {

        if(drawEdge){
        g.setColor(edgeColor);
        for (Edge e : edges) {
            Point p1 = new Point(e.node1.x * edge_size + xOffset, e.node1.y * edge_size + yOffset);
            Point p2 = new Point(e.node2.x * edge_size + xOffset, e.node2.y * edge_size + yOffset);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        }
        if(drawNode){
        g.setColor(nodeColor);
        for(Node n:this){
            Rectangle r = nodeBound(n);
            g.fillRect(r.x+xOffset,r.y+yOffset,r.width,r.height);
        }
        }
        
    }

    Node node(int col, int row) {
        for (Node node : this) {
            if (node.x == col && node.y == row) {
                return node;
            }
        }
        return null;
    }

    Rectangle nodeBound(Node node) {
        return new Rectangle(node.x * edge_size - node_size / 2, node.y * edge_size - node_size / 2, node_size, node_size);
    }

    Rectangle edgeBound(Edge edge) {
        Point p1 = new Point(edge.node1.x * edge_size, edge.node1.y * edge_size);
        Point p2 = new Point(edge.node2.x * edge_size, edge.node2.y * edge_size);
        int x = Math.min(p1.x, p2.x) - node_size / 2 + (p1.y == p2.y ? node_size : 0);
        int y = Math.min(p1.y, p2.y) - node_size / 2 + (p1.x == p2.x ? node_size : 0);
        int w = p1.x == p2.x ? node_size : Math.abs(p1.x - p2.x) - node_size;
        int h = p1.y == p2.y ? node_size : Math.abs(p1.y - p2.y) - node_size;

        return new Rectangle(x, y, w, h);
    }

    void nodeClick(Node node) {
        System.out.println("nodeClick " + node);
    }

    void edgeClick(Edge edge) {
        System.out.println("edgeClick " + edge);
    }

    List<ChangeListener> listeners = new ArrayList<>();

    public void addChangeListener(ChangeListener changeListener) {
        listeners.add(changeListener);
    }

    public void change() {
        for (ChangeListener listener : listeners) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }

    void add(Edge e) {
        add(e.node1, e.node2);
    }

    void add(int x1, int y1, int x2, int y2) {
        add(new Node(x1, y1), new Node(x2, y2));
    }

    void add(Node node1, Node node2) {
        if (!contains(node1)) {
            add(node1);
        }
        if (!contains(node2)) {
            add(node2);
        }

        edges.add(new Edge(node1, node2));
        change();
    }

    void wall(Node node1, Node node2) {
        int x1 = Math.min(node1.x, node2.x);
        int y1 = Math.min(node1.y, node2.y);
        int x2 = Math.max(node1.x, node2.x);
        int y2 = Math.max(node1.y, node2.y);

        for (int x = x1; x < x2; x++) {
            add(node(x, y1), node(x + 1, y1));
        }

        for (int y = y1; y < y2; y++) {
            add(node(x2, y), node(x2, y + 1));
        }
    }

    ;

    public void read(InputStream input) throws Exception {
        clear();
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(input, "utf-8")) {
            char[] buf = new char[1000];
            int count;
            while ((count = reader.read(buf)) > 0) {
                stringBuilder.append(new String(buf, 0, count));
            }
        }
        String[] strings = stringBuilder.toString().split("\n");
        for (String line : strings) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] args = line.trim().split("=");
            String key = args[0].trim();

            Object[] a = Arrays.asList(args[1].split(",")).stream().map(v -> {
                return Integer.valueOf(v.trim());
            }).toArray();

            switch (key) {
                case "node":
                    add(new Node((Integer) a[0], (Integer) a[1]));
                    break;
                case "edge":
                    add((Integer) a[0], (Integer) a[1], (Integer) a[2], (Integer) a[3]);
//                    add(node((Integer) a[0], (Integer) a[1]), node((Integer) a[2], (Integer) a[3]));
            }
        }
    }

    public void write(OutputStream out) throws Exception {
        try (OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8")) {
            for (Node node : this) {
                writer.write("node = " + node.x + ", " + node.y + "\n");
            }
            for (Edge edge : edges) {
                writer.write("edge =" + edge.node1.x + ", " + edge.node1.y + ", " + edge.node2.x + ", " + edge.node2.y + "\n");
            }
        }
    }

}

class Room extends HashMap<Direction, Edge> {

    public Room(Maze maze, Node position) {
        Node n1 = new Node(position);
        Node n2 = new Node(position.x + 1, position.y);
        //  north
        put(Direction.SN, maze.edge(n1, n2));
        n2 = new Node(position.x, position.y + 1);
        // west 
        put(Direction.EW, maze.edge(n1, n2));

        n1 = new Node(position.x + 1, position.y + 1);
        // south
        put(Direction.NS, maze.edge(n1, n2));
        n2 = new Node(position.x + 1, position.y);
        // east
        put(Direction.WE, maze.edge(n1, n2));

    }

    boolean isWall(Direction d) {
        return get(d) != null;
    }

}

class GraphListener extends MouseAdapter {

    Graph graph;
    Node start, stop;

    Edge edgeAt(Point p) {
        for (Edge e : graph.edges) {
            if (graph.edgeBound(e).contains(p)) {
                return e;
            }
        }
        return null;
    }

    Node nodeAt(Point p) {
        for (Node node : graph) {
            if (graph.nodeBound(node).contains(p)) {
                return node;
            }
        }
        return null;
    }

    public GraphListener(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (start != null) {
            Node node = nodeAt(e.getPoint());
            if (node != null && node != stop) {
                stop = node;
                System.out.println("" + stop);

            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        stop = nodeAt(e.getPoint());
        if (edge != null) {
            graph.edges.remove(edge);
        } else if (stop != null && start != null && stop != start) {
            graph.wall(start, stop);
        }
        graph.change();
    }

    Edge edge;

    @Override
    public void mousePressed(MouseEvent e) {
        edge = edgeAt(e.getPoint());
        if (edge != null) {
            graph.edgeClick(edge);
        }
        start = nodeAt(e.getPoint());
        if (start != null) {
            graph.nodeClick(start);
        }
        graph.change();
    }
}

class Sensor {

    Maze maze;

    public Sensor(Maze maze) {
        this.maze = maze;
    }

    public Node nextExists(Node position, Direction d) {
        Node n = new Node(position);
        switch (d) {
            case WE:
                n.x += 1;
                break;
            case NS:
                n.y += 1;
                break;
            case EW:
                n.x -= 1;
                break;
            case SN:
                n.y -= 1;
                break;
            default:
                return null;

        }
        if (n.x < 0 || n.x >= 16 || n.y < 0 || n.y >= 16) {
            return null;
        }

        Room room = new Room(maze, position);
        if (room.isWall(d)) {
            return null;
        }

        return n;
    }

}

class Mouse {

    public int speed = 20;

    private List<ChangeListener> listeners = new ArrayList<>();

    public void change() {
        for (ChangeListener e : listeners) {
            e.stateChanged(new ChangeEvent(this));
        }
    }

    public void addChangeListener(ChangeListener e) {
        listeners.add(e);
    }

    public void removeChangeListener(ChangeListener e) {
        listeners.remove(e);
    }

    String statusText = "unknow";

    Node position = new Node(0, 0);
    Direction direction = Direction.WE;
    List<Node> stack = new ArrayList<>();
    Sensor sensor;

    Graph graph = new Graph() {
        @Override
        public void paint(Graphics g) {
            
            super.paint(g);

            g.setColor(Color.red);

            Rectangle r = new Rectangle(position.x * edge_size + edge_size / 2 - 4, position.y * edge_size + edge_size / 2 - 4, edge_size / 2, edge_size / 2);

            int[] xPoints;
            int[] yPoints;

            switch (direction) {
                case WE:
                    xPoints = new int[]{r.x, r.x + r.width, r.x};
                    yPoints = new int[]{r.y, r.y + r.height / 2, r.y + r.height};
                    break;
                case NS:
                    xPoints = new int[]{r.x, r.x + r.width / 2, r.x + r.width};
                    yPoints = new int[]{r.y, r.y + r.height, r.y};
                    break;
                case EW:
                    xPoints = new int[]{r.x + r.width, r.x, r.x + r.width};
                    yPoints = new int[]{r.y, r.y + r.height / 2, r.y + r.height};
                    break;
                case SN:
                    xPoints = new int[]{r.x, r.x + r.width / 2, r.x + r.width};
                    yPoints = new int[]{r.y + r.height, r.y, r.y + r.height};
                    break;
                default:
                    return;
            }

            g.fillPolygon(xPoints, yPoints, 3);
        }

    };
    

    List<Edge> pathList() {
        List list = new ArrayList();

        Node start = new Node(0, 0);
        boolean f = true;
        while (f) {
            f = false;
            for (Edge e : graph.edges) {
                if (e.contain(start) && !list.contains(e)) {
                    list.add(e);
                    if (start.equals(e.node1)) {
                        start = e.node2;
                        f = true;
                        break;
                    } else {
                        start = e.node1;
                        f = true;
                        break;
                    }

                }
            }
        }
        return list;
    }

    public Mouse(Sensor sensor) {
        this.sensor = sensor;
        graph.add(position);
        graph.edgeColor = Color.ORANGE;
        graph.nodeColor = Color.ORANGE;
        graph.xOffset = graph.edge_size/2;
        graph.yOffset = graph.edge_size/2;
        graph.drawEdge = true;
        graph.drawNode = true;
        
    }

    public boolean forvard() {

        Node n;

        int count = 0;
        for (Direction d : Direction.values()) {
            n = sensor.nextExists(position, d);
            if (n != null && !graph.contains(n)) {
                count += 1;
            }
            if (n != null && graph.contains(n)) {
                Edge e = new Edge(n, position);
                if (!graph.edges.contains(e)) {
                    graph.edges.add(e);
                }
            }
        }
        if (count > 1) {
            stack.add(0, position);
        }

        n = sensor.nextExists(position, direction);

        if (n != null && !graph.contains(n)) {

//            graph.add(n);
            graph.add(position, n);
            position = n;
            return true;
        }

        for (Direction d : Direction.values()) {
            n = sensor.nextExists(position, d);
            if (n != null && !graph.contains(n)) {
                direction = d;
                return true;
            }
        }

        while (!stack.isEmpty()) {
            position = stack.remove(0);
            for (Direction d : Direction.values()) {
                n = sensor.nextExists(position, d);
                if (n != null && !graph.contains(n)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void left() {
        switch (direction) {
            case WE:
                direction = Direction.SN;
                break;
            case SN:
                direction = Direction.EW;
                break;
            case EW:
                direction = Direction.NS;
                break;
            case NS:
                direction = Direction.WE;
                break;
        }
    }

    public void right() {
        switch (direction) {
            case WE:
                direction = Direction.NS;
                break;
            case NS:
                direction = Direction.EW;
                break;
            case EW:
                direction = Direction.SN;
                break;
            case SN:
                direction = Direction.WE;
                break;
        }

    }

    public void reset() {
        graph.clear();
        position = new Node(0, 0);
        direction = Direction.WE;
        graph.add(position);
        stack.clear();
    }

    public Node next() {
        return null;
    }
;

}

class Maze extends Graph {

    int width;
    int height;

    @Override
    public void paint(Graphics g) {
        if (drawNode) {
            g.setColor(nodeColor);
            for (Node node : this) {
                Rectangle bound = nodeBound(node);
                g.drawRect(bound.x, bound.y, bound.width, bound.height);
            }
        }
        if (drawEdge) {
            g.setColor(edgeColor);
            for (Edge edge : edges) {
                Rectangle r = edgeBound(edge);
                g.drawRect(r.x, r.y, r.width, r.height);
            }
        }
    }
    
    

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        for (int col = 0; col < width + 1; col++) {
            for (int row = 0; row < height + 1; row++) {
                add(new Node(col, row));
            }
        }
    }

    public Dimension preferredSize() {
        return new Dimension(width * edge_size + 1, height * edge_size + 1);
    }

    @Override
    public void clear() {
        super.clear();
        for (int col = 0; col < width + 1; col++) {
            for (int row = 0; row < height + 1; row++) {
                add(new Node(col, row));
            }
        }
        change();
    }

    @Override
    public void write(OutputStream out) throws Exception {
        try (OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8")) {
            for (Edge edge : edges) {
                writer.write("edge =" + edge.node1.x + ", " + edge.node1.y + ", " + edge.node2.x + ", " + edge.node2.y + "\n");
            }
        }
    }

    @Override
    public void read(InputStream input) throws Exception {
        clear();
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(input, "utf-8")) {
            char[] buf = new char[1000];
            int count;
            while ((count = reader.read(buf)) > 0) {
                stringBuilder.append(new String(buf, 0, count));
            }
        }
        String[] strings = stringBuilder.toString().split("\n");
        for (String line : strings) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] args = line.trim().split("=");
            String key = args[0].trim();

            Object[] a = Arrays.asList(args[1].split(",")).stream().map(v -> {
                return Integer.valueOf(v.trim());
            }).toArray();

            switch (key) {
//                case "node":
//                    add(new Node((Integer) a[0], (Integer) a[1]));
//                    break;
                case "edge":
                    add((Integer) a[0], (Integer) a[1], (Integer) a[2], (Integer) a[3]);
            }
        }
    }

}

abstract class CommandManager extends ArrayList<Action> {

    public CommandManager() {
    }

    public CommandManager(String... command) {
        for (String s : command) {
            add(createAction(s));
        }
    }

    public abstract void doCommand(String command);

    protected final Action createAction(String command) {
        return new AbstractAction(command) {
            @Override
            public void actionPerformed(ActionEvent e) {
                doCommand(e.getActionCommand());
            }
        };
    }

    public JMenu getMenu(String menuName) {
        JMenu menu = new JMenu(menuName);
        for (Action a : this) {
            menu.add(a);
        }
        return menu;
    }

    public JPanel controlBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Action a : this) {
            panel.add(new JButton(a));
        }
        return panel;
    }

}

class MazeControl extends CommandManager {

    Maze maze;

    private static final String NEW = "new";
    private static final String CLEAR = "clear";
    private static final String READ = "read";
    private static final String WRITE = "write";

    public MazeControl(Maze maze) {
        this.maze = maze;
        add(createAction(READ));
        add(createAction(WRITE));
        add(createAction(CLEAR));
    }

    public void doCommand(String command) {
        File file = new File("maze2.ini");
        try {
            switch (command) {
                case NEW:
                    maze.clear();
                    break;
                case CLEAR:
                    maze.clear();
                    break;
                case READ:
                    maze.read(getClass().getResourceAsStream("/micromouse2/maze2"));
//                    if (file.exists())
//                    try (FileInputStream input = new FileInputStream(file)) {
//                        maze.read(input);
//                    }
                    break;
                case WRITE:
                try (FileOutputStream out = new FileOutputStream(file)) {
                    maze.write(out);
                }
                break;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}

//--------------------------- user unterface -----------------------------------
class MouseControl extends CommandManager {

    Mouse mouse;
    private static final String FORVARD = "forvars";
    private static final String TURN_LEFT = "left";
    private static final String TURN_RIGHT = "right";
    private static final String RESET = "recet";
    private static final String RUN = "run";

    public MouseControl(Mouse mouse) {
        this.mouse = mouse;
        add(createAction(RESET));
        add(createAction(FORVARD));
        add(createAction(TURN_LEFT));
        add(createAction(TURN_RIGHT));
        add(createAction(RUN));

    }

    @Override
    public void doCommand(String command) {
        switch (command) {
            case RUN:
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            while (mouse.forvard()) {
                                mouse.change();
                                long t = System.currentTimeMillis() + mouse.speed;
                                while (t > System.currentTimeMillis()) {
                                }
                            }
                            mouse.position = new Node(0, 0);
                            mouse.direction = Direction.WE;
                            mouse.change();

                            for (Edge p : mouse.pathList()) {
                                System.out.println(p);
                            }

                        } catch (Exception e) {
                            System.err.println("run : " + e.getMessage());
                        }
                    }

                }.start();
                break;

            case RESET:
                mouse.reset();
                mouse.change();
                break;
            case FORVARD:
                mouse.forvard();
                mouse.change();
                break;
            case TURN_LEFT:
                mouse.left();
                mouse.change();
                break;
            case TURN_RIGHT:
                mouse.right();
                mouse.change();
                break;

        }
    }
}

class StatusBar extends JPanel {

    JLabel label = new JLabel("status");

    public StatusBar() {
        super(new FlowLayout(FlowLayout.LEFT, 1, 1));
        add(label);
    }

    public void setStatusText(String text) {
        label.setText(text);
    }

}

class Brouser extends JPanel implements ChangeListener {

    List<Graph> grapList = new ArrayList<>();

    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (Graph graph : grapList) {
            graph.paint(g);
        }
    }

    public Brouser() {
        setBackground(Color.WHITE);
    }

}

class App extends JPanel implements WindowListener {

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    Maze maze = new Maze(16, 16);
    Mouse mouse = new Mouse(new Sensor(maze));
    MouseControl mouseControl = new MouseControl(mouse);
    MazeControl mazeControl = new MazeControl(maze);

    CommandManager mazeTools = new CommandManager("cmd1") {
        @Override
        public void doCommand(String command) {
            Graph g = new Graph();
            g.edgeColor = Color.BLUE;
            g.nodeColor = Color.BLUE;
            g.xOffset = g.edge_size/2;
            g.yOffset = g.edge_size/2;
            for (Edge edge : mouse.pathList()) {
                g.add(edge);
                brouser.grapList.add(g);
                brouser.repaint();
            }
        }
    };

    Brouser brouser = new Brouser();
    StatusBar statusBar = new StatusBar();

    JMenuBar menuBar = new JMenuBar();

    public App() {
        super(new BorderLayout());

        brouser.setPreferredSize(maze.preferredSize());
        brouser.grapList.add(maze);
        brouser.grapList.add(mouse.graph);

        GraphListener graphListener = new GraphListener(maze);
        brouser.addMouseListener(graphListener);
        brouser.addMouseMotionListener(graphListener);
        maze.addChangeListener(brouser);
        mouse.addChangeListener(e -> {
            Mouse m = (Mouse) e.getSource();
            brouser.repaint();
            statusBar.setStatusText(m.direction + " " + m.position.toString() + " " + m.stack.size() + " " + m.statusText);
        });

        File file = new File("maze2.ini");
        if (file.exists()) {
            try {
                maze.read(new FileInputStream(file));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        } else {
            try {
                maze.read(getClass().getResourceAsStream("/micromouse2/maze2"));
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        JPanel cover = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 7));
        cover.add(brouser);
        add(new JScrollPane(cover));
        add(mouseControl.controlBar(), BorderLayout.PAGE_START);
        add(statusBar, BorderLayout.PAGE_END);

        menuBar.add(mazeControl.getMenu("Maze"));
        menuBar.add(mazeTools.getMenu("Tools"));
    }

    private void execute() {
        JFrame frame = new JFrame("MicroMouse v2.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(menuBar);
        frame.addWindowListener(this);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new App().execute();
    }

}
