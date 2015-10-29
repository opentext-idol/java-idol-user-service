/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user.dto;

import com.autonomy.aci.client.annotations.IdolDocument;
import com.autonomy.aci.client.annotations.IdolField;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for reading a user list out of Community
 */
@IdolDocument("responsedata")
@AllArgsConstructor
public class UserList {

    /**
     * @return The list of user names
     */
    @Getter
    private final List<String> userNames;

    public UserList() {
        this(new ArrayList<String>());
    }

    @IdolField("autn:user")
    public void addUserName(final String userName){
        userNames.add(userName);
    }
}
