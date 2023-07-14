package by.andronovich.bot.BlazeCampBot.service;

import by.andronovich.bot.BlazeCampBot.config.BotConfig;
import by.andronovich.bot.BlazeCampBot.model.QuestionRepository;
import by.andronovich.bot.BlazeCampBot.model.User;
import by.andronovich.bot.BlazeCampBot.model.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    BotConfig botConfig;

    @Autowired
    private UserRepository userRepository;
    private QuestionRepository questionRepository;

    private static final String INSTAGRAM_ANDREI_BUTTON = "https://www.instagram.com/andrei_andronovich/";
    private static final String INSTAGRAM_CHURCH_YOUTH_BUTTON = "https://www.instagram.com/janov_youth/";
    public final static String Question_To_The_Pastor = "Вопрос пастору";
    public final static String SCHEDULE = "\uD83D\uDCC5 Расписание";
    public final static String WORKSHOP = "Семинары";
    public final static String BACK_BUTTON = "Отмена";
    //TODO возможно тут нужно записать в отдельную константу полное расписание.
    // Пока не знаю как это сделать правильно
    public final static String MONDAY_BUTTON = "Mon";
    public final static String TUESDAY_BUTTON = "Tue";
    public final static String WEDNESDAY_BUTTON = "Wed";
    public final static String THURSDAY_BUTTON = "Thu";
    public final static String FRIDAY_BUTTON = "Fri";
    public final static String SOCIAL_MEDIA = "Наши социальные сети";


    public TelegramBot(@Value("${bot.token}") String botToken, BotConfig botConfig) {
        super(botToken);
        this.botConfig = botConfig;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if ( (update.hasMessage() || update.hasCallbackQuery()) && (update.getMessage().getChatId() == 182370306L ||
                update.getCallbackQuery().getMessage().getChatId() == 182370306L)) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                var chatId = update.getMessage().getChatId();
                switch (messageText) {
                    case "/start" -> {
                        registerUser(update.getMessage());
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    }
                    case SCHEDULE -> {
                        log.info("нажата кнопка расписания");
                        schedualCommandReceived(chatId);
                    }
                    case SOCIAL_MEDIA -> {
                        log.info("нажата кнопка социальных");
                        socialMediaCommandReceived(chatId);
                    }
                    case Question_To_The_Pastor -> {
                        log.info("нажата кнопка вопрос пастору ");
                        // TODO Доделать полное описание вопроса пастору и реализацию отправки вопроса
                        sendMessageToThePastor(chatId);
                    }
                    default -> {
                        log.info("нажата кнопка  семинаров или неизвестной команды ");
                        sendMessage(chatId, "Sorry, command was not recognized", mainMarkup());
                    }
                }
            } else if (update.hasCallbackQuery()) {
                long chatId = update.getCallbackQuery().getMessage().getChatId();
                String callbackData = update.getCallbackQuery().getData();
                switch (callbackData) {
                    case BACK_BUTTON -> {
                        DeleteMessage message = new DeleteMessage();
                        message.setChatId(chatId);
                        message.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            log.error("Error callBackData Back_Button: " + e.getMessage());
                            throw new RuntimeException(e);
                        }
                    }
                    //case MONDAY_BUTTON ->

                }

            }
        }
    }

    private void schedualCommandReceived(Long chatId) {
        String text = "Выберите день :";
        sendMessageAndButton(chatId, text, schedualDaysButton());
    }

    private InlineKeyboardMarkup schedualDaysButton() {
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var button = new InlineKeyboardButton();
//TODO сделать кнопки красиво. А то они все  в одну линию идут
        button.setText(MONDAY_BUTTON);
        button.setCallbackData(MONDAY_BUTTON);
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText(TUESDAY_BUTTON);
        button.setCallbackData(THURSDAY_BUTTON);
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText(WEDNESDAY_BUTTON);
        button.setCallbackData(WEDNESDAY_BUTTON);
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText(THURSDAY_BUTTON);
        button.setCallbackData(THURSDAY_BUTTON);
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText(FRIDAY_BUTTON);
        button.setCallbackData(FRIDAY_BUTTON);
        rowInline.add(button);

        rowsInline.add(rowInline);
        inlineMarkup.setKeyboard(rowsInline);
        return inlineMarkup;
    }

    private void registerUser(Message message) {
        if (userRepository.findById(message.getChatId()).isEmpty()) {
            var chatId = message.getChatId();
            var chat = message.getChat();
            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }


    private void sendMessage(long chatId, String textToSend, ReplyKeyboardMarkup markup) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error Method SendMessage: " + e.getMessage());
        }
    }

    private void startCommandReceived(Long chatId, String firstName) {
        String answer = EmojiParser.parseToUnicode(" Hi, " + firstName + ", Nice to meet you " + ":grinning:");
        sendMessage(chatId, answer, mainMarkup());

    }

    private void socialMediaCommandReceived(long chatId) {
        String text = "*Сообщение о том что это наши соц сети и ещё картинку было бы неплохо. " +
                "А снизу нужно добавить кнопочки-ссылки на соцсети";
        sendMessageAndButton(chatId, text, socialMediaButton());
    }

    public ReplyKeyboardMarkup mainMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(SCHEDULE);
        row.add(WORKSHOP);
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add(SOCIAL_MEDIA);
        row.add(Question_To_The_Pastor);
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    //TODO отправить сообщение пастору
    public void sendMessageToThePastor(long chatId) {
        String text = "Вы можете написать анонимный вопрос пастору в поле ввода сообщения и нажать отправить. Или нажмите отмена ";
        sendMessageAndButton(chatId, text, backButton());
    }

    public void sendMessageAndButton(long chatId, String textToSend, InlineKeyboardMarkup inlineMarkup) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(textToSend);
        message.setReplyMarkup(inlineMarkup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    public InlineKeyboardMarkup backButton() {
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var backButton = new InlineKeyboardButton();
        backButton.setText(BACK_BUTTON);
        backButton.setCallbackData(BACK_BUTTON);
        rowInline.add(backButton);
        rowsInline.add(rowInline);
        inlineMarkup.setKeyboard(rowsInline);
        return inlineMarkup;
    }

    public InlineKeyboardMarkup socialMediaButton() {
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var instagramButton = new InlineKeyboardButton();
        instagramButton.setText("Instagram приятного человека");
        instagramButton.setUrl(INSTAGRAM_ANDREI_BUTTON);
        rowInline.add(instagramButton);
        rowsInline.add(rowInline);

        rowInline = new ArrayList<>();
        var instagramChurchButton = new InlineKeyboardButton();
        instagramChurchButton.setText("Instagram молодёжи");
        instagramChurchButton.setUrl(INSTAGRAM_CHURCH_YOUTH_BUTTON);
        rowInline.add(instagramChurchButton);
        rowsInline.add(rowInline);

        inlineMarkup.setKeyboard(rowsInline);
        return inlineMarkup;


    }
}
