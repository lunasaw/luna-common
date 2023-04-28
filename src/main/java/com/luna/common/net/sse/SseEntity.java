package com.luna.common.net.sse;

import com.google.common.base.Splitter;
import com.luna.common.io.IoUtil;
import com.luna.common.text.CharsetUtil;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.AbstractHttpEntity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class SseEntity extends AbstractHttpEntity {

    private BlockingQueue<Event> events       = new ArrayBlockingQueue<>(100);
    private StringBuilder        currentEvent = new StringBuilder();

    private final StringBuilder  allEvent     = new StringBuilder();
    private int                  newLineCount = 0;
    private String               lastEventId;

    public StringBuilder getAllEvent() {
        return allEvent;
    }

    public void setEvents(BlockingQueue<Event> events) {
        this.events = events;
    }

    public SseEntity(ContentType contentType) {
        super(contentType, CharsetUtil.defaultCharsetName());
    }

    public void pushBuffer(CharBuffer buf, boolean endOfStream) {
        while (buf.hasRemaining()) {
            processChar(buf.get());
        }
    }

    private void processChar(char nextChar) {
        if (nextChar == '\n') {
            newLineCount++;
        } else {
            newLineCount = 0;
        }
        if (newLineCount > 1) {
            processCurrentEvent();
            currentEvent = new StringBuilder();
        } else {
            currentEvent.append(nextChar);
        }
    }

    // Parse raw data for each event to create processed event object
    // Parsing specification - https://www.w3.org/TR/eventsource/#parsing-an-event-stream
    private void processCurrentEvent() {
        String rawEvent = currentEvent.toString();
        String id = "";
        String event = "";
        int retry = 0;
        StringBuilder data = new StringBuilder();
        List<String> list = Splitter.on("\n").splitToList(rawEvent);
        for (String[] lineTokens : list.stream().map(s -> s.split(":", 2)).collect(Collectors.toList())) {
            switch (lineTokens[0]) {
                case "id":
                    id = lineTokens[1].trim();
                    break;
                case "event":
                    event = lineTokens[1].trim();
                    break;
                case "retry":
                    retry = Integer.parseInt(lineTokens[1].trim());
                    break;
                case "data":
                    data.append(lineTokens[1].trim());
                    break;
            }
        }
        events.offer(new Event(id, event, data.toString(), retry));
        currentEvent = new StringBuilder();
        allEvent.append(rawEvent);
        newLineCount = 0;
        lastEventId = id;
    }

    public BlockingQueue<Event> getEvents() {
        return events;
    }

    public boolean hasMoreEvents() {
        return events.size() > 0;
    }

    public String getLastEventId() {
        return lastEventId;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public long getContentLength() {
        return allEvent.length();
    }

    @Override
    public InputStream getContent() throws IOException, UnsupportedOperationException {
        return IoUtil.toStream(allEvent.toString(), Charset.defaultCharset());
    }

    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        IoUtil.writeObjects(outStream, true, allEvent);
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public void close() throws IOException {

    }
}
