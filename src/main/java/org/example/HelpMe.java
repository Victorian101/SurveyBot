package org.example;

import javax.swing.*;
import java.awt.*;

public class HelpMe extends JPanel {
    private JFrame frame;
    private MyBot bot;
    private JTextField topicField;
    private JButton nextButton;
    private JButton backButton;


    public HelpMe(JFrame frame, MyBot bot) {
        this.bot = bot;
        this.frame = frame;

        this.setLayout(null);
        this.setBounds(0, 0, MainFrame.WINDOW_WIDTH, MainFrame.WINDOW_HEIGHT);

        JLabel title = new JLabel("Let's create your survey together!");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBounds(250, 50, 600, 40);
        this.add(title);

        JLabel label = new JLabel("About what subject do you want to make a survey?");
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setBounds(180, 150, 700, 30);
        this.add(label);

        topicField = new JTextField();
        topicField.setFont(new Font("Arial", Font.PLAIN, 18));
        topicField.setBounds(200, 200, 600, 40);
        this.add(topicField);

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 18));
        nextButton.setBounds(400, 270, 150, 40);
        this.add(nextButton);


        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBounds(30, 700, 100, 35);
        this.add(backButton);

        backButton.addActionListener(e -> {
            frame.setContentPane(new MainFrame(bot));
            frame.revalidate();
            frame.repaint();
        });

        ChatApi api = new ChatApi();

        this.bot.setFrame(frame);

        nextButton.addActionListener(e -> {
            String topic = topicField.getText().trim();
            if (topic.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a topic!");
                return;
            }

            String prompt = "תכין סקר בנושא \"" + topic + "\". תכתוב את נושא הסקר רק בשורה הראשונה, ולאחר מכן תציג רק ארבע אפשרויות לבחירה, בלי שום טקסט נוסף, בלי הקדמות ובלי שאלות פתוחות.";
            String response = api.sendMessage("324323484", prompt);

            String question = api.getLastQuestion();
            String[] options = api.getLastOptions();

            JPanel resultPanel = new JPanel();
            resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));

            JLabel questionLabel = new JLabel("AI suggested question:");
            JLabel questionText = new JLabel(question);
            questionText.setFont(new Font("Arial", Font.BOLD, 18));
            resultPanel.add(questionLabel);
            resultPanel.add(questionText);
            resultPanel.add(new JLabel(" "));

            resultPanel.add(new JLabel("Options:"));
            for (String opt : options) {
                resultPanel.add(new JLabel("• " + opt));
            }

            int choice = JOptionPane.showOptionDialog(
                    this,
                    resultPanel,
                    "Review AI Suggestion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Send to Telegram", "Try Again"},
                    "Send to Telegram"
            );

            if (choice == 0) { // Send to Telegram
                if (bot.getCommunitySize() < 3) {
                    JOptionPane.showMessageDialog(
                            this,
                            "You need at least 3 users in the community to start a survey.",
                            "Not enough users",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                int sendChoice = JOptionPane.showOptionDialog(
                        this,
                        "Do you want to send the survey now or schedule it for later?",
                        "Send Survey",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Send Now", "Schedule Later"},
                        "Send Now"
                );

                if (sendChoice == 0) {
                    bot.sendPoll(question, java.util.Arrays.asList(options));
                    JOptionPane.showMessageDialog(this, "Survey sent successfully!");
                    frame.setContentPane(new WaitingForResultPanel());
                    frame.revalidate();
                    frame.repaint();

                } else if (sendChoice == 1) {
                    String delayStr = JOptionPane.showInputDialog(
                            this,
                            "Enter delay time in seconds:",
                            "Schedule Send",
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (delayStr == null || delayStr.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "No delay entered.");
                        return;
                    }

                    try {
                        int delay = Integer.parseInt(delayStr);
                        JOptionPane.showMessageDialog(this, "Survey will be sent in " + delay + " seconds.");
                        frame.setContentPane(new WaitingForResultPanel());
                        frame.revalidate();
                        frame.repaint();

                        new Thread(() -> {
                            try {
                                Thread.sleep(delay * 1000L);
                                bot.sendPoll(question, java.util.Arrays.asList(options));
                                SwingUtilities.invokeLater(() ->
                                        JOptionPane.showMessageDialog(this, "Scheduled survey sent successfully!")
                                );
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }).start();

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid number, please enter seconds as a number.");
                    }
                }
            } else if (choice == 1) { // Try Again
                api.clearHistory("324323484");
                JOptionPane.showMessageDialog(this, "History cleared — try entering a new topic!");
            }
        });

    }


}
