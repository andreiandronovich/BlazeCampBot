package by.andronovich.bot.BlazeCampBot.service;

import by.andronovich.bot.BlazeCampBot.model.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static by.andronovich.bot.BlazeCampBot.service.TelegramBot.*;

@Component
@Slf4j
public class Button {

    private final ScheduleRepository scheduleRepository;
@Autowired
    public Button(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }


    public  InlineKeyboardMarkup socialMediaButton() {
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

    public  InlineKeyboardMarkup backButton() {
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

    public InlineKeyboardMarkup scheduleDaysButton() {
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var button = new InlineKeyboardButton();
        button.setText("Mon");
        button.setCallbackData("Mon");
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText("Tue");
        button.setCallbackData("Tue");
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText("Wed");
        button.setCallbackData("Wed");
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText("Thu");
        button.setCallbackData("Thu");
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText("Fri");
        button.setCallbackData("Fri");
        rowInline.add(button);

        rowsInline.add(rowInline);
        inlineMarkup.setKeyboard(rowsInline);
        return inlineMarkup;
    }
    public InlineKeyboardMarkup editScheduleDaysButton() {
        InlineKeyboardMarkup inlineMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        var button = new InlineKeyboardButton();
        //TODO сделать кнопки красиво. А то они все  в одну линию идут
        button.setText("Mon");
        button.setCallbackData("editMon");
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText("Tue");
        button.setCallbackData("editTue");
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText("Wed");
        button.setCallbackData("editWed");
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText("Thu");
        button.setCallbackData("editThu");
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText("Fri");
        button.setCallbackData("editFri");
        rowInline.add(button);

        rowsInline.add(rowInline);
        inlineMarkup.setKeyboard(rowsInline);
        return inlineMarkup;
    }


}
