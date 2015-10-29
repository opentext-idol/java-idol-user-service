/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user.dto;

import com.autonomy.aci.client.annotations.IdolDocument;

/**
 * Class for reading a user from a UserReadUserListDetails response. This class is identical to {@link User}, but reads
 * responses starting from a different tag.
 */
@IdolDocument("autn:user")
public class UserReadUserListDetailsUser extends User {
}
