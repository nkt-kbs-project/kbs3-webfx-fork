package naga.core.orm.entity;


import naga.core.orm.entity.lciimpl.EntityDataWriter;
import naga.core.orm.expression.Expression;

import java.util.HashMap;
import java.util.Map;

/**
 * A store for entities that are transactionally coherent.
 *
 * @author Bruno Salmon
 */
public class EntityStore {

    private final Map<EntityId, Entity> entities = new HashMap<>();
    private final Map<Object, EntityList> entityLists = new HashMap<>();
    private final EntityDataWriter entityDataWriter = new EntityDataWriter(this);

    // ID management

    public EntityId getEntityID(Object domainClassId, Object primaryKey) {
        return new EntityId(domainClassId, primaryKey);
    }

    // Entity management

    public Entity getEntity(EntityId entityId) {
        return entities.get(entityId);
    }

    public Entity getOrCreateEntity(Object domainClassId, Object primaryKey) {
        if (primaryKey == null)
            return null;
        return getOrCreateEntity(getEntityID(domainClassId, primaryKey));
    }

    public Entity getOrCreateEntity(EntityId id) {
        Entity entity = getEntity(id);
        if (entity == null)
            entities.put(id, entity = createEntity(id));
        return entity;
    }

    protected Entity createEntity(EntityId id) {
        return new DynamicEntity(id, this);
    }

    // EntityList management

    public EntityList getEntityList(Object listId) {
        return entityLists.get(listId);
    }

    public EntityList getOrCreateEntityList(Object listId) {
        EntityList entityList = getEntityList(listId);
        if (entityList == null)
            entityLists.put(listId, entityList = new EntityList(listId, this));
        return entityList;
    }

    // Expression

    public Object evaluateEntityExpression(Entity entity, Expression expression) {
        return expression.evaluate(entity, entityDataWriter);
    }

    // String

    public String getEntityClassesCountReport() {
        Map<Object, Integer> classesCount = new HashMap<>();
        for (EntityId id : entities.keySet()) {
            Integer count = classesCount.get(id.getDomainClassId());
            classesCount.put(id.getDomainClassId(), count == null ? 1 : count + 1);
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<Object, Integer> entry : classesCount.entrySet()) {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(entry.getValue()).append(' ').append(entry.getKey());
        }
        return sb.toString();
    }
}
