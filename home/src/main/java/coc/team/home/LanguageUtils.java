package coc.team.home;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ?? on 2018-01-14.
 */


public class LanguageUtils {
    /**
     *  ??????
     */
    public boolean isChinese(String zj){
        boolean flog = false;
        Matcher m = Pattern.compile("[\u2E80-\u9FFF]*").matcher(zj);
        if(m.matches()){
            flog = true;
        }
        return flog;
    }
    /**
     * ????????????
     * @param lastName
     * @return
     */
    public static String toEnglish(char lastName){
        HanyuPinyinOutputFormat hanyuPinyin = new HanyuPinyinOutputFormat();
        hanyuPinyin.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        hanyuPinyin.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);
        hanyuPinyin.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
        String[] pinyinArray=null;
        try {
            //????????
            if(lastName>=0x4e00 && lastName<=0x9fa5){
                pinyinArray = PinyinHelper.toHanyuPinyinStringArray(lastName, hanyuPinyin);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        String LastName=pinyinArray[0].substring(0,1);

        return LastName.toUpperCase();
    }
}