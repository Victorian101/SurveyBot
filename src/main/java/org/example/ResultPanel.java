package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;


public class ResultPanel extends JPanel {


    public ResultPanel(JFrame frame, MyBot bot) {
        this.setLayout(null);
        this.setBackground(Color.WHITE);

        JLabel title = new JLabel("Survey Results");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBounds(250, 20, 400, 40);
        this.add(title);

        List<PollResult> results = bot.getSurveyResults();

        int y = 80;

        for (PollResult poll : results) {
            JLabel questionLabel = new JLabel("Q: " + poll.getQuestion());
            questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
            questionLabel.setBounds(50, y, 900, 30);
            this.add(questionLabel);
            y += 40;
            for (PollResult.OptionResult opt : poll.getOptions()) {

                double percent = poll.getOptionPrecent(opt);

                JLabel optionLabel = new JLabel(
                        opt.getOption() + " — " + opt.getVotes() + " votes (" + String.format("%.1f", percent) + "%)"
                );
                optionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                optionLabel.setBounds(70, y, 800, 25);
                this.add(optionLabel);

                y += 30;
            }

            y += 20;

        }
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBounds(400, y + 30, 200, 40);

        backButton.addActionListener(e -> {
            frame.setContentPane(new MainFrame(bot));
            frame.revalidate();
            frame.repaint();
        });

        this.add(backButton);
        results.clear();
    }
}
