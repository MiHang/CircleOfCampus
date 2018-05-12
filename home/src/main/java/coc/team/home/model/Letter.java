package coc.team.home.model;

import coc.team.home.R;

/**
 * 索引条 javabeen
 */

public class Letter {
    int bg=R.color.white;//默认背景颜色
    int TextColor= R.color.black;//默认文字颜色
    int Hover_bg=R.drawable.letter;//经过时背景颜色
    int Hover_TextColor=R.color.white;//经过时文字颜色
    String Letter;//字母信息
    boolean isHover=false;//是否经过

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getHover_bg() {
        return Hover_bg;
    }

    public void setHover_bg(int hover_bg) {
        Hover_bg = hover_bg;
    }

    public int getTextColor() {
        return TextColor;
    }

    public void setTextColor(int textColor) {
        TextColor = textColor;
    }

    public int getHover_TextColor() {
        return Hover_TextColor;
    }

    public void setHover_TextColor(int hover_TextColor) {
        Hover_TextColor = hover_TextColor;
    }

    public String getLetter() {
        return Letter;
    }

    public void setLetter(String letter) {
        Letter = letter;
    }

    public boolean isHover() {
        return isHover;
    }

    public void setHover(boolean hover) {
        isHover = hover;
    }
}
