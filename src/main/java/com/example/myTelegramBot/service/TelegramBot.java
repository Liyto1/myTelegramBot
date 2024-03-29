package com.example.myTelegramBot.service;

import com.example.myTelegramBot.config.BotConfig;
import com.example.myTelegramBot.model.User;
import com.example.myTelegramBot.model.UserRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Data
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    final BotConfig config;

    static final String HELP_TEXT = "This bot is created to demonstrate Spring capabilities and gain experience.\n\n" +
            "You can execute commands from the main menu on the left or by typing a command:\n\n" +
            "Type /start to see a welcome message\n\n" +
            "Type /mydata to see data stored about yourself\n\n" +
            "Type /help to see this message again";
    static final String DELETE_TEXT = "All information about You was deleted successful!";


    public TelegramBot( BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start","get a welcome message"));
        listOfCommands.add(new BotCommand("/mydata", "get information about user"));
        listOfCommands.add(new BotCommand("/delete","delete data of user"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/settings", " set your preferences"));
        try{
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }catch (TelegramApiException e){
            log.error("Error setting bot`s command list: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "/start":
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    log.info("Send help description for " + update.getMessage().getChat().getFirstName());
                    break;
                case "/mydata":
                    sendMessage(chatId,"This is all information, what we have about you\n\n" +
                            dataOfUser(update.getMessage()));
                    log.info("Send data for "+ update.getMessage().getChat().getFirstName() + " from BD");
                    break;
                case "/delete":
                    deleteUserData(update.getMessage(),chatId);
                    sendMessage(chatId,DELETE_TEXT);
                    break;
                case "/settings":
                    break;
                default:
                    sendMessage(chatId, "Sorry, it  doesn't work for now(");
                    log.info("Incorrect ask from user " + update.getMessage().getChat().getFirstName());
            }
        }
    }

    private void deleteUserData(Message message, long chatId) {
        if(userRepository.findById(message.getChatId()).isEmpty()){
            log.error("Didn't found any info about " + message.getChat().getFirstName());
            sendMessage(chatId,"Sorry " + message.getChat().getFirstName() + " We didn't have any info about you :(");
        }else {
            userRepository.deleteById(chatId);
            log.info("information about " + message.getChat().getFirstName() + " was deleted");
        }
    }

    private Optional<User> dataOfUser(Message message) {

        Optional<User> userById = userRepository.findById(message.getChatId());
        return userById;

    }

    private void registerUser(Message message) {

        if(userRepository.findById(message.getChatId()).isEmpty()){
            var chatId = message.getChatId();
            var chat = message.getChat();
            User user = new User();
            user.setChatID(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("user: " + user + "saved");
        }
    }

    private void startCommandReceived(long chatId, String name){
        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to user " + name);
        sendMessage(chatId, answer);
    }

    private  void sendMessage (long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try{
            execute(message);
        }catch (TelegramApiException e){
            log.error("Error occurred: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }
}
