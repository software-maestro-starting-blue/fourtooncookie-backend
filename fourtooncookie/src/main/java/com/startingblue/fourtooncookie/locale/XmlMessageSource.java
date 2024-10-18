package com.startingblue.fourtooncookie.locale;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class XmlMessageSource extends AbstractMessageSource {

    private static final String DEFAULT_LANGUAGE = "en";
    private static final String KOREA_LANGUAGE = "ko";
    private static final String MESSAGE_FILE_PATH_FORMAT = "/messages/messages_%s.xml";
    private static final String ENTRY_TAG_NAME = "entry";
    private static final String KEY_ATTRIBUTE_NAME = "key";

    private static final Map<String, Map<String, String>> messages = new ConcurrentHashMap<>();

    public XmlMessageSource() {
        try {
            loadMessages(new Locale(DEFAULT_LANGUAGE));
            loadMessages(new Locale(KOREA_LANGUAGE));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load locale messages", e);
        }
    }

    private void loadMessages(Locale locale) throws Exception {
        String filename = getMessageFilePath(locale);
        Optional<Document> document = parseXmlDocument(filename);
        if (document.isPresent()) {
            Map<String, String> localeMessages = extractMessagesFromDocument(document.get());
            messages.put(locale.getLanguage(), localeMessages);
        } else {
            throw new RuntimeException("Failed to load messages file: " + filename);
        }
    }

    private String getMessageFilePath(Locale locale) {
        return String.format(MESSAGE_FILE_PATH_FORMAT, locale.getLanguage());
    }

    private Optional<Document> parseXmlDocument(String filename) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(Optional.ofNullable(getClass().getResourceAsStream(filename))
                    .orElseThrow(() -> new RuntimeException("Resource not found: " + filename)));
            return Optional.of(document);
        } catch (Exception e) {
            log.error("Failed to parse xml file: " + filename, e);
            return Optional.empty();
        }
    }

    private Map<String, String> extractMessagesFromDocument(Document document) {
        Map<String, String> localeMessages = new ConcurrentHashMap<>();
        Element root = document.getDocumentElement();
        NodeList entries = root.getElementsByTagName(ENTRY_TAG_NAME);

        for (int i = 0; i < entries.getLength(); i++) {
            Element entry = (Element) entries.item(i);
            String key = entry.getAttribute(KEY_ATTRIBUTE_NAME);
            String value = entry.getTextContent();
            localeMessages.put(key, value);
        }
        return localeMessages;
    }

    @Override
    public MessageFormat resolveCode(String code, Locale locale) {
        String language = locale.getLanguage();
        Map<String, String> localeMessages = messages.getOrDefault(language, messages.get(DEFAULT_LANGUAGE));
        String message = localeMessages.get(code);
        return new MessageFormat(message, locale);
    }
}
