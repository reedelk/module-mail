package de.codecentric.reedelk.mail.internal.pop3;

import de.codecentric.reedelk.mail.component.POP3Configuration;
import de.codecentric.reedelk.mail.component.pop3.POP3Protocol;
import de.codecentric.reedelk.mail.internal.commons.Defaults;
import de.codecentric.reedelk.mail.internal.exception.MailMessageConfigurationException;

import java.util.Optional;
import java.util.Properties;

import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class POP3Properties extends Properties {

    public POP3Properties(POP3Configuration configuration) {
        POP3Protocol protocol = Optional.ofNullable(configuration.getProtocol()).orElse(POP3Protocol.POP3);

        String host = Optional.ofNullable(configuration.getHost())
                .orElseThrow(() -> new MailMessageConfigurationException("Host is mandatory"));

        boolean startTlsEnable = Optional.ofNullable(configuration.getStartTlsEnabled()).orElse(Defaults.TLS_ENABLE);
        Integer connectionTimeout = Optional.ofNullable(configuration.getConnectTimeout()).orElse(Defaults.CONNECT_TIMEOUT);
        Integer socketTimeout = Optional.ofNullable(configuration.getSocketTimeout()).orElse(Defaults.SOCKET_TIMEOUT);

        if (POP3Protocol.POP3.equals(protocol)) {
            // POP3
            Integer port = Optional.ofNullable(configuration.getPort()).orElse(Defaults.POP3.DEFAULT_PORT);
            setProperty("mail.transport.protocol", Defaults.POP3.TRANSPORT);
            setProperty("mail.store.protocol", Defaults.POP3.TRANSPORT);
            setProperty("mail.pop3.host", host);
            setProperty("mail.pop3.port", String.valueOf(port));
            setProperty("mail.pop3.auth", Boolean.TRUE.toString());
            setProperty("mail.pop3.timeout", String.valueOf(socketTimeout));
            setProperty("mail.pop3.starttls.enable", String.valueOf(startTlsEnable));
            setProperty("mail.pop3.connectiontimeout", String.valueOf(connectionTimeout));

        } else {
            // POP3s
            Integer port = Optional.ofNullable(configuration.getPort()).orElse(Defaults.POP3s.DEFAULT_PORT);
            setProperty("mail.transport.protocol", Defaults.POP3s.TRANSPORT);
            setProperty("mail.store.protocol", Defaults.POP3s.TRANSPORT);
            setProperty("mail.pop3s.host", host);
            setProperty("mail.pop3s.port", String.valueOf(port));
            setProperty("mail.pop3s.auth", Boolean.TRUE.toString());
            setProperty("mail.pop3s.timeout", String.valueOf(socketTimeout));
            setProperty("mail.pop3s.starttls.enable", String.valueOf(startTlsEnable));
            setProperty("mail.pop3s.connectiontimeout", String.valueOf(connectionTimeout));

            if (isNotBlank(configuration.getTrustedHosts())) {
                setProperty("mail.pop3s.ssl.trust", configuration.getTrustedHosts());
            }
        }
    }
}
