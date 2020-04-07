package com.reedelk.mail.component;

import com.reedelk.mail.internal.listener.ProtocolMailListener;
import com.reedelk.mail.internal.listener.imap.ImapIdleMailListener;
import com.reedelk.mail.internal.listener.imap.ImapPollMailListener;
import com.reedelk.mail.internal.listener.pop3.POP3MailListener;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.component.AbstractInbound;
import org.osgi.service.component.annotations.Component;

import static com.reedelk.runtime.api.commons.ConfigurationPreconditions.requireNotNull;
import static org.osgi.service.component.annotations.ServiceScope.PROTOTYPE;

@ModuleComponent("Mail Listener")
@Description("The Email listener can be used to trigger events whenever new emails " +
        "are received on the server.")
@Component(service = MailListener.class, scope = PROTOTYPE)
public class MailListener extends AbstractInbound {

    @Property("Protocol")
    @Group("General")
    private Protocol protocol;

    @Property("POP3 Connection")
    @Group("General")
    @When(propertyName = "protocol", propertyValue = "POP3")
    private POP3Configuration pop3Configuration;

    @Property("IMAP Strategy")
    @Example("IDLE")
    @DefaultValue("POLLING")
    @When(propertyName = "protocol", propertyValue = "IMAP")
    @When(propertyName = "protocol", propertyValue = When.NULL)
    private ImapListeningStrategy imapStrategy;

    @Property("IMAP Connection")
    @Group("General")
    @When(propertyName = "protocol", propertyValue = "IMAP")
    @When(propertyName = "protocol", propertyValue = When.NULL)
    private IMAPConfiguration imapConfiguration;

    private ProtocolMailListener mailListener;

    @Override
    public void onStart() {
        requireNotNull(MailListener.class, protocol, "Protocol");
        mailListener = createMailListener();
        mailListener.start();
    }

    @Override
    public void onShutdown() {
        if (mailListener != null) {
            mailListener.stop();
        }
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public POP3Configuration getPop3Configuration() {
        return pop3Configuration;
    }

    public void setPop3Configuration(POP3Configuration pop3Configuration) {
        this.pop3Configuration = pop3Configuration;
    }

    public IMAPConfiguration getImapConfiguration() {
        return imapConfiguration;
    }

    public void setImapConfiguration(IMAPConfiguration imapConfiguration) {
        this.imapConfiguration = imapConfiguration;
    }

    public ImapListeningStrategy getImapStrategy() {
        return imapStrategy;
    }

    public void setImapStrategy(ImapListeningStrategy imapStrategy) {
        this.imapStrategy = imapStrategy;
    }

    private ProtocolMailListener createMailListener() {
        if (Protocol.POP3.equals(protocol)) {
            // POP3
            requireNotNull(MailListener.class, pop3Configuration, "POP3 Configuration");
            return new POP3MailListener(pop3Configuration);
        } else {
            // IMAP
            requireNotNull(MailListener.class, imapConfiguration, "IMAP Configuration");
            if (ImapListeningStrategy.IDLE.equals(imapStrategy)) {
                return new ImapIdleMailListener(imapConfiguration, this);
            } else {
                return new ImapPollMailListener(imapConfiguration, this);
            }
        }
    }
}
