package org.example;

import java.util.List;

public class PollData {
    private String question;
    private List<String> options;

    public PollData(String question, List<String> options) {
        this.question = question;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public List<String> getOptions() {
        return options;
    }
}
