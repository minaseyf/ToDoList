package todo.service;

import db.Database;
import db.Entity;
import db.exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.Task;

public class StepService {
    public static void setAsCompleted(int stepId) throws InvalidEntityException {
        Entity e = Database.get(stepId);
        if(e ==  null)
            throw new InvalidEntityException("Invalid step!");

        Step s = (Step) e;
        s.status = Step.Status.Completed;
        Database.update(s);
    }

    public static int saveStep(int taskRef, String title) throws InvalidEntityException {
        Step step = new Step(title, taskRef);
        Database.add(step);
        return step.id;
    }
}
