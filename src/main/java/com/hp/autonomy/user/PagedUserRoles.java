/*
 * Copyright (c) 2018, Micro Focus International plc.
 */

package com.hp.autonomy.user;

import java.util.List;
import lombok.Data;

@Data
public class PagedUserRoles {
    private final int totalUsers;
    private final List<UserRoles> users;
}
