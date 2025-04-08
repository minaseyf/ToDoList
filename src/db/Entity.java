package db;

import java.util.Date;

public abstract class Entity {
    public int id;
    public abstract Entity copy();
    public abstract int getEntityCode();
}
