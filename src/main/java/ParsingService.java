
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.*;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsingService {
    private static final ParsingService parsingService = new ParsingService();
    private final String PROPERTY_FILE_PATH = "C:\\Users\\thefa\\Documents\\Projects\\StopGameNotificationTelegramBot\\src\\main\\resources\\lastgame.properties";
    private ParsingService(){

    }

    public static final ParsingService getInstance(){
        return parsingService;
    }


    private Document getDocument() throws IOException {
        Document document = Jsoup.connect("https://stopgame.ru/news").userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:76.0) Gecko/20100101 Firefox/76.0").referrer("http://www.google.com").get();
        return document;
    }

    private Element getLastCard(){
        Element lastCard = null;
            try {
                lastCard = getDocument().selectFirst("div[class=item article-summary article-summary-card]");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return lastCard;
    }

    private Long getLastCardIndex(Element lastCard){
        Long lastCardIndex = 0L;

        if(lastCard != null){
            Element a = lastCard.selectFirst("a");
            Pattern pattern = Pattern.compile("\\d{5,}");
            Matcher matcher = pattern.matcher(a.attr("href"));

            while (matcher.find()){
                lastCardIndex = Long.parseLong(matcher.group());
            }
        }

        return lastCardIndex;
    }

    public String getLastCardUrl(){
        return "https://stopgame.ru/newsdata/"+getLastCardIndex(getLastCard());
    }

    public boolean isNewCard(){
        Properties properties = new Properties();
        boolean isNewCard = false;

        try (InputStream inputStream = new FileInputStream(PROPERTY_FILE_PATH)) {
            properties.load(inputStream);
            Long lastIndex = Long.parseLong(properties.getProperty("lastGameIndex"));
            inputStream.close();

            if(lastIndex < getLastCardIndex(getLastCard())) {
                System.out.println("Property file index: " + lastIndex);
                System.out.println("Last real index: " + getLastCardIndex(getLastCard()));
                isNewCard = true;
                OutputStream outputStream = new FileOutputStream(PROPERTY_FILE_PATH);
                properties.setProperty("lastGameIndex", getLastCardIndex(getLastCard()).toString());
                properties.store(outputStream, null);
                outputStream.close();

            }

        } catch (IOException e){
            e.printStackTrace();
        }

        System.out.println("Is new card: " + isNewCard);
        return isNewCard;
    }
}
