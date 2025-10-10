package org.example;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionPanel extends JPanel {

    private JTextField question;
    private List<JTextField> options = new ArrayList<>();

    public QuestionPanel(int questionNumber) {
        this.setLayout(null);
        this.setBorder(BorderFactory.createTitledBorder("Question " + questionNumber));
        JLabel questionLabel = new JLabel("Question:");
        questionLabel.setBounds(20, 20, 100, 25);
        this.add(questionLabel);
        question = new JTextField();
        question.setBounds(80, 20, 600, 25);
        this.add(question);

        addOptionField(1);
        addOptionField(2);

        JButton addOptionButton = new JButton("More option");
        addOptionButton.setBounds(700, 20, 90, 25);
        this.add(addOptionButton);
        addOptionButton.addActionListener(e -> {
            if (options.size() < 4) {
                addOptionField(options.size() + 1);
            } else {
                JOptionPane.showMessageDialog(this, "You can only enter up to 4 options.");
            }
        });

    }

    private void addOptionField(int number) {
        JLabel label = new JLabel(number + ".");
        label.setBounds(20, 60 + (number - 1) * 30, 20, 25);
        this.add(label);

        JTextField field = new JTextField();
        field.setBounds(50, 60 + (number - 1) * 30, 640, 25);
        this.add(field);

        options.add(field);

        this.revalidate();
        this.repaint();
    }

    public String getQuestion() {
        return question.getText().trim();
    }

    public List<String> getOptions() {
        List<String> output = new ArrayList<>();
        for (JTextField t : this.options) {
            if (!t.getText().trim().isEmpty()) {
                output.add(t.getText().trim());
            }
        }
        return output;
    }
}
