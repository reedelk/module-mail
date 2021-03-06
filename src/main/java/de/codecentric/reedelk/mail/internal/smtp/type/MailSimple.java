package de.codecentric.reedelk.mail.internal.smtp.type;

import de.codecentric.reedelk.mail.component.SMTPMailSend;
import de.codecentric.reedelk.runtime.api.converter.ConverterService;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.message.Message;
import de.codecentric.reedelk.runtime.api.message.content.MimeType;
import de.codecentric.reedelk.runtime.api.message.content.Pair;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class MailSimple extends AbstractMailType {

    public MailSimple(SMTPMailSend component, ConverterService converterService) {
        super(component, converterService);
    }

    @Override
    public MailTypeStrategyResult create(FlowContext context, Message message) throws EmailException {
        Email email = new SimpleEmail();
        configureConnection(email);
        configureBaseMessage(context, message, email);

        Pair<String, String> charsetAndBody = buildCharsetAndMailBody(context, message);
        email.setCharset(charsetAndBody.left());
        email.setMsg(charsetAndBody.right());

        return MailTypeStrategyResult.create(email, charsetAndBody.right(), MimeType.TEXT_PLAIN);
    }
}
