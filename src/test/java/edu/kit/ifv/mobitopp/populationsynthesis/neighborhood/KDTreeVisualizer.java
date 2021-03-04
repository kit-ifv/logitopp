package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class KDTreeVisualizer extends JPanel implements ActionListener {
	
	private KdTree<Point2D> tree;
	
	private static final long serialVersionUID = -8756694562660996571L;

	
	public KDTreeVisualizer(KdTree<Point2D> tree) {
		this.tree = tree;

    	JFrame frame = new JFrame();
		frame.add(this);
		frame.setTitle("Points");
		frame.setSize(1000, 1000);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setVisible(true);

		
	}
	
	@SuppressWarnings("rawtypes")
	public void doDrawing(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(Color.blue);       
        
        Stack<Node<Point2D>> nodes = new Stack<>();
        nodes.push(tree.getRoot());
        
        while (!nodes.empty()) {
        	Node<Point2D> current = nodes.pop();
        	
        	if (current instanceof InnerNode) {
        		g2d.setPaint(Color.black);
        		
        		int min_x = (int) (((InnerNode) current).getRect().getMinX() * 100.0);
        		int max_x = (int) (((InnerNode) current).getRect().getMaxX() * 100.0);
        		int min_y = (int) (((InnerNode) current).getRect().getMinY() * 100.0);
        		int max_y = (int) (((InnerNode) current).getRect().getMaxY() * 100.0);
        		
        		
        		g2d.drawLine(min_x, min_y, max_x, min_y);
        		g2d.drawLine(min_x, min_y, min_x, max_y);
        		g2d.drawLine(max_x, max_y, max_x, min_y);
        		g2d.drawLine(max_x, max_y, min_x, max_y);
        		
        	} else if (current instanceof Leaf) {
        		g2d.setPaint(Color.blue);
        		
        		int x = (int) (((Leaf) current).getX() * 100.0);
                int y = (int) (((Leaf) current).getY() * 100.0);
                g2d.drawLine(x, y, x, y);
        	}
        	
        	
        }

	}
	
	@Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

}
