/**
 * Copyright (c) 2014-2016 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.voice;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test SpeechRecognitionEvent event
 *
 * @author Kelly Davis - Initial contribution and API
 */
public class SpeechRecognitionEventTest {
    /**
     * Test SpeechRecognitionEvent(String, float) constructor
     */
    @Test
    public void testConstructor() {
        SpeechRecognitionEvent sRE = new SpeechRecognitionEvent("Message", 0.5);
        Assert.assertNotNull("SpeechRecognitionEvent(String, float) constructor failed", sRE);
    }

    /**
     * Test SpeechRecognitionEvent.getTranscript() method
     */
    @Test
    public void getTranscriptTest() {
        SpeechRecognitionEvent sRE = new SpeechRecognitionEvent("Message", 0.5);
        Assert.assertEquals("SpeechRecognitionEvent.getTranscript() method failed", "Message", sRE.getTranscript());
    }

    /**
     * Test SpeechRecognitionEvent.getConfidence() method
     */
    @Test
    public void getConfidenceTest() {
        SpeechRecognitionEvent sRE = new SpeechRecognitionEvent("Message", 0.5);
        Assert.assertEquals("SpeechRecognitionEvent.getConfidence() method failed", 0.5, sRE.getConfidence());
    }
}