/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoles implements Serializable {

    private static final long serialVersionUID = 7516101375412437377L;
    private String username;
    private long uid;
    private List<String> roles = new ArrayList<>();

    public UserRoles(final String username){
        this.username = username;
    }
}