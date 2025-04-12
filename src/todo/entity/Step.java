package todo.entity;

import db.Entity;
import db.Trackable;

import java.util.Date;

public class Step extends Entity {
    @Override
    public Entity copy() {
        Step stepCopy = new Step(this.title, this.taskRef);
        stepCopy.id = id;
        stepCopy.lastModificationDate = lastModificationDate;
        stepCopy.status = status;
        stepCopy.creationDate = creationDate;

        return stepCopy;
    }

    @Override
    public int getEntityCode() {
        return Step_ENTITY_CODE;
    }

    public enum Status {
        NotStarted, Completed;
    }
    public String title;
    public Status status;
    public int taskRef;
    public static final int Step_ENTITY_CODE = 20;

    public Step(String title, int taskRef) {
        this.title = title;
        this.taskRef = taskRef;
        this.status = Status.NotStarted;
    }
}
