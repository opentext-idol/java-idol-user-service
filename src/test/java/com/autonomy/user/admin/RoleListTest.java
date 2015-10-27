/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.autonomy.user.admin;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactoryImpl;
import com.autonomy.aci.client.services.StAXProcessor;
import com.autonomy.user.admin.dto.RoleList;
import com.hp.autonomy.test.xml.XmlTestUtils;
import org.junit.Before;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class RoleListTest {

    private XMLStreamReader roleListXMLReader;
    private IdolAnnotationsProcessorFactory processorFactory;

    @Before
    public void setUp() throws XMLStreamException {
        this.roleListXMLReader = XmlTestUtils.getResourceAsXMLStreamReader("/roleList.xml");
        this.processorFactory = new IdolAnnotationsProcessorFactoryImpl();
    }

    @Test
    public void licenseInfoParseTest(){
        final StAXProcessor<List<RoleList>> listProcessor = processorFactory.listProcessorForClass(RoleList.class);
        final List<RoleList> rolesList = listProcessor.process(this.roleListXMLReader);

        assertThat(rolesList, hasSize(1));
        final RoleList roleList = rolesList.get(0);

        assertThat(roleList.getRoles(), hasSize(2));
        assertThat(roleList.getRoles().get(1), is("everyone"));
    }
}
