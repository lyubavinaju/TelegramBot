import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Bot extends TelegramLongPollingBot {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString()); // to which chat we are sending the message
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

   private String lastCommand = "";

    public void onUpdateReceived(Update update) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = new Date();

        System.out.println(dateFormat.format(date));
        Message message = update.getMessage();
        if (message != null && message.hasText()) {

            if (message.getText().equals("/date")) {
                sendMsg(message, dateFormat.format(date));
            } else if (message.getText().equals("/add")) {
                sendMsg(message, "Введите дату и название события");
            } else if (message.getText().equals("/all")) {
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(new File("output.txt")));
                    String s = "";
                    while (s != null) {
                        if (s.length() >= 2) {
                            String[] strings = s.split(" ");
                            System.out.println(strings[0] + " " + strings[1]);
                            if (Long.parseLong(strings[0]) == message.getChatId()) {
                                stringBuilder.append(s.substring(strings[0].length())).append("\n");
                            }
                        }

                        s = br.readLine();
                    }
                    sendMsg(message, stringBuilder.toString());
                } catch (IOException e) {
                    try {
                        if (br != null)
                            br.close();
                    } catch (IOException e1) {
                        sendMsg(message, "error");
                    }

                }

            } else if (!message.getText().startsWith("/") && "/add".equals(lastCommand)) {
                try {
                    FileWriter writer = new FileWriter(new File("output.txt"), true);
                    PrintWriter printWriter = new PrintWriter(writer);

                    printWriter.println(update.getMessage().getChatId() + " " + update.getMessage().getText());
                    printWriter.close();
                    writer.close();
                    sendMsg(message, "добавлено");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                sendMsg(message, update.getMessage().getText());

            }
            if (message.getText().startsWith("/")) {
                lastCommand = message.getText();
            } else {
                lastCommand = "";
            }
        }
    }

    public String getBotUsername() {
        return "TheFriendliestDoveBot";
    }

    public String getBotToken() {
        return "1089800373:AAGTZYoC1GgpFVfeDUDkkAJ_yE6PtvT7Wvk";
    }
}
