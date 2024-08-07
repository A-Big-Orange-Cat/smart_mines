package com.jkzz.smart_mines.communication.HslCommunication;

import com.jkzz.smart_mines.communication.HslCommunication.Language.DefaultLanguage;
import com.jkzz.smart_mines.communication.HslCommunication.Language.English;

import java.util.Locale;

public class StringResources {

    /**
     * 获取或设置系统的语言选项 ->
     * Gets or sets the language options for the system
     */
    public static DefaultLanguage Language = new DefaultLanguage();

    static {
        Locale locale = Locale.getDefault();
        if (locale.getLanguage().startsWith("zh")) {
            SetLanguageChinese();
        } else {
            SeteLanguageEnglish();
        }
    }

    /**
     * 将语言设置为中文 ->
     * Set the language to Chinese
     */
    public static void SetLanguageChinese() {
        Language = new DefaultLanguage();
    }


    /**
     * 将语言设置为英文 ->
     * Set the language to English
     */
    public static void SeteLanguageEnglish() {
        Language = new English();
    }

}
