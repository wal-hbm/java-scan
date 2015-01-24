/*
 * Java Scan, a library for scanning and configuring HBM devices.
 *
 * The MIT License (MIT)
 *
 * Copyright (C) Stephan Gatzka
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hbm.devices.scan.filter;

import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hbm.devices.scan.ScanConstants;
import com.hbm.devices.scan.messages.Announce;
import com.hbm.devices.scan.messages.CommunicationPath;
import com.hbm.devices.scan.messages.MissingDataException;

/**
 * This class filters {@link CommunicationPath} objects with according to a {@link Matcher} object.
 * <p>
 * The class reads {@link CommunicationPath} objects and notifies them if
 * {@link Matcher#match(Announce)} method returns true.
 * 
 * @since 1.0
 */
public class Filter extends Observable implements Observer {

    private Matcher matcher;
    private static final Logger LOGGER = Logger.getLogger(ScanConstants.LOGGER_NAME);

    public Filter(Matcher m) {
        this.matcher = m;
    }

    public Matcher getMatcher() {
        return this.matcher;
    }

    @Override
    public void update(Observable o, Object arg) {
        final CommunicationPath ap = (CommunicationPath) arg;
        final Announce announce = ap.getAnnounce();
        try {
            if (matcher.match(announce)) {
                setChanged();
                notifyObservers(ap);
            }
        } catch (MissingDataException e) {
            LOGGER.log(Level.INFO, "Some information is missing in announce!", e);
        }
    }
}
