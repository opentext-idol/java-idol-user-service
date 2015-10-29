/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user.dto;

import com.autonomy.aci.client.annotations.IdolDocument;
import com.autonomy.aci.client.annotations.IdolField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Class for reading a user out of community
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdolDocument("responsedata")
public class User {

    /**
     * @return The uid of the user
     */
    private long uid;

    /**
     * @return The name of the user
     */
    private String name;

    /**
     * @return true if the user is locked; false otherwise
     */
    private boolean locked;

    /**
     * @return The time the user was last locked, or null if the user has never been locked
     */
    private DateTime lockedLastTime;

    /**
     * @return The maximum number of agents the user can have
     */
    private int maxAgents;

    /**
     * @return The number of agents the user has
     */
    private int numAgents;

    /**
     * @return The time the user last logged in, or null if the user has never logged in
     */
    private DateTime lastLoggedIn;

    /**
     * @return The number of fields the user has
     */
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
        if (lastLoggedIn != 0) {
            this.lastLoggedIn = new DateTime(lastLoggedIn * 1000);
        }
    }

    @IdolField("autn:lockedlasttime")
    public void setLockedLastTime(final long lockedLastTime) {
        if (lockedLastTime != 0) {
            this.lockedLastTime = new DateTime(lockedLastTime * 1000);
        }
    }

    @IdolField("autn:numfields")
    public void setNumFields(final int numFields) {
        this.numFields = numFields;
    }
}