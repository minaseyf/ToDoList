package example;

import java.util.Date;

import db.Entity;
import db.Trackable;

public class Document extends Entity implements Trackable {
    public String content;
    public static final int Document_ENTITY_CODE = 12;

    public Document(String content) {
        this.content = content;
    }

    @Override
    public void setCreationDate(Date date) {
        creationDate = date;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public void setLastModificationDate(Date date) {
        lastModificationDate = date;
    }

    @Override
    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    @Override
    public Entity copy() {
        Document documentCopy = new Document(content);
        documentCopy.lastModificationDate = lastModificationDate;
        documentCopy.creationDate = creationDate;
        documentCopy.id = id;
        return documentCopy;
    }

    @Override
    public int getEntityCode() {
        return Document_ENTITY_CODE;
    }
}
