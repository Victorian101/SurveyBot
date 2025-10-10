package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            MyBot bot = new MyBot();
            botsApi.registerBot(bot);
            new MainFrame(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.out.println("⚠️ Failed to start bot!");

        }
    }
}