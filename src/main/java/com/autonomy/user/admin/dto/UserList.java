/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.autonomy.user.admin.dto;

import com.autonomy.aci.client.annotations.IdolDocument;
import com.autonomy.aci.client.annotations.IdolField;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@IdolDocument("responsedata")
@AllArgsConstructor
public class UserList {

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
