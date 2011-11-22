package controllers.messages;

import controllers.DabController;
import controllers.DabLoggedController;
import play.mvc.*;

public class MessagesInbox extends DabLoggedController {

    public static void messagesInbox() {
        render();
    }

}
