package org.example;

import javax.swing.*;
import java.awt.*;

public class WaitingForResultPanel extends JPanel {

    public WaitingForResultPanel() {
        this.setLayout(null);
        JLabel title = new JLabel("The survey has been sent!");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBounds(250, 250, 600, 40);
        this.add(title);

        JLabel sub = new JLabel("When the survey ends, results will be sent automatically.");
        sub.setFont(new Font("Arial", Font.PLAIN, 20));
        sub.setBounds(220, 310, 700, 30);
        this.add(sub);
    }
}
