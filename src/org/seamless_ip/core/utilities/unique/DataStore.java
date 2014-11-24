/*
 * seamframe: DataStore.java
 * ==============================================================================
 * This work has been carried out as part of the SEAMLESS Integrated Framework
 * project, EU 6th Framework Programme, contract no. 010036-2 and/or as part
 * of the SEAMLESS association.
 *
 * Copyright (c) 2009 The SEAMLESS Association.
 *
 * For more information: http://www.seamlessassociation.org;
 * email: info@seamless-if.org
 *
 * The contents of this file is subject to the SEAMLESS Association License for
 * software infrastructure and model components Version 1.1 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at http://www.seamlessassociation.org/License.htm
 *
 * Software distributed under the License is distributed on an "AS IS"  basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific governing rights and limitations.
 *
 * The Initial Developers of the Original Code are:
 *  - Ioannis Athanasiadis; IDSIA Dalle Molle Institute for Artificial Intelligence
 *  - Sander Janssen; Alterra, Wageningen UR
 *  - Benny Johnsson; Lund University
 *  - Rob Knapen; Alterra, Wageningen UR
 *  - Hongtao Li; IDSIA Dalle Molle Institute for Artificial Intelligence
 *  - Michiel Rop; Alterra, Wageningen UR / ilionX
 *  - Lorenzo Ruinelli; IDSIA Dalle Molle Institute for Artificial Intelligence
 *
 * ================================================================================
 * Contributor(s): N/A
 * ================================================================================
 */

package org.seamless_ip.core.utilities.unique;

import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

//import org.apache.log4j.Logger;

/**
 * This DataOutput extension is used to be able to access the UUID classes
 * fields. It simply stores all the data passed via the write methods (base 16).
 *
 * @author david
 * @author Ioannis N. Athanasiadis
 * @see UUID
 */
final class DataStore implements DataOutput {
    private String hexprefix = "00000000000000000000000000000000";
//    private Logger log = Logger.getLogger("org.seamless_ip.core.utilities.unique.DataStore");

    private StringBuffer buffer = new StringBuffer();

    public void write(int b) throws IOException {
        if (b < 0) buffer.append(1).append(-b);
        else buffer.append(0).append(b);
    }

    public void write(byte[] b) throws IOException {
        for (byte bt : b) {
            if (bt < 0) buffer.append(1).append(-bt);
            else buffer.append(0).append(b);
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = off; i < off + len; ++i) {
            byte bt = b[i];
            if (bt < 0) buffer.append(1).append(-bt);
            else buffer.append(0).append(b);
        }
    }

    public void writeBoolean(boolean v) throws IOException {
        buffer.append(v);
    }

    public void writeByte(int b) throws IOException {
        if (b < 0) buffer.append(1).append(-b);
        else buffer.append(0).append(b);
    }

    public void writeShort(int b) throws IOException { // 7 (to include the minus)
        String val = Integer.toHexString(b);
        val = hexprefix.substring(0, 8 - val.length()) + val;
        buffer.append(val.substring(2));
    }

    public void writeChar(int b) throws IOException {
        String val = Integer.toHexString(b);
        buffer.append(val.substring(4));
    }

    public void writeInt(int b) throws IOException {
        String val = Integer.toHexString(b);
        val = hexprefix.substring(0, 8 - val.length()) + val;
        buffer.append(val);
    }

    public void writeLong(long b) throws IOException {
        String val = Long.toHexString(b);
        val = hexprefix.substring(0, 16 - val.length()) + val;
        buffer.append(val);
    }

    public void writeFloat(float b) throws IOException {
        if (b < 0) buffer.append(1).append(-b);
        else buffer.append(0).append(b);
    }

    public void writeDouble(double b) throws IOException {
        if (b < 0) buffer.append(1).append(-b);
        else buffer.append(0).append(b);
    }

    public void writeBytes(String s) throws IOException {
        buffer.append(s);
    }

    public void writeChars(String s) throws IOException {
        buffer.append(s);
    }

    public void writeUTF(String str) throws IOException {
        buffer.append(str);
    }

    public String toString() {
        return buffer.toString().toUpperCase();
    }
}