package masir_beheshti;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.methods.send.*;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.*;
import java.util.*;

public class MasirBeheshtiBot extends TelegramLongPollingBot {

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> pics;
    MongoCollection<Document> users;
    MongoCollection<Document> medias;
    private final int numberOfLevelQuestions = 5;
    private final int numberOfFinalQuestions = 10;

    boolean containsCollection(MongoDatabase db,String name){
        for (String s : db.listCollectionNames()) {
            if ((s.equals(name)))
                return true;
        }
        return false;
    }
    public MasirBeheshtiBot() {
        mongoClient = new MongoClient("localhost",27017);
        mongoDatabase = mongoClient.getDatabase("masir_beheshti");
        if (!containsCollection(mongoDatabase,"pics")){
            mongoDatabase.createCollection("pics");
        }
        pics = mongoDatabase.getCollection("pics");
        if (!containsCollection(mongoDatabase,"users")){
            mongoDatabase.createCollection("users");
        }
        users = mongoDatabase.getCollection("users");
        if (!containsCollection(mongoDatabase,"medias")){
            mongoDatabase.createCollection("medias");
        }
        medias = mongoDatabase.getCollection("medias");

    }
    void start(User user){
        addUser(user);
        showMenu(user);
    }
    void showMenu(User user){
        SendMessage message = new SendMessage();
        message.setText(HardCode.botInfo);
        message.setChatId(String.valueOf(user.getId()));
        message.setReplyMarkup(startMarkupShowMenu());
        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private ReplyKeyboard startMarkupShowMenu() {
        ReplyKeyboardMarkup markup  = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton("مسابقه عکاسی");
        row.add(keyboardButton);
        rowList.add(row);
        row = new KeyboardRow();
        keyboardButton = new KeyboardButton("مسابقه کتابخوانی");
        row.add(keyboardButton);
        rowList.add(row);
        row = new KeyboardRow();
        keyboardButton = new KeyboardButton("ارتباط با ما");
        row.add(keyboardButton);
        keyboardButton = new KeyboardButton("توشه سفر");
        row.add(keyboardButton);
        rowList.add(row);
        markup.setKeyboard(rowList);
        markup.setResizeKeyboard(true);
        return markup;
    }
    void showMedia(User user){
        SendMessage message = new SendMessage();
        message.setText(HardCode.mediaInfo);
        message.setChatId(String.valueOf(user.getId()));
        ReplyKeyboardMarkup markup  = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton("تصویری");
        row.add(keyboardButton);
        keyboardButton = new KeyboardButton("صوتی");
        row.add(keyboardButton);
        rowList.add(row);
        row = new KeyboardRow();
        keyboardButton = new KeyboardButton("کتابخانه");
        row.add(keyboardButton);
        keyboardButton = new KeyboardButton("متن و دل نوشته");
        row.add(keyboardButton);
        rowList.add(row);
        markup.setKeyboard(rowList);
        markup.setResizeKeyboard(true);
        message.setReplyMarkup(markup);
        try {
            sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    void  addUser(User user){
        if (!containsUser(user)) {
            Document document = new Document();
            document.append("id", user.getId());
            document.append("userName", user.getUserName());
            document.append("firstName", user.getFirstName());
            document.append("lastName", user.getLastName());
            document.append("firstLevel",new Integer(-1));
            document.append("secondLevel",new Integer(-1));
            document.append("thirdLevel",new Integer(-1));
            document.append("finalLevel",new Integer(-1));
            document.append("step","");
            users.insertOne(document);
        }
    }
    boolean containsUser(User user){
        return users.find(Filters.eq("id",user.getId())).iterator().hasNext();
    }
    void sendTextMessagetoAllusers(Message message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message.getText());
        for (Document document : users.find()) {
            sendMessage.setChatId(String.valueOf(document.getInteger("id")));
            try {
                sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    void sendPhotoMessagetoAllusers(Message message){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setPhoto(message.getPhoto().get(0).getFileId());
        sendPhoto.setCaption(message.getCaption());
        for (Document document : users.find()) {
            sendPhoto.setChatId(String.valueOf(document.getInteger("id")));
            try {
                sendPhoto(sendPhoto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    void sendVideoMessagetoAllusers(Message message){
        SendVideo sendVideo = new SendVideo();
        sendVideo.setVideo(message.getVideo().getFileId());
        sendVideo.setCaption(message.getCaption());
        for (Document document : users.find()) {
            sendVideo.setChatId(String.valueOf(document.getInteger("id")));
            try {
                sendVideo(sendVideo);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    void sendAudioMessagetoAllusers(Message message){
        SendAudio sendAudio = new SendAudio();
        sendAudio.setAudio(message.getAudio().getFileId());
        sendAudio.setCaption(message.getCaption());
        for (Document document : users.find()) {
            sendAudio.setChatId(String.valueOf(document.getInteger("id")));
            try {
                sendAudio(sendAudio);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    void sendDocumentMessagetoAllusers(Message message){
        SendDocument sendDocument = new SendDocument();
        sendDocument.setDocument(message.getDocument().getFileId());
        sendDocument.setCaption(message.getCaption());
        for (Document document : users.find()) {
            sendDocument.setChatId(String.valueOf(document.getInteger("id")));
            try {
                sendDocument(sendDocument);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    void sendMessagetoAllusers(Message message){
        String type = "text";
        String fileid = null;
        String text = "";
        if (message.hasText()) {
            sendTextMessagetoAllusers(message);
            text = message.getText();
        }
        if (message.hasPhoto()) {
            sendPhotoMessagetoAllusers(message);
            fileid = message.getPhoto().get(0).getFileId();
            type = "photo";
            text = message.getCaption();
        }
        if (message.getAudio() != null) {
            sendAudioMessagetoAllusers(message);
            fileid = message.getAudio().getFileId();
            type = "audio";
            text = message.getCaption();
        }
        if (message.getVideo() != null) {
            sendVideoMessagetoAllusers(message);
            fileid = message.getVideo().getFileId();
            type = "video";
            text = message.getCaption();
        }
        if (message.getDocument() != null){
            sendDocumentMessagetoAllusers(message);
            fileid = message.getDocument().getFileId();
            type = "document";
            text = message.getCaption();

        }
        Document document = new Document();
        document.append("type",type);
        document.append("fileid",fileid);
        document.append("text",text);
        medias.insertOne(document);
    }
    void addPic(Message message){
        ForwardMessage forwardMessage = new ForwardMessage();
        forwardMessage.setChatId(HardCode.channelOfphoto);
        forwardMessage.setFromChatId(message.getChatId());
        forwardMessage.setMessageId(message.getMessageId());
        try {
            forwardMessage(forwardMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    void showAudioMedia(User user){
        FindIterable<Document> documents = medias.find(Filters.eq("type", "audio"));
        SendAudio sendAudio = new SendAudio();
        sendAudio.setChatId(String.valueOf(user.getId()));
        for (Document document : documents) {
            sendAudio.setAudio(document.getString("fileid"));
            sendAudio.setCaption(document.getString("text"));
            sendAudio.setReplyMarkup(startMarkupShowMenu());
            try {
                sendAudio(sendAudio);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    void showVideoMedia(User user){
        FindIterable<Document> documents = medias.find(Filters.eq("type", "video"));
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(String.valueOf(user.getId()));
        for (Document document : documents) {
            sendVideo.setVideo(document.getString("fileid"));
            sendVideo.setCaption(document.getString("text"));
            sendVideo.setReplyMarkup(startMarkupShowMenu());
            try {
                sendVideo(sendVideo);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    void showPhotoMedia(User user){
        FindIterable<Document> documents = medias.find(Filters.eq("type", "photo"));
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(user.getId()));
        for (Document document : documents) {
            sendPhoto.setPhoto(document.getString("fileid"));
            sendPhoto.setCaption(document.getString("text"));
            sendPhoto.setReplyMarkup(startMarkupShowMenu());
            try {
                sendPhoto(sendPhoto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    void showTextMedia(User user){
        FindIterable<Document> documents = medias.find(Filters.eq("type", "text"));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));
        for (Document document : documents) {
            sendMessage.setText(document.getString("text"));
            sendMessage.setReplyMarkup(startMarkupShowMenu());
            try {
                sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    void showDocumentّMedia(User user){
        FindIterable<Document> documents = medias.find(Filters.eq("type", "document"));
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(String.valueOf(user.getId()));
        for (Document document : documents) {
            sendDocument.setDocument(document.getString("fileid"));
            sendDocument.setCaption(document.getString("text"));
            sendDocument.setReplyMarkup(startMarkupShowMenu());
            try {
                sendDocument(sendDocument);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    String createExcelOfUsersAndLinkOfFile() {
        MasirBeheshtiBot bot = new MasirBeheshtiBot();
        FindIterable<Document> us = bot.users.find();
        Comparator<Document> cmp = new UserComperator();
        Document[] usArr = new Document[(int)bot.users.count()];
        Iterator<Document> it = us.iterator();
        for (int i = 0; i < usArr.length; i++) {
            usArr[i] = it.next();
        }
        Arrays.sort(usArr,cmp);
        try (PrintWriter dos = new PrintWriter(new FileOutputStream("/root/scores.txt"))) {
            dos.println("نام کاربری\tنام\tنام خانوادگی\tآیدی تلگرام\tامتیاز مرحله اول\tامتیاز مرحله دوم\tامتیاز مرحله سوم\tامتیاز مرحله نهایی\tمجموع امتیازات");
            for (Document d : usArr) {
                String username = d.getString("userName");
                username = username == null ? "-" : username;
                String firstName = d.getString("firstName");
                firstName = firstName == null ? "-" : firstName;
                String lastName = d.getString("lastName");
                lastName = lastName == null ? "-" : lastName;
                Integer firstLevel = d.getInteger("firstLevel");
                firstLevel = firstLevel == null || firstLevel.equals(-1) ? 0 : firstLevel;
                Integer secondLevel = d.getInteger("secondLevel");
                secondLevel = secondLevel == null || secondLevel.equals(-1) ? 0 : secondLevel;
                Integer thirdLevel = d.getInteger("thirdLevel");
                thirdLevel = thirdLevel == null || thirdLevel.equals(-1) ? 0 : thirdLevel;
                Integer finalLevel = d.getInteger("finalLevel");
                finalLevel = finalLevel == null || finalLevel.equals(-1) ? 0 : finalLevel;
                Integer score = firstLevel + secondLevel + thirdLevel + finalLevel;
                Integer id = d.getInteger("id");
                dos.println(username + "\t" + firstName + "\t" + lastName + "\t" + id + "\t" + firstLevel + "\t" + secondLevel + "\t" + thirdLevel + "\t" + finalLevel + "\t" + score);
            }
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Runtime.getRuntime().exec("zip /root/scores.zip /root/scores.txt");
            Runtime.getRuntime().exec("cp /root/scores.zip /root/iso_project/media/scores.zip");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "http://95.211.150.99/media/scores.zip";
    }
    boolean canParticipate(User user,String field){
        FindIterable<Document> documents = users.find(Filters.eq("id", user.getId()));
        if(documents.iterator().hasNext()){
            Document next = documents.iterator().next();
            String step = next.getString("step");
            return step.equals(field);
        }
        return false;
    }
    void bookMatch(User user){
        FindIterable<Document> documents = users.find(Filters.eq("id", user.getId()));
        boolean flag = false;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
            flag = true;
        if(documents.iterator().hasNext()){
            Document next = documents.iterator().next();
            String step = next.getString("step");
            if(step.equals("finish"))
                flag = true;
        }
        if (flag) {
            showMessage(user,HardCode.canNotParticipate);
            return;
        }else{
            if(documents.iterator().hasNext()){
                Document next = documents.iterator().next();
                String step = next.getString("step");
                if(!step.equals("bm2") && !step.equals("bm3") && !step.equals("bmf"))
                    users.updateOne(Filters.eq("id", user.getId()), Updates.set("step", "bm1"));
            }

        }
        showMessage(user,HardCode.infoBookMatchLastDay);
        showMessage(user,HardCode.firstLevel);
        showMessage(user,HardCode.secondLevel);
        showMessage(user,HardCode.thirdLevel);
        showMessage(user,HardCode.finalLevel);
        showMessage(user,HardCode.startmatch);
    }
    void bookMatch_level1(User user){
        users.updateOne(Filters.eq("id",user.getId()), Updates.set("step","bm1"));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(HardCode.firstLevel);
        sendMessage.setChatId(String.valueOf(user.getId()));
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    void bookMatch_level2(User user){
        users.updateOne(Filters.eq("id",user.getId()), Updates.set("step","bm2"));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(HardCode.secondLevel);
        sendMessage.setChatId(String.valueOf(user.getId()));
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    void bookMatch_level3(User user){
        users.updateOne(Filters.eq("id",user.getId()), Updates.set("step","bm3"));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(HardCode.thirdLevel);
        sendMessage.setChatId(String.valueOf(user.getId()));
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    void bookMatch_levelfinal(User user){
        users.updateOne(Filters.eq("id",user.getId()), Updates.set("step","bmf"));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(HardCode.finalLevel);
        sendMessage.setChatId(String.valueOf(user.getId()));
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    boolean isValidAnswer(String answer,int num){
        return answer.matches("[1-4]{" + num + "," + num + "}");
    }
    int calculatScore(String userAnswer,String answer){
                int score = 0;
                for (int i = 0; i < Math.min(userAnswer.length(),answer.length()); i++) {
                    if (userAnswer.charAt(i) == answer.charAt(i))
                        score++;
                }
                return score;
    }
    boolean isAdmin(User user){
        for (int i = 0; i < HardCode.admins.length; i++) {
            if(HardCode.admins[i].equals(String.valueOf(user.getId())))
                return true;
        }
        return false;
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            Message message = update.getMessage();
            String step = "";
            MongoCursor<Document> iterator = users.find(Filters.eq("id", message.getFrom().getId())).iterator();
            if (iterator.hasNext())
                step = iterator.next().getString("step");
            if (message.hasPhoto()){
                if (step.equals("pic")){
                    addPic(message);
                    showMessage(message, HardCode.picMatch_submit);
                    users.updateOne(Filters.eq("id",message.getFrom().getId()),Updates.set("step",""));
                }
            }
            if(step.equals("adminmessage")) {
                if (message.hasText() && message.getText().equals("send")){
                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(HardCode.getChannelOfUsersInfo);
                    sendDocument.setDocument(createExcelOfUsersAndLinkOfFile());
                    JalaliCalendar calendar = new JalaliCalendar();
                    calendar.setTime(new Date());
                    int day = calendar.get(JalaliCalendar.DAY_OF_MONTH);
                    int month = calendar.get(JalaliCalendar.MONTH) + 1;
                    int year = calendar.get(JalaliCalendar.YEAR);
                    int hour = calendar.get(JalaliCalendar.HOUR);
                    int minute = calendar.get(JalaliCalendar.MINUTE);
                    String caption = year + "/" + month + "/" + day + " " + hour + " : " + minute;
                    sendDocument.setCaption(caption);
                    try {
                        sendDocument(sendDocument);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    sendMessagetoAllusers(message);
                }
                users.updateOne(Filters.eq("id", message.getFrom().getId()), Updates.set("step", ""));
            }
            if (message.hasText() && message.getChat().isUserChat()){
                String text = message.getText();
                if (text.equals("/start")){
                    start(message.getFrom());
                }

                if (step.equals("bm1")&& isValidAnswer(message.getText(),numberOfLevelQuestions)) {
                    int score = calculatScore(text, HardCode.firstLevel_Answer);
                    users.updateOne(Filters.eq("id",message.getFrom().getId()),Updates.set("firstLevel",score));
                    users.updateOne(Filters.eq("id",message.getFrom().getId()),Updates.set("step","bm2"));
                    showMessage(message,HardCode.GoodAnswerevel1);
                }
                else if (step.equals("bm1")){
                    showMessage(message, HardCode.Level_Errore);
                }
                if (step.equals("bm2")&& isValidAnswer(message.getText(),numberOfLevelQuestions)) {
                    int score = calculatScore(text, HardCode.secondLevel_Answer);
                    users.updateOne(Filters.eq("id",message.getFrom().getId()),Updates.set("secondLevel",score));
                    users.updateOne(Filters.eq("id",message.getFrom().getId()),Updates.set("step","bm3"));
                    showMessage(message,HardCode.GoodAnswerevel2);
                }
                else if (step.equals("bm2")){
                    showMessage(message, HardCode.Level_Errore);
                }
                if (step.equals("bm3")&& isValidAnswer(message.getText(),numberOfLevelQuestions)) {
                    int score = calculatScore(text, HardCode.thirdLevel_Answer);
                    users.updateOne(Filters.eq("id",message.getFrom().getId()),Updates.set("thirdLevel",score));
                    users.updateOne(Filters.eq("id",message.getFrom().getId()),Updates.set("step","bmf"));
                    showMessage(message,HardCode.GoodAnswerevel3);
                }
                else if (step.equals("bm3")){
                    showMessage(message, HardCode.Level_Errore);
                }
                if (step.equals("bmf")&& isValidAnswer(message.getText(),numberOfFinalQuestions)) {
                    int score = calculatScore(text, HardCode.finalLevel_Answer);
                    users.updateOne(Filters.eq("id",message.getFrom().getId()),Updates.set("finalLevel",score));
                    users.updateOne(Filters.eq("id",message.getFrom().getId()),Updates.set("step","finish"));
                    showMessage(message,HardCode.GoodAnswerevelf);
                }
                else if (step.equals("bmf")){
                    showMessage(message, HardCode.Final_Errore);
                }
                if (message.getText().equals("مسابقه عکاسی")){
                    users.updateOne(Filters.eq("id",message.getFrom().getId()),Updates.set("step","pic"));
                    showMessage(message, HardCode.picMatch);
                }
                if (message.getText().equals("مسابقه کتابخوانی")){
                    bookMatch(message.getFrom());
                }
                if (message.getText().equals("ارتباط با ما")){
                    showMessage(message, HardCode.ContactUs);
                }
                if (message.getText().equals("توشه سفر")){
                    showMedia(message.getFrom());
                }
                if (message.getText().equals("صوتی")){
                    showAudioMedia(message.getFrom());
                }
                if (message.getText().equals("تصویری")){
                    showVideoMedia(message.getFrom());
                    showPhotoMedia(message.getFrom());
                }
                if (message.getText().equals("کتابخانه")){
                    showDocumentّMedia(message.getFrom());
                }
                if (message.getText().equals("متن و دل نوشته")){
                    showTextMedia(message.getFrom());
                }
                if (message.getText().equals("admin") && isAdmin(message.getFrom())){
                    users.updateOne(Filters.eq("id",message.getFrom().getId()),Updates.set("step","adminmessage"));
                }
            }
        }
    }
    void showMessage(User user,String str){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(str);
        sendMessage.setChatId(String.valueOf(user.getId()));
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    void showMessage(Message message,String str){
        showMessage(message.getFrom(),str);
    }
    @Override
    public String getBotUsername() {
        return "#############";
    }
    @Override
    public String getBotToken() {
        return "#####################################";
    }

}
