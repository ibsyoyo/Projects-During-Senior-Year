//Ibrahim Rahman
import javax.swing.*; 
import java.util.Random;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class StartGUI extends JPanel{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
    public int car1o = 14;
	public int car2o = 14;
	public int car3o = 14;
	BufferedImage carpic = carpicture();
	@Override
	public void paintComponent(Graphics graphic) {
	    super.paintComponent(graphic);  
	    graphic.drawRect(50,20,400,10);
	    graphic.setColor(Color.GRAY);  
	    graphic.fillRect(50,20,400,10);
	    graphic.drawImage(carpic,car1o,10,35,32,null);

	    graphic.drawRect(50,60,400,10);
	    graphic.setColor(Color.GRAY);  
	    graphic.fillRect(50,60,400,10);  
	    graphic.drawImage(carpic,car2o,50,35,32,null);

	    graphic.drawRect(50,100,400,10);
	    graphic.setColor(Color.GRAY);  
	    graphic.fillRect(50,100,400,10);
	    graphic.drawImage(carpic,car3o,90,35,32,null); 

  	}
  	public Dimension getPreferredSize() {
        return new Dimension(500, 200); 
  	}
  
  	public BufferedImage carpicture(){
        BufferedImage image;
        try{
        	File jf = new File("/Users/ibrahim/eclipse-workspace/RaceTrack/src/sportive-car.png");
        	image = ImageIO.read(jf);
        	return image;
        }
        catch(IOException ex){
        	System.out.println("error");
        	return null;
        }
        
    }
   
}
public class RaceTrack extends JPanel{
	
	/**
	 * 
	 */
	//private static int car1o, car2o, car3o;
	
	private static final long serialVersionUID = 1L;
	private static JButton start, pause, reset;

	private static JFrame jf;
	private static JPanel jp;
	private static boolean bool;
	
	public static synchronized void pause(){
		bool = true;
	}
	public static synchronized void start(){
		bool = false;
	}
	
	private static Random rando = new Random(); 

	public static void main(String[] args) throws IOException{
		jp = new JPanel();
		StartGUI initial= new StartGUI();
		start= new JButton("Start");
		start.setVisible(true);
		pause= new JButton("Pause");
		pause.setVisible(true);
		reset = new JButton("Reset");
		reset.setVisible(true);
		
        start.addActionListener((e1) -> {
        	start();
        	new Thread(() -> {
        		boolean move = true;
	        	
	        	try{	
	        		while(move){
		        		if(bool)
			        		break;
		        		if(initial.car2o < 410 &&
		        				initial.car3o < 410 
		        				&& initial.car1o < 410){
			        		initial.car1o += (rando.nextInt(10) + 1
); 
			        		initial.repaint();
			        	}
			        	else{
			        		break;
			        	}
		        		if(initial.car1o >= 410){
		        			JOptionPane.showMessageDialog(jf, "car 1 wins");

		        			break;
		        		}
		        			Thread.sleep(50);
	        		}
	        	}
	        	catch(InterruptedException ex){
	        		System.out.println("Interrupted");
	        		
	        	}})
        		.start();
        	new Thread(() -> {
        		boolean move = true;
        		try{
	        		while(move){
		        		if(bool)
			        		break;
		        		if(initial.car1o < 410 &&
		        				initial.car3o < 410 &&
		        				initial.car2o < 410){
			        		initial.car2o += (rando.nextInt(10) + 1
); 
			        		initial.repaint();
			        	}
			        	else{
			        		break;
			        	}
		        		if(initial.car2o >= 410){
		        			JOptionPane.showMessageDialog(jf, "car 2 wins");

		        			break;
		        		}
		        			Thread.sleep(50);
	        		}
	        	}
	        	catch(InterruptedException ex){
	        		System.out.println("Interrupted");
	        		
	        	}})
        		.start();
        	new Thread(() -> {
        		boolean move = true;
	        	try{
	        		while(move){
		        		if(bool)
			        		break;
		        		if(initial.car1o < 410 &&
		        				initial.car2o < 410 &&
		        				initial.car3o < 410){
		        			initial.car3o += (rando.nextInt(10) + 1
); 
			        		initial.repaint();
			        	}
			        	else{
			        		break;
			        	}
			        	if(initial.car3o >= 410){
			        		JOptionPane.showMessageDialog(jf, "car 3 wins");
			        		break;
		        		}
		        		Thread.sleep(50);
	        		}
	        	}
	        	catch(InterruptedException ex){
	        		System.out.println("Interrupted");
	        		
	        	}})
        		.start();

        });
       
        start.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent ex) {
				bool = false;
			}
		});
        
        pause.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent ex) {
				bool = true;
			}
		});
		
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ex) {
				initial.car1o= 10;
				initial.car2o= 10;
				initial.car3o= 10;
				initial.repaint();
				bool = true;
			}
		});
        
     
        jp.add(start);
        jp.add(pause);
        jp.add(reset);
        jp.add(initial);
        
		jf = new JFrame("Race Track");
		jf.setContentPane(jp);
		jf.setSize(500, 200);
		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setResizable(false);


	}
	
	
}

