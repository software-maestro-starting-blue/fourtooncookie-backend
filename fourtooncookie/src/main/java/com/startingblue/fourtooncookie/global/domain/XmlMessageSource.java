package com.startingblue.fourtooncookie.global.domain;

import org.springframework.context.support.AbstractMessageSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class XmlMessageSource extends AbstractMessageSource {
    private final Map<String, String> koreanMessages = new HashMap<>();
    private final Map<String, String> englishMessages = new HashMap<>();

    public XmlMessageSource() {
        try {
            loadMessages(new Locale("ko"), koreanMessages);
            loadMessages(new Locale("en"), englishMessages);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load locale messages", e);
        }
    }

    private void loadMessages(Locale locale, Map<String, String> messages) throws Exception {
        String filename = "/messages/messages_en.xml";

        if (locale.getLanguage().equals("ko")) {
            filename = "/messages/messages_ko.xml";
        } else if (locale.getLanguage().equals("en")) {
            filename = "/messages/messages_en.xml";
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream(filename));
        Element root = document.getDocumentElement();
        NodeList entries = root.getElementsByTagName("entry");

        for (int i = 0; i < entries.getLength(); i++) {
            Element entry = (Element) entries.item(i);
            String key = entry.getAttribute("key");
            String value = entry.getTextContent();
            messages.put(key, value);
        }
    }

    @Override
    public MessageFormat resolveCode(String code, Locale locale) {
        Map<String, String> selectedMessages = new HashMap<>();
        if (locale.getLanguage().equals("ko")) {
            selectedMessages = koreanMessages;
        } else if (locale.getLanguage().equals("en")) {
            selectedMessages = englishMessages;
        }

        String message = selectedMessages.get(code);
        return (message != null) ? new MessageFormat(message, locale) : null;
    }
}
