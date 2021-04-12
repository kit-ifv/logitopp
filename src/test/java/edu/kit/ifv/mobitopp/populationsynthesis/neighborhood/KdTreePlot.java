package edu.kit.ifv.mobitopp.populationsynthesis.neighborhood;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.Timer;

public class KdTreePlot extends JFrame {

	private static final long serialVersionUID = -1666718956939797827L;

	public KdTreePlot(KdTree<Point2D> tree) {

        initUI(tree);
    }

    private void initUI(KdTree<Point2D> tree) {

        final KdTreeDrawer surface = new KdTreeDrawer(tree);
        add(surface);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Timer timer = surface.getTimer();
                timer.stop();
            }
        });

        setTitle("Points");
        setSize(1000, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void display(KdTree<Point2D> tree) {
      EventQueue.invokeLater(new Runnable() {
	      @Override
	      public void run() {
	
	          KdTreePlot ex = new KdTreePlot(tree);
	          ex.setVisible(true);
	      }
	  });
    }
}

