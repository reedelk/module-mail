package com.reedelk.mail.internal.send.attachment;

import com.reedelk.mail.component.AttachmentDefinition;
import com.reedelk.mail.internal.commons.ContentType;
import com.reedelk.mail.internal.commons.Headers;
import com.reedelk.runtime.api.flow.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.ScriptEngineService;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

public class ExpressionType implements Strategy {

    @Override
    public MimeBodyPart attach(ScriptEngineService scriptEngine,
                               AttachmentDefinition definition,
                               FlowContext context,
                               Message message) throws MessagingException {

        String charset = definition.getCharset();
        String attachmentName = definition.getName();
        String contentType = definition.getContentType();

        final String attachmentContentType = ContentType.from(contentType, charset);
        final String contentTransferEncoding = definition.getContentTransferEncoding();

        ByteArrayDataSource dataSource = scriptEngine.evaluate(definition.getExpression(), context, message)
                .map(bytes -> {
                    ByteArrayDataSource ds = new ByteArrayDataSource(bytes, attachmentContentType);
                    ds.setName(attachmentName);
                    return ds;
                }).orElse(new ByteArrayDataSource(new byte[0], attachmentContentType));

        MimeBodyPart part = new MimeBodyPart();
        part.setDataHandler(new DataHandler(dataSource));
        part.addHeader(Headers.CONTENT_TRANSFER_ENCODING, contentTransferEncoding);
        return part;
    }
}
