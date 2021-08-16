package com.luna.common.text;

import com.luna.common.dto.constant.ResultCode;
import com.luna.common.exception.BaseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

/**
 * HTML 解析标签文字
 * 
 * @author luna
 */
public class Html2Text extends HTMLEditorKit.ParserCallback {

    private static final Html2Text HTML_2_TEXT = new Html2Text();
    private StringBuffer           content;

    public Html2Text() {}

    /**
     * 获取富文本内容
     * 
     * @param str
     * @return
     */
    public static String getContent(String str) {
        HTML_2_TEXT.parse(str);
        return HTML_2_TEXT.getText();
    }

    public void parse(String str) {
        InputStream iin = null;
        Reader in = null;
        try {
            iin = new ByteArrayInputStream(str.getBytes());
            in = new InputStreamReader(iin);
            content = new StringBuffer();
            ParserDelegator delegator = new ParserDelegator();
            delegator.parse(in, this, Boolean.TRUE);
        } catch (IOException e) {
            throw new BaseException(ResultCode.ERROR_SYSTEM_EXCEPTION, ResultCode.MSG_ERROR_SYSTEM_EXCEPTION);
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
        content.append(text);
    }

    public String getText() {
        return content.toString();
    }

}