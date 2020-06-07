import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        DatabaseService.getInstance().init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(StopGameNotificationTelegramBot.getInstance());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
        ParsingThread parsingThread = new ParsingThread();
        parsingThread.start();
    }
}
