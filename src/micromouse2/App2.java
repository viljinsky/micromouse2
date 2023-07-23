/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package micromouse2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

class GraphControl extends CommandManager {

    Graph graph;

    File file = new File("test.ini");

    private static final String NEW = "new";
    private static final String OPEN = "open";
    private static final String SAVE = "save";
    private static final String SAVE_AS = "save as";

    public GraphControl(Graph graph) {
        super(NEW, OPEN, SAVE, SAVE_AS);
        this.graph = graph;
    }

    @Override
    protected void doCommand(String command) {
        try {
            switch (command) {
                case NEW:
                    graph.clear();
                    file = null;
                    graph.change();
                    break;
                case OPEN:
                    JFileChooser fileChooser = new JFileChooser(".");
                    fileChooser.setSelectedFile(file);
                    int retVal = fileChooser.showOpenDialog(null);
                    if (retVal == JFileChooser.APPROVE_OPTION) {
                        File tmp = fileChooser.getSelectedFile();
                        try (FileInputStream in = new FileInputStream(fileChooser.getSelectedFile())) {
                            graph.read(in);
                            file = tmp;
                            graph.change();
                        }
                    }
                    break;
                case SAVE:
                    if (file == null) {
                        JFileChooser fc = new JFileChooser(".");
                        fc.setSelectedFile(new File("test.ini"));
                        int val = fc.showSaveDialog(null);
                        if (val == JFileChooser.APPROVE_OPTION) {
                            file = fc.getSelectedFile();
                        } else {
                            return;
                        }
                    }
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        graph.write(out);
                        graph.change();
                    }
                    break;
                case SAVE_AS:
                    break;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}

class GraphListener extends MouseAdapter {

    private Graph graph;

    Node node1 = null;
    Node node2 = null;
    Edge edge1 = null;
    List<Node> tmpList;

    public GraphListener(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Node node = graph.nodeAt(e.getPoint());
        if (node != null) {
            if (!node.equals(node1)) {
                node1 = node;
                System.out.println("MouseMoved " + node);
            }
        } else if (node1 != null) {
            node1 = null;
        }

        Edge edge = graph.edgeAt(e.getPoint());
        if (edge != null) {
            if (!edge.equals(edge1)) {
                edge1 = edge;
                System.out.println("edge " + edge);
            }
        } else if (edge1 != null) {
            edge1 = null;

        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Node node = graph.nodeAt(e.getPoint());
        if (node != null) {
            if (!node.equals(node2)) {
                node2 = node;
                if (node2 != null && node1 != null) {
                    System.out.println("MouseDragged " + node1 + " " + node2);

                    for (int i = 1; i < tmpList.size(); i++) {
                        graph.edges.remove(new Edge(tmpList.get(i - 1), tmpList.get(i)));
                    }

                    Node lastNode = tmpList.get(tmpList.size() - 1);

                    if ((Math.abs(lastNode.x - node2.x) == 1 && lastNode.y == node2.y) || (Math.abs(lastNode.y - node2.y) == 1 && lastNode.x == node2.x)) {
                        tmpList.add(node2);
                    }

                    for (int i = 1; i < tmpList.size(); i++) {
                        graph.edges.add(new Edge(tmpList.get(i - 1), tmpList.get(i)));
                    }

                    graph.change();
                }
            }
        } else if (node2 != null) {
            node2 = null;

        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        System.out.println("MouseExited");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        System.out.println("MouseEntered");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Node node = graph.nodeAt(e.getPoint());
        if (node != null) {
            System.out.println("mouseRelized " + node);
        }

//        if (node1!=null && node2!=null){
//            graph.wall(node1, node2);
//            graph.change();
//        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Node node = graph.nodeAt(e.getPoint());
        if (node != null) {
            tmpList = new ArrayList<>();
            tmpList.add(node);
            System.out.println("mousePressed " + node);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Node node = graph.nodeAt(e.getPoint());
        if (node != null) {
            System.out.println("mouseClicked " + node);
        }

        Edge edge = graph.edgeAt(e.getPoint());
        if (edge != null) {
            System.out.println("edge " + edge);
        }
    }

}

/**
 *
 * @author viljinsky
 */
public class App2 extends JPanel implements WindowListener {

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
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

    private JFrame frame;

    public Brouser brouser;

    public StatusBar statusBar = new StatusBar();
    

    CommandManager commandManager = new CommandManager("cmd1", "cmd2") {
        @Override
        protected void doCommand(String command) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    };

    public App2(JFrame frame) {
        super(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        this.frame = frame;
        frame.addWindowListener(this);

        Maze maze = new Maze(16, 16);
        GraphListener graphListener = new GraphListener(maze);
        maze.addChangeListener(e->{
        });

        brouser = new Brouser();
        brouser.addMouseListener(graphListener);
        brouser.addMouseMotionListener(graphListener);
        brouser.setPreferredSize(maze.preferredSize());
        brouser.add(maze);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.add(brouser);

        maze.addChangeListener(brouser);
        add(new JScrollPane(wrapper));
        add(new GraphControl(maze).controlBar(), BorderLayout.PAGE_START);
        add(statusBar, BorderLayout.PAGE_END);

    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("App2");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setContentPane(new App2(frame));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
