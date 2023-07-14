package by.andronovich.bot.BlazeCampBot.service;

import by.andronovich.bot.BlazeCampBot.config.BotConfig;
import by.andronovich.bot.BlazeCampBot.model.*;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    BotConfig botConfig;

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final ScheduleRepository scheduleRepository;
    private final Button button;
    private final Admin admin;

    public static final String INSTAGRAM_ANDREI_BUTTON = "https://www.instagram.com/andrei_andronovich/";
    public static final String INSTAGRAM_CHURCH_YOUTH_BUTTON = "https://www.instagram.com/janov_youth/";
    public final static String Question_To_The_Pastor = "Вопрос пастору";
    public final static String SCHEDULE = "\uD83D\uDCC5 Расписание";
    public final static String WORKSHOP = "Семинары";
    public final static String BACK_BUTTON = "Отмена";
    public final static String SOCIAL_MEDIA = "Наши социальные сети";

    public TelegramBot(@Value("${bot.token}") String botToken, BotConfig botConfig, UserRepository userRepository, QuestionRepository questionRepository, ScheduleRepository scheduleRepository, Button button, Admin admin) {
        super(botToken);
        this.botConfig = botConfig;

        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.scheduleRepository = scheduleRepository;
        this.button = button;
        this.admin = admin;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && userRepository.findById(update.getMessage().getChatId()).get().getRole().contains("admin")) {
            String messageText = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start" -> {
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                }
                case SCHEDULE -> {
                    log.info(update.getMessage().getChat().getFirstName() + update.getMessage().getChat().getFirstName() + ": нажата кнопка расписания");
                    scheduleCommandReceived(chatId);
                }
                case WORKSHOP -> {
                    log.info(update.getMessage().getChat().getFirstName() + update.getMessage().getChat().getFirstName() + ": нажата кнопка семинаров");
                    workshopCommandReceived(chatId);
                }
                case SOCIAL_MEDIA -> {
                    log.info(update.getMessage().getChat().getFirstName() + update.getMessage().getChat().getFirstName() + ": нажата кнопку социальных");
                    socialMediaCommandReceived(chatId);
                }
                case Question_To_The_Pastor -> {
                    log.info(update.getMessage().getChat().getFirstName() + update.getMessage().getChat().getFirstName() + ": нажата кнопка вопрос пастору ");
                    // TODO Доделать полное описание вопроса пастору и реализацию отправки вопроса
                    questionToThePastorCommandReceived(chatId, update);
                }
                case "Edit" -> {
                    if (userRepository.findById(chatId).get().getRole().contains("admin")) {
                        sendMessage(chatId, "Что вы хотите изменить?", editMainMarkup(chatId));
                    } else {
                        sendMessage(chatId, "Sorry, command was not recognized", mainMarkup(chatId));
                    }
                }

                case "editSCHEDULE" -> editScheduleCommandReceived(chatId);


                default -> {
                    log.info("нажата кнопка неизвестной команды ");
                    sendMessage(chatId, "Sorry, command was not recognized", mainMarkup(chatId));
                }
            }
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage().getChatId() == 182370306L) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            log.info(" получение chatId from CallBack: " + update.getCallbackQuery().getMessage().getChatId());
            String callbackData = update.getCallbackQuery().getData();
            switch (callbackData) {
                case BACK_BUTTON -> {
                    DeleteMessage message = new DeleteMessage();
                    message.setChatId(chatId);
                    message.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        log.error("Error callBackData.Back_Button: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
                case "Mon" -> {
                    String description = scheduleRepository.findById("Mon").get().getFullScheduleDescription();
                    sendMessage(chatId, description, mainMarkup(chatId));
                }
                case "Tue" -> {
                    String description = scheduleRepository.findById("Tue").get().getFullScheduleDescription();
                    sendMessage(chatId, description, mainMarkup(chatId));
                }
                case "Wed" -> {
                    String description = scheduleRepository.findById("Wed").get().getFullScheduleDescription();
                    sendMessage(chatId, description, mainMarkup(chatId));
                }
                case "Thu" -> {
                    String description = scheduleRepository.findById("Thu").get().getFullScheduleDescription();
                    sendMessage(chatId, description, mainMarkup(chatId));
                }
                case "Fri" -> {
                    String description = scheduleRepository.findById("Fri").get().getFullScheduleDescription();
                    sendMessage(chatId, description, mainMarkup(chatId));
                }
                // todo так нужно написать для каждого дня недели
                case "editMon" -> {
                    Schedule schedule = new Schedule();
                    schedule.setDay("Mon");
                    schedule.setFullScheduleDescription("mdmvmdmvldmvmdmvmdmlvml");
                    scheduleRepository.save(schedule);
                    sendMessage(chatId, "расписание изменено", mainMarkup(chatId));

                }


            }

        }
    }

    private void workshopCommandReceived(Long chatId) {
        String text = "Семинар на сегодня";
        sendMessage(chatId, text, mainMarkup(chatId));
    }


    private void scheduleCommandReceived(Long chatId) {
        String text = "Выберите день :";
        sendMessageAndButton(chatId, text, button.scheduleDaysButton());
    }

    private void editScheduleCommandReceived(Long chatId) {
        String text = "Выберите день который хотите изменить :";
        sendMessageAndButton(chatId, text, button.editScheduleDaysButton());
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
            user.setRole("user");
            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }


    public void sendMessage(long chatId, String textToSend, ReplyKeyboardMarkup markup) {
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
        sendMessage(chatId, answer, mainMarkup(chatId));

    }

    private void socialMediaCommandReceived(long chatId) {
        String text = "*Сообщение о том что это наши соц сети и ещё картинку было бы неплохо. " + "А снизу нужно добавить кнопочки-ссылки на соцсети";
        sendMessageAndButton(chatId, text, button.socialMediaButton());
    }

    //TODO отправить сообщение пастору

    public void questionToThePastorCommandReceived(long chatId, Update update) {
        String text = "Вы можете написать анонимный вопрос пастору в поле ввода сообщения и нажать отправить. Или нажмите отмена " + " Id сообщения " + update.getMessage().getMessageId();
        sendMessageAndButton(chatId, text, button.backButton());
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


    public ReplyKeyboardMarkup mainMarkup(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(SCHEDULE);
        row.add(WORKSHOP);
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add(SOCIAL_MEDIA);
        row.add(Question_To_The_Pastor);
        // todo проверять на наличие в массиве. То есть добавить ещё админов
        if (userRepository.findById(chatId).get().getChatId() == 182370306L) {
            row.add("Edit");
        }
        //todo тут нужно написать айди василия. Пока что стоит мой
        if (userRepository.findById(chatId).get().getChatId() == 182370306L) {
            row.add("Questions");
        }
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }


    public ReplyKeyboardMarkup editMainMarkup(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add("editSCHEDULE");
        row.add("editWORKSHOP");
        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("editSOCIAL_MEDIA");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
