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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
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

    boolean startWith(Node node) {
        return node1.equals(node);
    }

    boolean endWith(Node node) {
        return node2.equals(node);
    }

    Edge revert() {
        return new Edge(node2, node1);
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

    protected List<Edge> edges = new ArrayList<>();

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

    Node node(int col, int row) {
        for (Node node : this) {
            if (node.x == col && node.y == row) {
                return node;
            }
        }
        return null;
    }

    Rectangle nodeBound(Node node) {
        Point p = pointToView(node);
        return new Rectangle(p.x - node_size / 2 , p.y - node_size / 2, node_size, node_size);
    }

    Point pointToView(Point p) {
        return new Point(p.x * edge_size + xOffset, p.y * edge_size + yOffset);
    }

    Rectangle edgeBound(Edge edge) {
        Point p1 = pointToView(edge.node1);
        Point p2 = pointToView(edge.node2);
        int x = Math.min(p1.x, p2.x) - node_size / 2 + (p1.y == p2.y ? node_size : 0);
        int y = Math.min(p1.y, p2.y) - node_size / 2 + (p1.x == p2.x ? node_size : 0);
        int w = p1.x == p2.x ? node_size : Math.abs(p1.x - p2.x) - node_size;
        int h = p1.y == p2.y ? node_size : Math.abs(p1.y - p2.y) - node_size;

        return new Rectangle(x, y, w, h);
    }

    public void paint(Graphics g) {

        if (drawEdge) {
            g.setColor(edgeColor);
            for (Edge e : edges) {
                Point p1 = pointToView(e.node1);
                Point p2 = pointToView(e.node2);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
        if (drawNode) {
            g.setColor(nodeColor);
            for (Node n : this) {
                Rectangle r = nodeBound(n);
                g.fillRect(r.x, r.y, r.width, r.height);
            }
        }

    }

    void nodeClick(Node node) {
        System.out.println("nodeClick " + node);
    }

    void edgeClick(Edge edge) {
        System.out.println("edgeClick " + edge);
    }

    private List<ChangeListener> listeners = new ArrayList<>();

    public void addChangeListener(ChangeListener e) {
        listeners.add(e);
    }

    public void removeChangeListener(ChangeListener e) {
        listeners.remove(e);
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
            add(x, y1, x + 1, y1);
        }

        for (int y = y1; y < y2; y++) {
            add(x2, y, x2, y + 1);
        }
    }

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
            }
        }
    }

    public void write(OutputStream out) throws Exception {
        try (OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8")) {
            for (Node node : this) {
                writer.write("node = " + node.x + ", " + node.y + "\n");
            }
            for (Edge edge : edges) {
                writer.write("edge = " + edge.node1.x + ", " + edge.node1.y + ", " + edge.node2.x + ", " + edge.node2.y + "\n");
            }
        }
    }

    Map<Direction, Edge> room(Node node) {
        Map map = new HashMap();

        //  north
        Node n1 = new Node(node);
        Node n2 = new Node(node.x + 1, node.y);
        map.put(Direction.SN, edge(n1, n2));

        // west 
        n2 = new Node(node.x, node.y + 1);
        map.put(Direction.EW, edge(n1, n2));

        // south
        n1 = new Node(node.x + 1, node.y + 1);
        map.put(Direction.NS, edge(n1, n2));

        // east
        n2 = new Node(node.x + 1, node.y);
        map.put(Direction.WE, edge(n1, n2));
        return map;
    }
    
    public Edge edgeAt(Point p) {
        for (Edge e : edges) {
            if (edgeBound(e).contains(p)) {
                return e;
            }
        }
        return null;
    }

    public Node nodeAt(Point p) {
        for (Node node : this) {
            if (nodeBound(node).contains(p)) {
                return node;
            }
        }
        return null;
    }

    

}

interface Sensor {

    public Node nextExists(Node position, Direction d);
}

class MouseIcons extends HashMap<Direction, ImageIcon> {

    int width = 10;
    int height = 10;

    ImageIcon createIcon(Direction direction) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        int[] xPoints;
        int[] yPoints;

        switch (direction) {
            case WE:
                xPoints = new int[]{0, width, 0};
                yPoints = new int[]{0, height / 2, height};
                break;
            case NS:
                xPoints = new int[]{0, width / 2, width};
                yPoints = new int[]{0, height, 0};
                break;
            case EW:
                xPoints = new int[]{width, 0, width};
                yPoints = new int[]{0, height / 2, height};
                break;
            case SN:
                xPoints = new int[]{0, width / 2, width};
                yPoints = new int[]{height, 0, height};
                break;
            default:
                return null;
        }

        g.setColor(Color.red);
        g.fillPolygon(xPoints, yPoints, 3);

        return new ImageIcon(image);

    }

    public MouseIcons() {
        for (Direction d : Direction.values()) {
            put(d, createIcon(d));
        }
    }

}

class MouseGraph extends Graph {

    Mouse mouse;
    MouseIcons icons = new MouseIcons();

    public MouseGraph(Mouse mouse) {
        this.mouse = mouse;
        edgeColor = Color.ORANGE;
        nodeColor = Color.ORANGE;
        xOffset = edge_size / 2;
        yOffset = edge_size / 2;
        drawEdge = true;
        drawNode = true;
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        g.setColor(Color.red);

        ImageIcon icon = icons.get(mouse.direction);
        icon.paintIcon(null, g, mouse.position.x * edge_size + xOffset - icon.getIconWidth() / 2, mouse.position.y * edge_size + yOffset - icon.getIconHeight() / 2);

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

    Graph graph = new MouseGraph(this);

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
            statusText = "step";
            return true;
        }

        for (Direction d : Direction.values()) {
            n = sensor.nextExists(position, d);
            if (n != null && !graph.contains(n)) {
                direction = d;
                statusText = "rotate";
                return true;
            }
        }

        while (!stack.isEmpty()) {
            position = stack.remove(0);
            for (Direction d : Direction.values()) {
                n = sensor.nextExists(position, d);
                if (n != null && !graph.contains(n)) {
                    statusText = "back";
                    return true;
                }
            }
        }

        statusText = "stop";
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
        statusText = "left";
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
        statusText = "right";

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

}

class MazeListener extends MouseAdapter {

    Graph graph;
    Node start, stop;

    public MazeListener(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (start != null) {
            Node node = graph.nodeAt(e.getPoint());
            if (node != null && node != stop) {
                stop = node;
                System.out.println("" + stop);

            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        stop = graph.nodeAt(e.getPoint());
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
        edge = graph.edgeAt(e.getPoint());
        if (edge != null) {
            graph.edgeClick(edge);
        }
        start = graph.nodeAt(e.getPoint());
        if (start != null) {
            graph.nodeClick(start);
        }
        graph.change();
    }
}

class Maze extends Graph implements Sensor {

    int width;

    int height;

    @Override
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

        if (room(position).get(d) == null) {
            return n;
        }

        return null;
    }

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

    protected abstract void doCommand(String command);

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

    private List<Graph> grapList = new ArrayList<>();

    public void add(Graph graph) {
        grapList.add(graph);
    }

    public void remove(Graph graph) {
        grapList.remove(graph);
    }

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

class MazeTools extends CommandManager {

    private static final String TOOL1 = "tool1";
    private static final String TOOL2 = "tool2";
    private static final String TOOL3 = "tool3";

    App app;

    public MazeTools(App app) {
        super(TOOL1, TOOL2, TOOL3);
        this.app = app;
    }

    void tool1() {

    }

    class Node2 extends Node {

        int weight;

        public Node2(Point point, int weight) {
            super(point);
            this.weight = weight;
        }

    }



    void tool2() {

    }

    class GraphExt extends Graph {
        
        Graph source;

        public GraphExt(Graph source) {
            this.source = source;
            nodeColor = Color.BLUE;
            edgeColor = Color.BLUE;
            xOffset = edge_size / 2;
            yOffset = edge_size / 2;
        }
        List<Node> children( Node node) {
            List list = new ArrayList();

            for (Edge edge : source.edges) {
                if (edge.node1.equals(node)) {
                    list.add(edge.node2);
                }
                if (edge.node2.equals(node)) {
                    list.add(edge.node1);
                }
            }
            return list;
        }

        int childrenCount(Node node) {
            int result = 0;
            for (Edge edge : source.edges) {
                if (edge.contain(node)) {
                    result++;
                }
            }
            return result;
        }

        void recur(Node node, List<Node> visited) {            
            for (Node child : children(node)) {
                if (!visited.contains(child)) {
                    visited.add(child);
                    recur(child, visited);
                }
            }
        }
        
        List<Node> path(int col,int row){
            Node node = source.node(col, row);
            List<Node> visited = new ArrayList<>();
            recur(node, visited);
            return visited;
        }
        
        void print(List<Node> list) {
            for (Node node : list) {
                System.out.print(node + " ");
            }
            System.out.println("");
        }

    }

    void tool3() {

        Graph graph = app.mouse.graph;

        GraphExt g = new GraphExt(graph);
        
        List<Node> visited = g.path(0,0);
        
        g.print(visited);
        
        for (int i = 1; i < visited.size(); i++) {
            g.add(new Edge(visited.get(i - 1), visited.get(i)));
        }
        app.brouser.add(g);
        app.brouser.repaint();

    }

    @Override
    public void doCommand(String command) {
        switch (command) {
            case TOOL1:
                tool1();
                break;
            case TOOL2:
                tool2();
                break;
            case TOOL3:
                tool3();
                break;
        }
    }

}

class GraphTree extends JTree {

    public GraphTree() {
        setPreferredSize(new Dimension(200, 200));
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

    public Maze maze = new Maze(16, 16);
    MouseAdapter mazeListener = new MazeListener(maze);
    MazeControl mazeControl = new MazeControl(maze);

    public Mouse mouse = new Mouse(maze);
    MouseControl mouseControl = new MouseControl(mouse);

    CommandManager mazeTool = new MazeTools(this);

    public Brouser brouser = new Brouser();

    StatusBar statusBar = new StatusBar();

    public JMenuBar menuBar = new JMenuBar();

    GraphTree tree = new GraphTree();

    public App() {
        super(new BorderLayout());

        brouser.setPreferredSize(maze.preferredSize());
        brouser.add(maze);
        brouser.add(mouse.graph);

        brouser.addMouseListener(mazeListener);
        brouser.addMouseMotionListener(mazeListener);
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
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new JScrollPane(cover));
        splitPane.setRightComponent(new JScrollPane(tree));
        splitPane.setResizeWeight(1.0);
        add(splitPane);
//        add(new JScrollPane(cover));
        add(mouseControl.controlBar(), BorderLayout.PAGE_START);
        add(statusBar, BorderLayout.PAGE_END);

        menuBar.add(mazeControl.getMenu("Maze"));
        menuBar.add(mazeTool.getMenu("Tools"));
    }

    String title = "MicroMouse v2.0";

    private void execute() {
        JFrame frame = new JFrame(title);
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
