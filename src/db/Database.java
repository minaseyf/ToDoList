package db;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import db.*;
import db.exception.*;
import example.*;
import db.exception.*;

public class Database {
    public static ArrayList<Entity> entities = new ArrayList<>();
    private static int ID = 1;
    private static HashMap<Integer, Validator> validators = new HashMap<>();

    private Database() {
    }

    public static void add(Entity e) throws InvalidEntityException {
        Validator validator = validators.get(e.getEntityCode());
        if(e instanceof Trackable){
            e.id = ID;
            ((Trackable) e).setLastModificationDate(new Date());
            ((Trackable) e).setCreationDate(new Date());
            entities.add(e.copy());
        }
        else{
            validator.validate(e);
            e.id = ID;
            entities.add(e.copy());
        }
        ID++;
    }

    public static Entity get(int id) {
        for (Entity e : entities) {
            if (e.id == id)
                return e.copy();
        }
        throw new EntityNotFoundException(id);
    }

    public static void delete(int id) {
        for (Entity e : entities) {
            if (e.id == id) {
                entities.remove(e);
                return;
            }
        }
        throw new EntityNotFoundException();
    }

    public static void update(Entity e) throws EntityNotFoundException, InvalidEntityException {
        Validator validator = validators.get(e.getEntityCode());
        if (validator != null) {
            validator.validate(e);
        }
        if (e instanceof Trackable) {
            ((Trackable) e).setLastModificationDate(new Date());
        }
        int found = 0;
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).id == e.id) {
                entities.set(i, e.copy());
                found = 1;
                break;
            }
        }
        if (found == 0) {
            throw new EntityNotFoundException(e.id);
        }
    }

    public static void registerValidator(int entityCode, Validator validator) {
        if (validators.containsKey(entityCode) || validators.containsValue(validator))
            throw new IllegalArgumentException("The entityCode or the validator already exists!");

        validators.put(entityCode, validator);
    }

    public static ArrayList<Entity> getAll(int entityCode) {
        ArrayList<Entity> entityList = new ArrayList<>();
        for (Entity e : entities) {
            if (e.getEntityCode() == entityCode)
                entityList.add(e);
        }
        return entityList;
    }
}