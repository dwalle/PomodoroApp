package main;

import java.awt.EventQueue;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import DigitalClock.DigitalClock;

public class Pomodoro {

	private JFrame frmPomodoro;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			//sets the GUI to look like native OS
			  UIManager.setLookAndFeel(
			    UIManager.getSystemLookAndFeelClassName());
			  } catch (Exception e) {
			    e.printStackTrace();
			}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Pomodoro window = new Pomodoro();
					window.frmPomodoro.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Pomodoro() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private JTextField tfTimeUntilBreak;
	private JLabel lblTimeUntilBreak;
	private JLabel lblBreakLength;
	private final JTextField tfBreakLength = new JTextField();
	JLabel lblBreak;
	JButton btnReset;
	long breakLength;
	long startTime;
	long currTime;
	long desiredTimerLength = 2000;//1500000;
	boolean isOnBreak = false;
	int mouseY;
    int mouseX;
    
    public enum SoundFileType {
    	Back2Work, Break;
    }
    
    private String back2WorkSoundFilePath = "glados.wav";
    private String breakSoundFilePath = "backToWork.wav";
    
    Clip back2WorkClip = null, breakClip = null;
    
	private void initialize() {
		
		//open the default sounds
		try{
			File soundResource = new File(back2WorkSoundFilePath);
			AudioInputStream ais_back2Work = AudioSystem.getAudioInputStream(soundResource);
			back2WorkClip =  AudioSystem.getClip();
			back2WorkClip.open(ais_back2Work);
			
		 	soundResource = new File(breakSoundFilePath);
		 	AudioInputStream ais_break = AudioSystem.getAudioInputStream(soundResource);
		 	breakClip = AudioSystem.getClip();
		 	breakClip.open(ais_break);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		frmPomodoro = new JFrame();
		frmPomodoro.setTitle("Pomodoro");
		mouseY = MouseInfo.getPointerInfo().getLocation().y;
        mouseX = MouseInfo.getPointerInfo().getLocation().x;  
		frmPomodoro.setBounds(mouseX-325, mouseY, 325, 139);
		frmPomodoro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPomodoro.getContentPane().setLayout(null);
		try {
			
			frmPomodoro.setIconImage(ImageIO.read(new File("icon.png")));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		final JLabel lblTimer = new JLabel("Time:");
		lblTimer.setBounds(10, 11, 168, 14);
		frmPomodoro.getContentPane().add(lblTimer);
		
		lblBreak = new JLabel("");
		lblBreak.setBounds(10, 25, 265, 14);
		frmPomodoro.getContentPane().add(lblBreak);
		
		final ActionListener resetAL = new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JButton butt = (JButton)e.getSource();
				butt.setEnabled(false);
				
				startTime = System.currentTimeMillis();
				if(isOnBreak){
					if(!back2WorkClip.isRunning())
						 playSound(back2WorkClip);
					lblBreak.setText("Keep Working until"+convertEpochTimeToDate(startTime+desiredTimerLength));
					isOnBreak = false;
				}
				else{
					lblBreak.setText("Go On Break until"+convertEpochTimeToDate(startTime+breakLength));
					isOnBreak =true;
				}
			}
		};
	
		btnReset = new JButton("Enter");
		btnReset.setBounds(210, 64, 89, 23);
		btnReset.setEnabled(true);
		btnReset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!back2WorkClip.isRunning())
					 playSound(back2WorkClip);
				JButton butt = (JButton)e.getSource();
				butt.setEnabled(false);
				String timeLen = tfTimeUntilBreak.getText();
				desiredTimerLength = convertStringtoInt(timeLen)*60000; 
				startTime = System.currentTimeMillis();
				lblBreak.setText("Keep Working until: "+convertEpochTimeToDate(startTime+desiredTimerLength));
				TimerStart(lblTimer);
				
				breakLength = convertStringtoInt(tfBreakLength.getText())*60000;
				
				butt.setText("Reset");
				frmPomodoro.setBounds((int)frmPomodoro.getBounds().getX(), (int)frmPomodoro.getBounds().getY(), 325, 81);
				frmPomodoro.getContentPane().remove(tfTimeUntilBreak);
				frmPomodoro.getContentPane().remove(lblTimeUntilBreak);
				frmPomodoro.getContentPane().remove(lblBreakLength);
				frmPomodoro.getContentPane().remove(tfBreakLength);
				frmPomodoro.repaint();
				frmPomodoro.revalidate();
				butt.setBounds(198, 7, 89, 23);
				butt.removeActionListener(this);
				butt.addActionListener(resetAL);
			}
		});
		frmPomodoro.getContentPane().add(btnReset);
		
		tfTimeUntilBreak = new JTextField();
		tfTimeUntilBreak.setText("25");
		tfTimeUntilBreak.setBounds(156, 68, 44, 14);
		frmPomodoro.getContentPane().add(tfTimeUntilBreak);
		tfTimeUntilBreak.setColumns(10);
		
		lblTimeUntilBreak = new JLabel("Time Until Break (in mins):");
		lblTimeUntilBreak.setBounds(26, 68, 190, 14);
		frmPomodoro.getContentPane().add(lblTimeUntilBreak);
		
		lblBreakLength = new JLabel("Break Length (in mins):");
		lblBreakLength.setBounds(26, 50, 190, 14);
		frmPomodoro.getContentPane().add(lblBreakLength);
		tfBreakLength.setText("5");
		tfBreakLength.setBounds(156, 50, 44, 14);
		frmPomodoro.getContentPane().add(tfBreakLength);
		tfBreakLength.setColumns(10);
		
		BufferedImage buttonIcon = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
		
		try{
			buttonIcon = ImageIO.read(new File("openFolder.png"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		JButton btnOFDBreakSound = new JButton(new ImageIcon(buttonIcon));
		btnOFDBreakSound.setBounds(10, 50, 15, 14);
		btnOFDBreakSound.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				PickASoundFile(SoundFileType.Break);
			}
		});
		frmPomodoro.getContentPane().add(btnOFDBreakSound);
		
		JButton btnOFDBack2WorkSound = new JButton(new ImageIcon(buttonIcon));
		btnOFDBack2WorkSound.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				PickASoundFile(SoundFileType.Back2Work);
			}
		});
		btnOFDBack2WorkSound.setBounds(10, 68, 15, 14);
		frmPomodoro.getContentPane().add(btnOFDBack2WorkSound);
		
		
	}
	

	DigitalClock dc = new DigitalClock();

	private void TimerStart(final JLabel lbl) {
		ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	lbl.setText("Time: "+dc.currentTime());
            	currTime= System.currentTimeMillis();
            	
            	
            	if(isOnBreak){
            		if(currTime - startTime > breakLength && !btnReset.isEnabled()){
                		lblBreak.setText("BREAK Ended at: "+dc.currentTime());
                		startTime = currTime;
                		btnReset.setEnabled(true);
                	}
            	}
            	else{
            		if(currTime - startTime > desiredTimerLength && !btnReset.isEnabled()){
                		lblBreak.setText("BREAK began at: "+dc.currentTime());
                		startTime = currTime;
                		btnReset.setEnabled(true);
                	}
            	}
            	
            	if(btnReset.isEnabled()){
            		if(!breakClip.isRunning())
            			 playSound(breakClip);
            	}
            	
            }
        };
        Timer t = new Timer(1000, taskPerformer);
        t.start();
	}
	
	protected void PickASoundFile(SoundFileType sType) {
		
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Sounds", "mp4", "wav");
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
			try{
				if(sType == SoundFileType.Back2Work){
					back2WorkSoundFilePath = chooser.getSelectedFile().getAbsolutePath();
					File soundResource = new File(back2WorkSoundFilePath);
				 	AudioInputStream ais_back2Work = AudioSystem.getAudioInputStream(soundResource);
				 	Clip clip = AudioSystem.getClip();
			        clip.open(ais_back2Work);
				}else if(sType== SoundFileType.Break){
					breakSoundFilePath =  chooser.getSelectedFile().getAbsolutePath();
					File soundResource = new File(breakSoundFilePath);
					AudioInputStream ais_break = AudioSystem.getAudioInputStream(soundResource);
				 	Clip clip = AudioSystem.getClip();
			        clip.open(ais_break);
				}else{
					//do nothing
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}

	}

	public void playSound(Clip clip) {
		 try{
			 	clip.stop();
			 	System.out.println("Play sound: "+clip.toString());
		        //FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		        //gainControl.setValue(-25.0f); // Reduce volume by x decibels.
		        clip.start();
		        
		    }catch (Exception exc){
		        exc.printStackTrace(System.out);
		        java.awt.Toolkit.getDefaultToolkit().beep();
		    }
	}//end playSound()
	
	
	public String convertEpochTimeToDate(long e){
		String date = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date (e));
	       return (date);
	}
	private static int convertStringtoInt(String stringToInt) {
		return Integer.valueOf(stringToInt).intValue();
	}
}
