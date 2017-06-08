package mongoose.entities;

import mongoose.entities.markers.EntityHasLabel;
import mongoose.entities.markers.EntityHasName;
import naga.framework.orm.entity.Entity;

/**
 * @author Bruno Salmon
 */
public interface Method extends Entity,
        EntityHasName,
        EntityHasLabel {

}