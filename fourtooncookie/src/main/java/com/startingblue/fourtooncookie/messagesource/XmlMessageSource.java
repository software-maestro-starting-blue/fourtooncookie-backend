package com.startingblue.fourtooncookie.messagesource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.AbstractMessageSource;
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

import lombok.Getter;

@Slf4j
@Getter
public class XmlMessageSource extends AbstractMessageSource {

    private static final String ENTRY_TAG_NAME = "entry";
    private static final String KEY_ATTRIBUTE_NAME = "key";
    private static final String MESSAGE_FILE_PATH_FORMAT = "/messages/messages_%s.xml";

    private static final Map<String, Map<String, String>> messages = new ConcurrentHashMap<>();

    private Locale defaultLocale;

    public XmlMessageSource() {}

    public void setDefaultLocale(Locale locale) {
        this.defaultLocale = locale;
        setMessages(locale);
    }

    public void setMessages(Locale locale) {
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
        Map<String, String> localeMessages = messages.getOrDefault(language, messages.get(defaultLocale));
        String message = localeMessages.get(code);
        return new MessageFormat(message, locale);
    }
}