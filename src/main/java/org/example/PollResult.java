package org.example;

import java.util.ArrayList;
import java.util.List;

public class PollResult {
    private String question;
    private List<OptionResult> options;
    private int answeredCount;
    private String pollId;

    public PollResult(String question, List<OptionResult> options) {
        this.question = question;
        this.options = options;
        this.answeredCount = 0;
        this.pollId = "";
    }

    public String getPollId() {
        return pollId;
    }

    public void setPollId(String pollId) {
        this.pollId = pollId;
    }

    public int getAnsweredCount() {
        return answeredCount;
    }

    public void increaseAnsweredCount() {
        this.answeredCount++;
    }

    public String getQuestion() {
        return question;
    }

    public List<OptionResult> getOptions() {
        return options;
    }

    public double getOptionPrecent(OptionResult optionResult) {
        if (answeredCount == 0) return 0;

        return (double) (optionResult.getVotes() * 100) / answeredCount;
    }

    public static class OptionResult {
        private String option;
        private int votes;

        public OptionResult(String option, int votes) {
            this.option = option;
            this.votes = votes;
        }

        public String getOption() {
            return option;
        }

        public int getVotes() {
            return votes;
        }

        public void increaseVotes() {
            this.votes++;
        }


    }
}
