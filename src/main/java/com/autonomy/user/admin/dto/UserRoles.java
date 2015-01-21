package com.autonomy.user.admin.dto;

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