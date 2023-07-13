/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package micromouse2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
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
import javafx.scene.layout.Border;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author viljinsky
 */
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

}

class Path extends ArrayList<Node> {
}

public class Graph extends ArrayList<Node> {

    public int edge_size = 30;

    public int node_size = 4;

    List<Edge> edges = new ArrayList<>();

    @Override
    public void clear() {
        edges.clear();
        super.clear(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
    
    
    
    Edge edge(Node node1,Node node2){
        for(Edge e:edges){
            if ((e.node1.equals(node1) && e.node2.equals(node2))||(e.node1.equals(node2)&&e.node2.equals(node1))){
                return e;
            }
        }
        return null;
    }

    public void paint(Graphics g) {
        g.setColor(Color.GRAY);
        for (Node node : this) {
            Rectangle bound = nodeBound(node);
            g.drawRect(bound.x, bound.y, bound.width, bound.height);
        }
        g.setColor(Color.GRAY);
        for (Edge edge : edges) {
            g.drawLine(edge.node1.x * edge_size, edge.node1.y * edge_size, edge.node2.x * edge_size, edge.node2.y * edge_size);
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

    public Rectangle nodeBound(Node node) {
        return new Rectangle(node.x * edge_size - node_size / 2, node.y * edge_size - node_size / 2, node_size, node_size);
    }
    
    public Rectangle edgeBound(Edge edge){
        Point p1 = new Point(edge.node1.x*edge_size,edge.node1.y*edge_size);
        Point p2 = new Point(edge.node2.x*edge_size,edge.node2.y*edge_size);
        int x = Math.min(p1.x, p2.x)- node_size/2;
        int y = Math.min(p1.y, p2.y)- node_size/2;
        int w = p1.x == p2.x ?  node_size : Math.abs(p1.x - p2.x);
        int h = p1.y == p2.y ? node_size: Math.abs(p1.y-p2.y);
        
        return new Rectangle(x,y,w,h);
    }

    public void nodeClick(Node node) {
        throw new UnsupportedOperationException("unsupported yet");
    }

    public void edgeClick(Edge edge) {
        System.out.println("edgeClick "+edge);
//        throw new UnsupportedOperationException("unsupported yet");
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

    public void addEdge(Node node1, Node node2) {
        if (contains(node1) && contains(node2)) {
            Edge edge = new Edge(node1, node2);
            edges.add(edge);
            change();
        }
    }

    public void wall(Node node1, Node node2) {
        int x1 = Math.min(node1.x, node2.x);
        int y1 = Math.min(node1.y, node2.y);
        int x2 = Math.max(node1.x, node2.x);
        int y2 = Math.max(node1.y, node2.y);

        for (int x = x1; x < x2; x++) {
            addEdge(node(x, y1), node(x + 1, y1));
        }

        for (int y = y1; y < y2; y++) {
            addEdge(node(x2, y), node(x2, y + 1));
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
                    addEdge(node((Integer) a[0], (Integer) a[1]), node((Integer) a[2], (Integer) a[3]));
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

class Room {
    
    Edge north;
    Edge east;
    Edge south;
    Edge west;

    public Room(Maze maze,Node position) {
        Node n1 = new Node(position);
        Node n2 = new Node(position.x+1,position.y);
        north = maze.edge(n1, n2);
        n2 = new Node(position.x,position.y+1);
        west = maze.edge(n1, n2);
        
        n1 = new Node(position.x+1,position.y+1);
        south = maze.edge(n1, n2);
        n2 = new Node(position.x+1,position.y);
        east = maze.edge(n1, n2);
        
    }
    
    boolean isWall(Mouse.Direction d){
        switch(d){
            case WE:
                return east !=null;
            case SN:
                return north !=null;
            case EW:
                return west !=null;
            case NS:
                return south !=null;
            default:
                return false;
        }
    }
    
}

class GraphListener extends MouseAdapter {

    Graph graph;
    Node start, stop;
    
    Edge edgeAt(Point p){
        for(Edge edge:graph.edges){
            if (graph.edgeBound(edge).contains(p)){
                return edge;
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
        Node node = nodeAt(e.getPoint());
        if (node != null && node != start) {
            stop = node;
            graph.wall(start, stop);

        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        start = null;
        Edge edge = edgeAt(e.getPoint());
        if (edge!=null){
            graph.edgeClick(edge);
            graph.edges.remove(edge);
            graph.change();
            return;
        }
        Node node = nodeAt(e.getPoint());
        if (node != null) {
            start = node;
        }
    }
}

class Sensor {

    Maze maze;

    public Sensor(Maze maze) {
        this.maze = maze;
    }

    public Node nextExists(Node position, Mouse.Direction d) {
        Node n = new Node(position);
        switch(d){
            case WE:
                n.x+=1;break;
            case NS:
                n.y+=1;break;                
            case EW:
                n.x-=1;break;
            case SN:
                n.y-=1;break;
            default:
                return null;
                
        }
        if (n.x<0 || n.x>=16 || n.y<0 || n.y>=16){
            return null;
        }
        
        Room room = new Room(maze,position);
        if (room.isWall(d)){
            return null;
        }
        
        return n;
    }

}

class Mouse {

    enum Direction {
        WE, NS, EW, SN;
        
        public Direction left(){
            switch(this){
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
        
        public Direction right(){
            switch(this){
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
    
    Node position = new Node(0, 0);
    Direction direction = Direction.WE;
    List<Node> stack = new ArrayList<>();
    Sensor sensor;

    Graph graph = new Graph() {
        @Override
        public void paint(Graphics g) {
            
            g.setColor(Color.ORANGE);
            for(Edge e:edges){
                Point p1 = new Point(e.node1.x*edge_size+edge_size/2,e.node1.y*edge_size+edge_size/2);
                Point p2 = new Point(e.node2.x*edge_size+edge_size/2,e.node2.y*edge_size+edge_size/2);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
            
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

    public Mouse(Sensor sensor) {
        this.sensor = sensor;
        graph.add(position);
    }

    public boolean forvard() {
                
        Node n;
        
        int count = 0;
        for(Direction d:Direction.values()){
            n =sensor.nextExists(position, d);
            if (n!=null && !graph.contains(n)){
                count+=1;
            }            
        }
        if(count>1){
            stack.add(position);
        }
        
        
        n = sensor.nextExists(position, direction);
       
        if (n!=null && !graph.contains(n)) {            
            
            graph.add(n);
            graph.addEdge(position, n);
            position = n;
            return true;
        }
        
        for (Direction d:Direction.values()){
            n = sensor.nextExists(position, d);
            if (n!=null && !graph.contains(n)){
                direction = d;
                return true;
            }
        }
        
        while(!stack.isEmpty()){
            position = stack.remove(0);
            for(Direction d:Direction.values()){
                n = sensor.nextExists(position, d);
                if (n!=null && !graph.contains(n)){
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
    }

    public Node next() {
        return null;
    }
;

}

class Maze extends Graph {

    int width;
    int height;

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
        return new Dimension(width * edge_size, height * edge_size);
    }
}

//--------------------------- user unterface -----------------------------------
class MouseControl extends ArrayList<Action> {

    Mouse mouse;
    private static final String FORVARD = "forvars";
    private static final String TURN_LEFT = "left";
    private static final String TURN_RIGHT = "right";
    private static final String RESET = "recet";
    private static final String RUN = "run";
    //private static final String CLEAR = "clear";

    Action createAction(String command) {
        return new AbstractAction(command) {
            @Override
            public void actionPerformed(ActionEvent e) {
                doCommand(e.getActionCommand());
            }
        };
    }

    public MouseControl(Mouse mouse) {
        this.mouse = mouse;
        add(createAction(RESET));
        add(createAction(FORVARD));
        add(createAction(TURN_LEFT));
        add(createAction(TURN_RIGHT));
        add(createAction(RUN));

    }

    public void doCommand(String command) {
        switch (command) {
            case RUN:
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            while (mouse.forvard()){
                                change();
                                long t = System.currentTimeMillis()+50;
                                while (t>System.currentTimeMillis()){
                                }
                            }
                            System.out.println("finish");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    
                }.start();
                break;
                
            case RESET:
                mouse.reset();
                break;
            case FORVARD:
                mouse.forvard();
                break;
            case TURN_LEFT:
                mouse.left();
                break;
            case TURN_RIGHT:
                mouse.right();
                break;

        }
        change();
    }
    List<ChangeListener> listeners = new ArrayList<>();

    public void change() {
        for (ChangeListener listener : listeners) {
            listener.stateChanged(new ChangeEvent(mouse));
        }
    }
}

class StatusBar extends JPanel {
    JLabel label = new JLabel("status");

    public StatusBar() {
        super(new FlowLayout(FlowLayout.LEFT,1,1));
        add(label);
    }
    
    public void setStatusText(String text){
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
        try {
            File file = new File("maze2.ini");
            maze.write(new FileOutputStream(file));
        } catch (Exception h) {
            h.printStackTrace();
        }
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

    Brouser brouser = new Brouser();
    StatusBar statusBar = new StatusBar();

    public App() {
        brouser.setPreferredSize(maze.preferredSize());
        brouser.grapList.add(maze);
        brouser.grapList.add(mouse.graph);
        
        GraphListener graphListener = new GraphListener(maze);
        brouser.addMouseListener(graphListener);
        brouser.addMouseMotionListener(graphListener);
        maze.addChangeListener(brouser);
        mouseControl.listeners.add(brouser);
        mouseControl.listeners.add(e->{
            Mouse m = (Mouse)e.getSource();
            statusBar.setStatusText(m.direction+" "+m.position.toString()+" "+m.stack.size());
        });
        

        File file = new File("maze2.ini");
        if (file.exists()) {
            try {
                maze.read(new FileInputStream(file));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void execute() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (Action a : mouseControl) {
            panel.add(new JButton(a));
        }
        
        JPanel cover = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 7));
        cover.add(brouser);

        JFrame frame = new JFrame("MicroMouse v2.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(this);
        Container c = frame.getContentPane();
        c.add(new JScrollPane(cover));
        c.add(panel, BorderLayout.PAGE_START);
        c.add(statusBar,BorderLayout.PAGE_END);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new App().execute();
    }

}
