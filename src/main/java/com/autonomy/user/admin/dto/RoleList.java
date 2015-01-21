package com.autonomy.user.admin.dto;

import com.autonomy.aci.client.annotations.IdolDocument;
import com.autonomy.aci.client.annotations.IdolField;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@IdolDocument(value = "responsedata")
public class RoleList {

    @Getter
    private final List<String> roles = new ArrayList<>();

    @IdolField("autn:role")
    void setRolesProducts(final String role){
        this.roles.add(role);
    }
}