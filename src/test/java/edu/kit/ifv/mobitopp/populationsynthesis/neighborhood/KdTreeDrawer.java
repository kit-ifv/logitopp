package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

class KdTreeDrawer extends JPanel implements ActionListener {

	private static final List<Color> colors = List.of(Color.blue, Color.red, Color.cyan, Color.green, Color.magenta, Color.orange, Color.pink, Color.yellow);
	private static final long serialVersionUID = -2407163892681786728L;
	private final int DELAY = 500;
    private Timer timer;
    
    private KdTree<Point2D> tree;

    public KdTreeDrawer(KdTree<Point2D> tree) {
    	this.tree = tree;
        initTimer();
    }

    private void initTimer() {

        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    public Timer getTimer() {
        
        return timer;
    }

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        draw(tree.getRoot(), g2d);
    }
    
    private void draw(Node<Point2D> node, Graphics2D g2d) {
		if (node instanceof InnerNode<?>) {
			drawInner((InnerNode<Point2D>) node, g2d);
		} else if (node instanceof Leaf<?>) {
			drawLeaf((Leaf<Point2D>)node, g2d);
		}
		
	}
    
    private void drawInner(InnerNode<Point2D> node, Graphics2D g2d) {
    	Rectangle2D rect = node.getRect();

    	g2d.setPaint(randomColor(new Random(rect.hashCode())));
    	g2d.draw(rect);
    	
    	for (Node<Point2D> child : node.getChildren()) {
    		this.draw(child, g2d);
    	}
    }

	private void drawLeaf(Leaf<Point2D> leaf, Graphics2D g2d) {
		int x = (int) Math.round(leaf.getX());
		int y = (int) Math.round(leaf.getY());

		g2d.setPaint(Color.black);
    	g2d.drawLine(x,y,x,y);
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
    
    private Color randomColor(Random random) {   	
    	int c = random.nextInt(colors.size());
    	return colors.get(c);
    }
}