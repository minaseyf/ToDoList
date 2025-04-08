package example;

import db.Entity;
import db.Validator;
import db.exception.InvalidEntityException;

public class HumanValidator implements Validator {
    @Override
    public void validate(Entity entity) throws InvalidEntityException {
        if (!(entity instanceof Human))
            throw new IllegalArgumentException("Entity is not an instance of Human!");

        if (((Human) entity).age < 0)
            throw new InvalidEntityException("Age can not be negative!");

        if (((Human) entity).name == null || ((Human) entity).name.isEmpty())
            throw new InvalidEntityException("Name can not be null or empty!");
    }
}
