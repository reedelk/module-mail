package de.codecentric.reedelk.mail.internal.script;

import de.codecentric.reedelk.runtime.api.annotation.Type;
import de.codecentric.reedelk.runtime.api.annotation.TypeFunction;

@Type(global = true,
        description = "The MailAttachmentBuilder creates new Mail Attachment object.")
public class MailAttachmentBuilder {

    @TypeFunction(signature = "create()",
            example = "MailAttachmentBuilder.create()" +
                    ".filename('my-picture.png')" +
                    ".binary(message.payload(), 'image/png')" +
                    ".build()",
            description = "Creates a new MailAttachment object.")
    public MailAttachment create() {
        return new MailAttachment();
    }
}
