package org.example;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MakeSurveyAlone extends JPanel {

    private JFrame frame;
    private List<QuestionPanel> questions = new ArrayList<>();
    private JButton addQuestion;
    private JButton backButton;
    private JButton nextButton;
    private MyBot bot;

    public MakeSurveyAlone(JFrame frame, MyBot bot) {
        this.bot = bot;
        this.frame = frame;

        this.bot.setFrame(frame);

        this.setLayout(null);
        this.setBounds(0, 0, MainFrame.WINDOW_WIDTH, MainFrame.WINDOW_HEIGHT);

        addQuestion = new JButton("Add Question");
        addQuestion.setBounds(50, 270, 150, 30);
        this.add(addQuestion);
        addQuestion.addActionListener(e -> {
            if (questions.size() < 3) {
                addNewQuestion();
            } else {
                JOptionPane.showMessageDialog(this, "You can only enter up to 3 questions.");
            }
        });


        backButton = new JButton("Back");
        backButton.setBounds(50, 300, 100, 30);
        this.add(backButton);

        backButton.addActionListener(e -> {
            if (frame instanceof MainFrame) {
                MainFrame mainFrame = (MainFrame) frame;
                JPanel homePanel = mainFrame.getHomePanel();
                if (homePanel != null) {
                    frame.setContentPane(homePanel);
                    frame.revalidate();
                    frame.repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "Home panel is missing!");
                }
            }
        });

        nextButton = new JButton("Next");
        nextButton.setBounds(340, 270, 120, 30);
        this.add(nextButton);
        nextButton.addActionListener(e -> openSendOptions());

        addNewQuestion();

        frame.setContentPane(this);
        frame.revalidate();
        frame.repaint();


    }

    private void openSendOptions() {
        int choice = JOptionPane.showOptionDialog(
                this,
                "Do you want to send the survey now or later?",
                "Send Survey",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Send Now", "Later"},
                "Send Now"
        );

        if (choice == 0) {
            // שליחה עכשיו
            List<PollData> data = getSurveyData();
            new Thread(() -> {
                for (PollData poll : data) {
                    bot.sendPoll(poll.getQuestion(), poll.getOptions());
                }
                JOptionPane.showMessageDialog(this, "Survey will be sent now!");
                SwingUtilities.invokeLater(() -> {
                    frame.setContentPane(new WaitingForResultPanel());
                    frame.revalidate();
                    frame.repaint();
                });

            }).start();

        } else if (choice == 1) {
            // שליחה מאוחרת
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

                List<PollData> data = getSurveyData();

                new Thread(() -> {
                    try {
                        // מחכים את הזמן שבחרת
                        Thread.sleep(delay * 1000L);

                        for (PollData poll : data) {
                            if (poll.getQuestion().isEmpty() || poll.getOptions().size() < 2) continue;
                            bot.sendPoll(poll.getQuestion(), poll.getOptions());
                        }

                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(this, "Scheduled survey sent successfully!")
                        );

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

                JOptionPane.showMessageDialog(this, "Survey will be sent in " + delay + " seconds.");
                frame.setContentPane(new WaitingForResultPanel());
                frame.revalidate();
                frame.repaint();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number, please enter seconds as a number.");
            }
        }

    }


    public List<PollData> getSurveyData() {
        List<PollData> output = new ArrayList<>();
        for (QuestionPanel q : questions) {
            output.add(new PollData(q.getQuestion(), q.getOptions()));
        }
        return output;
    }

    private void addNewQuestion() {
        int num = questions.size() + 1;
        QuestionPanel q = new QuestionPanel(num);
        int y = 50 + (num - 1) * 220;
        q.setBounds(50, y, 800, 200);
        this.add(q);
        questions.add(q);
        int buttonY = y + 230;
        addQuestion.setBounds(50, buttonY, 150, 30);
        backButton.setBounds(220, buttonY, 100, 30);
        nextButton.setBounds(340, buttonY, 100, 30);
        this.revalidate();
        this.repaint();
    }

}
