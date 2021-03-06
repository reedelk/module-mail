package de.codecentric.reedelk.mail.internal.commons;

import de.codecentric.reedelk.mail.internal.FireEventOnResult;
import de.codecentric.reedelk.runtime.api.component.Component;
import de.codecentric.reedelk.runtime.api.component.InboundEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class OnMessageEvent {

    private static final Logger logger = LoggerFactory.getLogger(OnMessageEvent.class);

    private OnMessageEvent() {
    }

    public static boolean fire(Class<? extends Component> componentClazz, InboundEventListener listener, Message mail) {
        try {
            de.codecentric.reedelk.runtime.api.message.Message message = MailMessageToMessageMapper.map(componentClazz, mail);
            return fireEventAndWaitResult(listener, message);
        } catch (Exception exception) {
            String error = String.format("Could not process mail message=[%s]", exception.getMessage());
            logger.error(error);
            return false;
        }
    }

    public static boolean fire(Class<? extends Component> componentClazz,
                               InboundEventListener listener,
                               Message[] mails) {
        try {
            de.codecentric.reedelk.runtime.api.message.Message inMessage = MailMessageToMessageMapper.map(componentClazz, mails);
            return fireEventAndWaitResult(listener, inMessage);
        } catch (Exception exception) {
            String error = String.format("Could not process Mail Message IMAP Message=[%s]", exception.getMessage());
            logger.error(error);
            return false;
        }
    }

    private static boolean fireEventAndWaitResult(InboundEventListener listener,
                                                  de.codecentric.reedelk.runtime.api.message.Message inMessage) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        FireEventOnResult fireEvent = new FireEventOnResult(latch);

        listener.onEvent(inMessage, fireEvent);

        // We give at most X seconds for the flow to complete the processing of the
        // mail message. After X seconds an error will be thrown.
        boolean processed = latch.await(Defaults.FLOW_MAX_MESSAGE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (processed) {
            // We got an answer from the flow execution: the flow might have been successful or not and
            // therefore we return the result of the flow execution.
            return fireEvent.result();
        } else {
            // The flow took more than the await time to process the message, therefore the message
            // is considered as not 'processed'.
            return false;
        }
    }
}
