/*
 * Copyright (c) 2010-2025 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.zwave.internal.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openhab.binding.zwave.handler.ZWaveThingChannel;
import org.openhab.binding.zwave.handler.ZWaveThingChannel.DataType;
import org.openhab.binding.zwave.internal.protocol.ZWaveController;
import org.openhab.binding.zwave.internal.protocol.ZWaveEndpoint;
import org.openhab.binding.zwave.internal.protocol.ZWaveNode;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveCommandClass.CommandClass;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveMeterTblMonitorCommandClass;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveMeterTblMonitorCommandClass.MeterTblMonitorScale;
import org.openhab.binding.zwave.internal.protocol.commandclass.ZWaveMeterTblMonitorCommandClass.MeterTblMonitorType;
import org.openhab.binding.zwave.internal.protocol.event.ZWaveCommandClassValueEvent;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.openhab.core.types.State;

/**
 *
 * @author Chris Jackson
 *
 */
public class ZWaveMeterTblMonitorConverterTest {
    final ChannelUID uid = new ChannelUID("zwave:node:bridge:channel");
    final ChannelTypeUID typeUid = new ChannelTypeUID("zwave:channel");

    private ZWaveThingChannel createChannel(String type) {
        Map<String, String> args = new HashMap<String, String>();
        args.put("type", type);
        return new ZWaveThingChannel(null, typeUid, uid, DataType.DecimalType,
                CommandClass.COMMAND_CLASS_METER_TBL_MONITOR.toString(), 0, args);
    }

    private ZWaveCommandClassValueEvent createEvent(MeterTblMonitorType type, MeterTblMonitorScale scale,
            BigDecimal value) {
        ZWaveController controller = Mockito.mock(ZWaveController.class);
        ZWaveNode node = Mockito.mock(ZWaveNode.class);
        ZWaveEndpoint endpoint = Mockito.mock(ZWaveEndpoint.class);
        ZWaveMeterTblMonitorCommandClass cls = new ZWaveMeterTblMonitorCommandClass(node, controller, endpoint);

        return cls.new ZWaveMeterTblMonitorValueEvent(1, 0, type, scale, value);
    }

    @Test
    public void EventElectric() {
        ZWaveMeterTblMonitorConverter converter = new ZWaveMeterTblMonitorConverter(null);
        ZWaveThingChannel channel = createChannel(MeterTblMonitorScale.TE_KWh.toString());
        BigDecimal value = new BigDecimal("3.3");

        ZWaveCommandClassValueEvent event = createEvent(
                ZWaveMeterTblMonitorCommandClass.MeterTblMonitorType.TWIN_ELECTRIC,
                ZWaveMeterTblMonitorCommandClass.MeterTblMonitorScale.TE_KWh, value);

        State state = converter.handleEvent(channel, event);

        assertEquals(state.getClass(), DecimalType.class);
        assertEquals(((DecimalType) state).toBigDecimal(), value);
    }
}
