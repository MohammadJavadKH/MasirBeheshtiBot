package masir_beheshti;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();
        MasirBeheshtiBot testBot = new MasirBeheshtiBot();
        try{
            api.registerBot(testBot);
        }catch (TelegramApiRequestException e){
            e.printStackTrace();
        }
    }
}
