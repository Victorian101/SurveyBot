package org.example;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 800;
    private JPanel home;
    private MyBot bot;

    public MainFrame(MyBot bot) {
        this.bot = bot;
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setTitle("Survey");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(null);

        JLabel text1 = new JLabel("I make survey,");
        JLabel text2 = new JLabel("did you want make it by yourself or you need help?");
        text1.setFont(text1.getFont().deriveFont(Font.BOLD, 22f));
        text1.setBounds(400, 10, 400, 30);
        text2.setFont(text2.getFont().deriveFont(Font.BOLD, 22f));
        text2.setBounds(200, 40, 800, 30);

        home = new JPanel();
        home.setLayout(null);
        home.setBounds(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setContentPane(home);

        home.add(text1);
        home.add(text2);

        JButton button1 = new JButton("Make Survey Alone");
        JButton button2 = new JButton("Help me");
        button1.setBounds(380, 350, 150, 50);
        button1.addActionListener(e -> {
            MakeSurveyAlone makeSurveyAlone = new MakeSurveyAlone(this, bot);
            this.setContentPane(makeSurveyAlone);
            this.revalidate();
            this.repaint();

        });
        button2.setBounds(400, 450, 100, 50);
        button2.addActionListener(e -> {
            HelpMe helpMe = new HelpMe(this, bot);
            this.setContentPane(helpMe);
            this.revalidate();
            this.repaint();

        });
        home.add(button2);
        home.add(button1);

        this.setVisible(true);
    }

    public JPanel getHomePanel() {
        return home;
    }
}
