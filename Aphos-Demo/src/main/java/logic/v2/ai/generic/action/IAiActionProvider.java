package logic.v2.ai.generic.action;

import logic.v2.entity.UnitRef;

/**
 * Created by Alexander on 1/22/2023
 */
public interface IAiActionProvider {
     String getActionName(UnitRef ref);
}
