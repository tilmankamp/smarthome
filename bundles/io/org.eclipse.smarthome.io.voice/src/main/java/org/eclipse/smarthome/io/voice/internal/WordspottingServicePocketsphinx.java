/**
 * Copyright (c) 2014-2016 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.voice.internal;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.smarthome.io.audio.AudioFormat;
import org.eclipse.smarthome.io.audio.AudioSource;
import org.eclipse.smarthome.io.voice.STTException;
import org.eclipse.smarthome.io.voice.STTListener;
import org.eclipse.smarthome.io.voice.STTService;
import org.eclipse.smarthome.io.voice.STTServiceHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Segment;

/**
 * This is a Wordspotting implementation using Pocketsphinx.
 *
 * @author Andre Natal - Initial contribution and API
 *
 */
public class WordspottingServicePocketsphinx implements STTService {

    static {
        System.loadLibrary("pocketsphinx_jni");
    }

    private static final Logger logger = LoggerFactory.getLogger(WordspottingServicePocketsphinx.class);

    /**
     * Set of supported locales
     */
    private final HashSet<Locale> locales = initLocales();

    /**
     * Set of supported audio formats
     */
    private final HashSet<AudioFormat> audioFormats = initAudioFormats();

    public WordspottingServicePocketsphinx() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Locale> getSupportedLocales() {
        return this.locales;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<AudioFormat> getSupportedFormats() {
        return this.audioFormats;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public STTServiceHandle recognize(STTListener sttListener, AudioSource audioSource, Locale locale,
            Set<String> grammars) throws STTException {

        /*
         * // Validate arguments
         * if (null == sttListener) {
         * throw new IllegalArgumentException("The passed STTListener is null");
         * }
         * if (null == audioSource) {
         * throw new IllegalArgumentException("The passed AudioSource is null");
         * }
         * boolean isAudioFormatValid = false;
         * AudioFormat audioFormat = audioSource.getFormat();
         * for (AudioFormat currentAudioFormat : this.audioFormats) {
         * if (currentAudioFormat.isCompatible(audioFormat)) {
         * isAudioFormatValid = true;
         * break;
         * }
         * }
         * if (!isAudioFormatValid) {
         * throw new IllegalArgumentException("The passed AudioSource's AudioFormat is unsupported");
         * }
         * if (null == audioFormat.getBitRate()) {
         * throw new IllegalArgumentException("The passed AudioSource's AudioFormat's bit rate is not set");
         * }
         * if (!this.locales.contains(locale)) {
         * throw new IllegalArgumentException("The passed Locale is unsupported");
         * }
         * // Note: Currently Kaldi doesn't use grammars. Thus grammars isn't validated
         *
         * // Setup WsDuplexRecognitionSession
         * WsDuplexRecognitionSession recognitionSession;
         * try {
         * recognitionSession = new WsDuplexRecognitionSession(kaldiWebSocketURL);
         * } catch (IOException e) {
         * throw new STTException("Error connected to the server", e);
         * } catch (URISyntaxException e) {
         * throw new STTException("Invalid WebSocket URL", e);
         * }
         * // One need not call recognitionSession.setContentType(...) [See http://bit.ly/1TGvQzA]
         * recognitionSession.addRecognitionEventListener(new RecognitionEventListenerKaldi(sttListener));
         *
         * // Start recognition
         * STTServiceKaldiRunnable sttServiceKaldiRunnable = new STTServiceKaldiRunnable(recognitionSession,
         * sttListener,
         * audioSource);
         * Thread thread = new Thread(sttServiceKaldiRunnable);
         * thread.start();
         *
         * // Return STTServiceHandleKaldi
         * return new STTServiceHandleKaldi(sttServiceKaldiRunnable);
         */

        try {
            Config c = Decoder.defaultConfig();
            c.setString("-hmm", "../../../model/en-us/en-us");
            c.setString("-lm", "../../../model/en-us/en-us.lm.bin");
            c.setString("-dict", "../../../model/en-us/cmudict-en-us.dict");
            Decoder d = new Decoder(c);

            FileInputStream ais = new FileInputStream(new File("../../../test/data/goforward.raw"));

            d.startUtt();
            d.setRawdataSize(300000);
            byte[] b = new byte[4096];
            int nbytes;
            while ((nbytes = ais.read(b)) >= 0) {
                ByteBuffer bb = ByteBuffer.wrap(b, 0, nbytes);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                short[] s = new short[nbytes / 2];
                bb.asShortBuffer().get(s);
                d.processRaw(s, nbytes / 2, false, false);
            }
            d.endUtt();
            System.out.println(d.hyp().getHypstr());

            short[] data = d.getRawdata();
            System.out.println("Data size: " + data.length);
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("test.raw")));
            for (int i = 0; i < data.length; i++) {
                dos.writeShort(data[i]);
            }
            dos.close();

            for (Segment seg : d.seg()) {
                System.out.println(seg.getWord());
            }
        } catch (Exception exc) {

        }

        return null;
    }

    /**
     * Initializes this.locales
     *
     * @return The locales of this instance
     */
    private final HashSet<Locale> initLocales() {
        HashSet<Locale> locales = new HashSet<Locale>();
        return locales;
    }

    /**
     * Initializes this.audioFormats
     *
     * @return The audio formats of this instance
     */
    private final HashSet<AudioFormat> initAudioFormats() {
        HashSet<AudioFormat> audioFormats = new HashSet<AudioFormat>();
        return audioFormats;
    }

}
