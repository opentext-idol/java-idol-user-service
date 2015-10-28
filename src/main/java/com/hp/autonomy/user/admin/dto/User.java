/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user.admin.dto;

import com.autonomy.aci.client.annotations.IdolDocument;
import com.autonomy.aci.client.annotations.IdolField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IdolDocument("responsedata")
public class User {

    private long uid;
    private String name;
    private boolean locked;
    private long lockedLastTime;
    private int maxAgents;
    private int numAgents;
    private long lastLoggedIn;
    private int numFields;

    @IdolField("autn:uid")
    public void setUid(final long uid) {
        this.uid = uid;
    }

    @IdolField("autn:username")
    public void setName(final String name) {
        this.name = name;
    }

    @IdolField("autn:locked")
    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

    @IdolField("autn:maxagents")
    public void setMaxAgents(final int maxAgents) {
        this.maxAgents = maxAgents;
    }

    @IdolField("autn:numagents")
    public void setNumAgents(final int numAgents) {
        this.numAgents = numAgents;
    }

    @IdolField("autn:lastloggedin")
    public void setLastLoggedIn(final long lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    @IdolField("autn:lockedlasttime")
    public void setLockedLastTime(final long lockedLastTime) {
        this.lockedLastTime = lockedLastTime;
    }

    @IdolField("autn:numfields")
    public void setNumFields(final int numFields) {
        this.numFields = numFields;
    }
}