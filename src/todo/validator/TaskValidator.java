package todo.validator;

import db.Entity;
import db.Validator;
import db.exception.InvalidEntityException;
import example.Human;
import todo.entity.Task;

public class TaskValidator implements Validator {
    @Override
    public void validate(Entity entity) throws InvalidEntityException {
        if(!(entity instanceof Task))
            throw new IllegalArgumentException("Invalid type of input!");

        if (((Task) entity).title == null || ((Task) entity).title.isEmpty())
            throw new InvalidEntityException("Title can not be null or empty!");
    }
}
