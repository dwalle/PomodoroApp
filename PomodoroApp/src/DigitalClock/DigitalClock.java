package DigitalClock;
import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class DigitalClock extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel p1;
    private JLabel time;

    public static void main(String[] args) {
        DigitalClock myFrame = new DigitalClock();
        myFrame.pack();
        myFrame.setTitle("Digital Clock");
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setLocationRelativeTo(null);
        myFrame.setVisible(true);

    }// main()

    public DigitalClock() {
        System.out.println(currentTime());
        p1 = new JPanel();
        p1.setLayout(new FlowLayout());
        time = new JLabel(currentTime());
        time.setFont(new Font("TimesRoman", Font.BOLD, 20));
        time.setForeground(Color.blue);
        p1.add(time);
        this.setLayout(new BorderLayout());
        this.add(p1, BorderLayout.CENTER);
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                time.setText(currentTime());
            }
        };
        Timer t = new Timer(1000, taskPerformer);
        t.start();
        
    }

    public  String currentTime() {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int aP = calendar.get(Calendar.AM_PM);
        String currentTime = hours + ":" + checkTime(minutes) + ":"
                + checkTime(seconds) + " " + amP(aP);
        return currentTime;
    }

    public String checkTime(int t) {
        String time1;
        if (t < 10) {
            time1 = ("0" + t);
        } else {
            time1 = ("" + t);
        }
        return time1;
    }

    public String amP(int ap) {
        String amPm;
        if (ap == 0)
            amPm = "AM";
        else
            amPm = "PM";
        return amPm;
    }

}// Project2