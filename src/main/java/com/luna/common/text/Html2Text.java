package com.luna.common.text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * HTML 解析标签文字
 * @author luna
 */
public class Html2Text extends HTMLEditorKit.ParserCallback {

    private static final Html2Text html2Text = new Html2Text();
    StringBuffer                   s;

    public Html2Text() {}

    /**
     * 获取富文本内容
     * 
     * @param str
     * @return
     */
    public static String getContent(String str) {
        html2Text.parse(str);
        return html2Text.getText();
    }

    public void parse(String str) {
        InputStream iin = null;
        Reader in = null;
        try {
            iin = new ByteArrayInputStream(str.getBytes());
            in = new InputStreamReader(iin);
            s = new StringBuffer();
            ParserDelegator delegator = new ParserDelegator();
            delegator.parse(in, this, Boolean.TRUE);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                iin.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleText(char[] text, int pos) {
        s.append(text);
    }

    public String getText() {
        return s.toString();
    }

}