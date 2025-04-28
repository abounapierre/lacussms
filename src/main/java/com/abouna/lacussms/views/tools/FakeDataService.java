package com.abouna.lacussms.views.tools;

import com.abouna.lacussms.entities.Message;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FakeDataService {

    public static List<Message> getFakeMessages() {
        return Arrays.asList(new Message(1,"Titre 1", "Message 1", new Date(), "237694567894"),
                new Message(2,"Titre 2", "Message 1", new Date(), "237694567894"),
                new Message(3,"Titre 3", "Message 1", new Date(), "237694567894"),
                new Message(4,"Titre ", "Message 1", new Date(), "237694567894"),
                new Message(5,"Titre 1", "Message 1", new Date(), "237694567894"),
                new Message(6,"Titre 1", "Message 1", new Date(), "237694567894"));
    }
}
