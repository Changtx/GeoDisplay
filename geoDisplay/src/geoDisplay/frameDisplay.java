package geoDisplay;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import geoDisplay.oracleConnect;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

public class frameDisplay{
  public static void main(String[] args){
	GeoFrame frame=new GeoFrame();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}

class GeoFrame extends JFrame{
	GeoPanel panel = new GeoPanel();;
	public GeoFrame(){
		setTitle("Geo");
		setSize(700, 700);
		this.getContentPane().add(panel, BorderLayout.CENTER);;
	}
}

class GeoPanel extends JPanel {
	
	private JPanel jp=new JPanel();
	private JCheckBox check = new JCheckBox("show lions and ponds in selected region");
	private boolean state = false;
	private Polygon polyMousePos;

	private ArrayList<Polygon> polygon = new ArrayList<Polygon>();
	private ArrayList<Ellipse2D> ellipse = new ArrayList<Ellipse2D>();
	private ArrayList<Point2D> pointsArray = new ArrayList<Point2D>();

	public GeoPanel() {
	  setLayout(new BorderLayout());
	  setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Geo"));
	  setBackground(Color.GRAY);
	  jp.add(check);
	  jp.setLayout(new FlowLayout());
	  add(jp,BorderLayout.SOUTH);
	  
	  try {
		getRegion();
		getPond();
		getLion();
	} catch (ClassNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	  
	  check.addItemListener(new ItemListener() {
	  	@Override
	      public void itemStateChanged(ItemEvent e) {
	          if(e.getStateChange() == ItemEvent.SELECTED) {
	          	state = true;
	          	polyMousePos = null;
	          	repaint();
	          } else {
	          	state = false;
	          	polyMousePos = null;
	          	repaint();
	          };
	      }
	  });
	  
	  addMouseListener(new MouseAdapter() {
		  public void mouseClicked(MouseEvent e) {
				if (state==true) {
					polyMousePos = null;
					getPolyMousePos();
					repaint();
				}
			}
	  });
	}
	
	public void  getPolyMousePos() {
		 for (Polygon p : polygon) {
			 if (p.contains(this.getMousePosition())) {
				 polyMousePos = p;
				 break;
			 }
		 }
	}
	
	@Override
	public void paintComponent(Graphics g){
	  super.paintComponent(g);
	  if (state) {
		  paintGeo(g);
	  } else {
		  paintGeo(g);
	  }
	}

	public void paintGeo(Graphics g) {
		paintRegion(g);
		paintLion(g);
		paintPond(g);
	}
	
	public void paintRegion(Graphics g) {
		for (Polygon p : polygon) {
			  g.setColor(Color.white);
			  g.fillPolygon(p);
			  g.setColor(Color.black);
			  g.drawPolygon(p);
		}
	}
		
	public void paintPond(Graphics g) {
		if (state == true) {
			for (Ellipse2D e : ellipse) {
				if (polyMousePos != null && polyMousePos.contains(e.getBounds2D())) {
					((Graphics2D) g).setStroke(new BasicStroke(1.0f));
					  g.setColor(Color.red);
					  ((Graphics2D) g).fill(e);
					  ((Graphics2D) g).draw(e);
				} else {
					((Graphics2D) g).setStroke(new BasicStroke(1.0f));
					  g.setColor(Color.blue);
					  ((Graphics2D) g).fill(e);
					  g.setColor(Color.black);
					  ((Graphics2D) g).draw(e);
				}	 
			}
		} else {
			for (Ellipse2D e : ellipse) {
				 ((Graphics2D) g).setStroke(new BasicStroke(1.0f));
				  g.setColor(Color.blue);
				  ((Graphics2D) g).fill(e);
				  g.setColor(Color.black);
				  ((Graphics2D) g).draw(e);
			}
		}
	}
	
	public void paintLion(Graphics g) {
		float lineWidth = 5.0f;
	    ((Graphics2D)g).setStroke(new BasicStroke(lineWidth));
	    
		if (state == true) {
			g.setColor(Color.red);
			for (Point2D point : pointsArray) {
				if (polyMousePos != null && polyMousePos.contains(point)) {
					g.setColor(Color.red);
					g.drawLine((int)point.getX(), (int)point.getY(), (int)point.getX(), (int)point.getY());
				} else {
					g.setColor(Color.green);
					g.drawLine((int)point.getX(), (int)point.getY(), (int)point.getX(), (int)point.getY());
				}
			}
		} else {
			g.setColor(Color.green);
			for (Point2D point : pointsArray) {
				g.drawLine((int)point.getX(), (int)point.getY(), (int)point.getX(), (int)point.getY());
			}
		}
		
	}
	
	public void getRegion() throws ClassNotFoundException, SQLException {
		  oracleConnect connect = new oracleConnect();
		  ResultSet rs = connect.findRegion();
		  while(rs.next()){
			  STRUCT dbObject = (STRUCT) rs.getObject(2);  
			  JGeometry geom = JGeometry.load(dbObject); 
			  double[] points = geom.getOrdinatesArray();
			  int[] xPoints = new int[4];
			  int[] yPoints = new int[4];
			  int j = 0;
			  for (int i = 0; i < 8; i = i + 2) {
				  xPoints[j] = (int) points[i] + 20;
				  yPoints[j] = (int) points[i + 1] + 20;
				  j++;
			  }
			  Polygon p = new Polygon(xPoints, yPoints, 4);
			  polygon.add(p);
		  }
		  connect.closeConnect();
	}

	public void getPond() throws ClassNotFoundException, SQLException {
		  oracleConnect connect = new oracleConnect();
		  ResultSet rs = connect.findPond();
		  while(rs.next()){
			  STRUCT dbObject = (STRUCT) rs.getObject(2);  
			  JGeometry geom = JGeometry.load(dbObject); 
			  double[] points = geom.getOrdinatesArray();
			  int[] xPoints = new int[3];
			  int[] yPoints = new int[3];
			  int j = 0;
			  for (int i = 0; i < 6; i = i + 2) {
				  xPoints[j] = (int) points[i] + 20;
				  yPoints[j] = (int) points[i + 1] + 20;
				  j++;
			  }
			  int radius = yPoints[1] - yPoints[0];
			  Rectangle2D rect = new Rectangle2D.Double(xPoints[2] - radius, yPoints[2], radius * 2, radius * 2);
			  Ellipse2D e = new Ellipse2D.Double();
			  e.setFrame(rect);
			  ellipse.add(e);
		  }
		  connect.closeConnect();
	}

	public void getLion() throws ClassNotFoundException, SQLException {
		  oracleConnect connect = new oracleConnect();
		  ResultSet rs = connect.findLion();
		  while(rs.next()){
			  STRUCT dbObject = (STRUCT) rs.getObject(2);  
			  JGeometry geom = JGeometry.load(dbObject); 
			  double[] points = geom.getPoint();
			  int[] xPoints = new int[1];
			  int[] yPoints = new int[1];
			  int j = 0;
			  for (int i = 0; i < 1; i = i + 2) {
				  xPoints[j] = (int) points[i] + 20;
				  yPoints[j] = (int) points[i + 1] + 20;
				  j++;
			  }
			  Point2D point = new Point2D.Double(xPoints[0], yPoints[0]);
			  pointsArray.add(point);
		  }
		  connect.closeConnect();
	}
  
}