import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StopGameNotificationTelegramBot extends TelegramLongPollingBot {
    private final static StopGameNotificationTelegramBot stopGameNotificationTelegramBot = new StopGameNotificationTelegramBot();

    private StopGameNotificationTelegramBot(){

    }

    public static final StopGameNotificationTelegramBot getInstance(){
        return stopGameNotificationTelegramBot;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();

            if(text.equals("/start")){
                startCommand(update);
            }

            if(text.equals("/subscribe")){
                if(!DatabaseService.getInstance().checkSubscription(update.getMessage().getChatId())){
                    DatabaseService.getInstance().subscribe(update.getMessage().getChatId());
                    sendMessage("Вы успешно подписались на новостную рассылку!", update.getMessage().getChatId());
                }
            }

            if(text.equals("/unsubscribe")){
                if (DatabaseService.getInstance().checkSubscription(update.getMessage().getChatId())){
                    DatabaseService.getInstance().unsubscribe(update.getMessage().getChatId());
                    sendMessage("Вы успешно одписались от новостной рассылки!", update.getMessage().getChatId());
                }
            }

            if (text.equals("/help")){
                sendMessage("/subscribe - подписаться на рассылку\n/unsubscribe - одписаться от новостной рассылки\n/help - доступные команды", update.getMessage().getChatId());
            }

        }
    }

    @Override
    public String getBotUsername() {
        return "StopGameNotificationBot";
    }

    @Override
    public String getBotToken() {
        return "1207282755:AAEcrG6YkRVrSN5PS6uAJ4V0plUG04jhKFc";
    }

    private void sendMessage(String content, Long chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(content);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void startCommand(Update update){
        if(!DatabaseService.getInstance().isSubscriberExist(update.getMessage().getChatId())){
            DatabaseService.getInstance().addSubscriber(update.getMessage().getChatId(), true);
            sendMessage("Вы успешно подписались на новостную рассылку, поздравляем! Список команд:\n/unsubscribe - отписаться от рассылки\n/subscribe - подписаться на рассылку\n/help - доступные команды", update.getMessage().getChatId());
        }
    }

    public void sendToAllSubscribers(String contentUrl){

        try {
             ResultSet subscribers = DatabaseService.getInstance().getSubscribers();

             while (subscribers.next()){
                 if (subscribers.getBoolean("status") == true){
                     sendMessage(contentUrl, subscribers.getLong("user_id"));
                     System.out.println("Chat id: " + subscribers.getLong("user_id"));
                 }
             }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
