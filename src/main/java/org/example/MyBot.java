package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {
    private List<Long> registered = new ArrayList<>();
    private long pollStartTime;
    private List<PollResult> surveyResults = new ArrayList<>();
    private JFrame frame;


    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("📨 התקבל עדכון חדש מסוג: " + update);
        System.out.println("Has pollAnswer? " + update.hasPollAnswer());
        System.out.println("Has poll? " + update.hasPoll());
        System.out.println("Has message? " + update.hasMessage());

        if (update.hasPollAnswer()) {
            String id = update.getPollAnswer().getPollId();
            System.out.println("התקבלה תשובה לסקר ID: " + id);
            List<Integer> chosen = update.getPollAnswer().getOptionIds();

            for (PollResult poll : this.surveyResults) {
                System.out.println("בדיקה מול סקר: " + poll.getPollId());

                if (poll.getPollId().equals(id)) {
                    poll.increaseAnsweredCount();
                    for (int index : chosen) {
                        poll.getOptions().get(index).increaseVotes();
                    }
                    System.out.println("✅ תשובה התקבלה לסקר: " + poll.getQuestion());
                    break;
                }


            }
            boolean allCompleted = true;
            for (PollResult p : this.surveyResults) {
                if (p.getAnsweredCount() < this.registered.size()) {
                    allCompleted = false;
                    break;
                }
            }

            if (allCompleted || fiveMinutePass()) {
                showResultsInSwing();
            }

        }


        if (update.getMessage().hasText() && update.getMessage() != null) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            if (text.equals("Hi") || text.equals("/start") || text.equals("היי")) {
                if (!registered.contains(chatId)) {
                    for (Long id : registered) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(id);
                        sendMessage.setText("New member join to group");
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Welcome to group");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    this.registered.add(chatId);
                }
            }
        }

//        else if(update.hasPollAnswer()){
//            for(PollResult poll: this.surveyResults){
//                if(poll.getPollId().equals(update.getPollAnswer().getPollId())){
//                    poll.increaseAnsweredCount();
//                    // בדיקה לספירת האנשים
//                    System.out.println("תשובה התקבלה לסקר: " + poll.getQuestion());
//
//                }
//            }

//            if(allAnswer() || fiveMinutePass()){
//                sendResult();
//            }
//        }


    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    private String sendResult(PollResult poll) {
        String resultText = "Survey result for: " + poll.getQuestion() + "\n";
        for (PollResult.OptionResult opt : poll.getOptions()) {
            double percent = poll.getOptionPrecent(opt);
            resultText += opt.getOption() + " - " + opt.getVotes() + " votes - " + percent + "%" + "\n";
        }
        return resultText;
    }

    public List<PollResult> getSurveyResults() {
        return surveyResults;
    }


    public void sendPoll(String question, List<String> options) {
        this.pollStartTime = System.currentTimeMillis();
        for (Long id : registered) {
            System.out.println("Registered users: " + registered);
            SendPoll sendPoll = new SendPoll();
            sendPoll.setChatId(id);
            sendPoll.setQuestion(question);
            sendPoll.setOptions(options);
            sendPoll.setIsAnonymous(false);
            try {
                Message message = execute(sendPoll);
                String pollId = message.getPoll().getId();
                System.out.println("שמירת מספר מזהה של הסקר");
                //            שמירת הסקר
                List<PollResult.OptionResult> optionList = new ArrayList<>();
                for (String opt : options) {
                    optionList.add(new PollResult.OptionResult(opt, 0));
                }

                PollResult pollResult = new PollResult(question, optionList);
                surveyResults.add(pollResult);
                pollResult.setPollId(pollId);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }


//            בדיקה

            System.out.println("נשמר סקר חדש: " + question);
            System.out.println("סך הכול סקרים: " + surveyResults.size());

        }
    }

    private void showResultsInSwing() {
        if (frame == null) return;

        SwingUtilities.invokeLater(() -> {
            frame.setContentPane(new ResultPanel(frame, this));
            frame.revalidate();
            frame.repaint();
        });
    }

    private boolean fiveMinutePass() {
        long now = System.currentTimeMillis();
        long pass = now - this.pollStartTime;
        return pass >= 5 * 60 * 1000;
    }

    public int getCommunitySize() {
        return registered.size();
    }


    @Override
    public String getBotUsername() {
        return "SurvyBot_bot";
    }

    public String getBotToken() {
        return "8385816309:AAFMEt5FZQEMK7AkpHyXxSl1maeb7RfxvZY";
    }
}
