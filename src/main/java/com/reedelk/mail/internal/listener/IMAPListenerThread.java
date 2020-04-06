package com.reedelk.mail.internal.listener;

import com.sun.mail.imap.IMAPFolder;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.io.Closeable;

public class IMAPListenerThread extends Thread implements Closeable {

    private final Folder folder;
    private final String username;
    private final String password;
    private volatile boolean running = true;

    public IMAPListenerThread(String username, String password, Folder folder) {
        super();
        this.folder = folder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run() {
        while (running) {
            try {
                ensureOpen(folder);
                System.out.println("enter idle");
                ((IMAPFolder) folder).idle();
            } catch (Exception e) {
                // something went wrong
                // wait and try again
                e.printStackTrace();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    // ignore
                }
            }

        }
    }

    public void ensureOpen(final Folder folder) throws MessagingException {

        if (folder != null) {
            Store store = folder.getStore();
            if (store != null && !store.isConnected()) {
                store.connect(username, password);
            }
        } else {
            throw new MessagingException("Unable to open a null folder");
        }

        if (folder.exists() && !folder.isOpen() && (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
            System.out.println("open folder " + folder.getFullName());
            folder.open(Folder.READ_ONLY);
            if (!folder.isOpen())
                throw new MessagingException("Unable to open folder " + folder.getFullName());
        }

    }

    @Override
    public synchronized void close() {
        if (!running) return;
        this.running = false;
    }
}