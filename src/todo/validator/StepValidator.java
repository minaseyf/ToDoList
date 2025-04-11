package todo.validator;

import db.Database;
import db.Entity;
import db.Validator;
import db.exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.Task;

public class StepValidator implements Validator {
    @Override
    public void validate(Entity entity) throws InvalidEntityException {
        if(!(entity instanceof Step))
            throw new IllegalArgumentException("Invalid type of input!");

        if (((Step) entity).title == null || ((Step) entity).title.isEmpty())
            throw new InvalidEntityException("Title can not be null or empty!");

        int found = 0;
        for(Entity e : Database.entities) {
            if (e.id == ((Step) entity).taskRef) {
                found = 1;
                break;
            }
        }
        if(found == 0)
            throw new InvalidEntityException("Invalid taskID!");
    }
}
