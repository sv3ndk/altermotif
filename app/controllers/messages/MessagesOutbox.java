package controllers.messages;

import controllers.DabController;
import controllers.DabLoggedController;
import play.mvc.*;

public class MessagesOutbox extends DabLoggedController {

    public static void messagesOutbox() {
        render();
    }

}
