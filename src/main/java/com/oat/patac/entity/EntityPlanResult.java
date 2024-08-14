package com.oat.patac.entity;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class EntityPlanResult {

    /**
     *功能块的ID
     */
    private Integer functionId;
    //"Successful" or "Unsuccessful"
    private boolean planFailed = false;


}
