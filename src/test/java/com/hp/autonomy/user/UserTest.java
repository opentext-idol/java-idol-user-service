/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactoryImpl;
import com.autonomy.aci.client.services.StAXProcessor;
import com.hp.autonomy.test.xml.XmlTestUtils;
import com.hp.autonomy.user.dto.User;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UserTest {

    private XMLStreamReader xmlReader;
    private IdolAnnotationsProcessorFactory processorFactory;

    @Before
    public void setUp() throws XMLStreamException {
        this.xmlReader = XmlTestUtils.getResourceAsXMLStreamReader("/user.xml");
        this.processorFactory = new IdolAnnotationsProcessorFactoryImpl();
    }

    @Test
    public void userParseTest(){
        final StAXProcessor<List<User>> listProcessor = processorFactory.listProcessorForClass(User.class);
        final List<User> users = listProcessor.process(this.xmlReader);

        assertThat(users, hasSize(1));
        final User user = users.get(0);

        assertThat(user.getUid(), is(8L));
        assertThat(user.getName(), is("baz"));
        assertFalse(user.isLocked());
        assertThat(user.getLockedLastTime(), is(nullValue()));
        assertThat(user.getMaxAgents(), is(15));
        assertThat(user.getNumAgents(), is(0));
        assertThat(user.getLastLoggedIn(), is(new DateTime(1379929725L * 1000L)));
        assertThat(user.getNumFields(), is(0));
    }
}
