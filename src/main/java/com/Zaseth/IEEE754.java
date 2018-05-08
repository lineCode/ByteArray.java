package com.Zaseth;

import static java.lang.Float.NaN;

import java.util.Arrays;

class IEEE754 {
    public IEEE754() {
    
    }
    
    public float readFloat(byte[] buffer, int offset, boolean isLE, int mLen, int nBytes) {
        int e;
        int m;
        int eLen = (nBytes * 8) - mLen - 1;
        int eMax = (1 << eLen) - 1;
        int eBias = eMax >> 1;
        int nBits = -7;
        int i = isLE ? (nBits - 1) : 0;
        int d = isLE ? -1 : 1;
        int s = buffer[offset + i];
        boolean xd;
        i += d;
        e = s & ((1 << (-nBits)) - 1);
        s >>= (-nBits);
        nBits += eLen;
        for (; nBits > 0; e = (e * 256) + buffer[offset + i], i += d, nBits -= 8) {}
        m = e & ((1 << (-nBits)) - 1);
        e >>= (-nBits);
        nBits += mLen;
        for (; nBits > 0; m = (m * 256) + buffer[offset + i], i += d, nBits -= 8) {}
        if (s == 1) {
            xd = true;
        } else {
            xd = false;
        }
        if (e == 0) {
            e = 1 - eBias;
        } else if (e == eMax) {
            return m < 0 ? NaN : (float) ((xd ? -1 : 1) * Float.POSITIVE_INFINITY);
        } else {
            m = (int) (m + Math.pow(2, mLen));
            e = e - eBias;
        }
        return (float) ((xd ? -1 : 1) * m * Math.pow(2, e - mLen));
    }
    
    public double readDouble(byte[] buffer, int offset, boolean isLE, int mLen, int nBytes) {
        int e;
        double m;
        int eLen = (nBytes * 8) - mLen - 1;
        int eMax = (1 << eLen) - 1;
        int eBias = eMax >> 1;
        int nBits = -7;
        int i = isLE ? (nBits - 1) : 0;
        int d = isLE ? -1 : 1;
        int s = buffer[offset + i];
        boolean xd;
        i += d;
        e = s & ((1 << (-nBits)) - 1);
        s >>= (-nBits);
        nBits += eLen;
        for (; nBits > 0; e = (e * 256) + buffer[offset + i], i += d, nBits -= 8) {}
        m = e & ((1 << (-nBits)) - 1);
        e >>= (-nBits);
        nBits += mLen;
        for (; nBits > 0; m = (m * 256) + buffer[offset + i], i += d, nBits -= 8) {}
        if (s == 1) {
            xd = true;
        } else {
            xd = false;
        }
        if (e == 0) {
            e = 1 - eBias;
        } else if (e == eMax) {
            return m < 0 ? NaN : ((xd ? -1 : 1) * Float.POSITIVE_INFINITY);
        } else {
            m = (m + Math.pow(2, mLen));
            e = e - eBias;
        }
        return (xd ? -1 : 1) * m * Math.pow(2, e - mLen);
    }
    
    public void write(byte[] buffer, float value, int offset, boolean isLE, int mLen, int nBytes) {
        double e;
        double m;
        double c;
        int eLen = (nBytes * 8) - mLen - 1;
        int eMax = (1 << eLen) - 1;
        int eBias = eMax >> 1;
        int rt = mLen == 23 ? (int) (Math.pow(2, -24) - Math.pow(2, -77)) : 0;
        int i = isLE ? 0 : (nBytes - 1);
        int d = isLE ? 1 : -1;
        int s = value < 0 || (value == 0 && 1 / value < 0) ? 1 : 0;
        value = Math.abs(value);
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            m = Float.isNaN(value) ? 1 : 0;
            e = eMax;
        } else {
            e = Math.floor(Math.log(value) / Math.log(2)); // Math.LN2
            if (value * (c = Math.pow(2, -e)) < 1) {
                e--;
                c *= 2;
            }
            if (e + eBias >= 1) {
                value += rt / c;
            } else {
                value += rt * Math.pow(2, 1 - eBias);
            }
            if (value * c >= 2) {
                e++;
                c /= 2;
            }
            if (e + eBias >= eMax) {
                m = 0;
                e = eMax;
            } else if (e + eBias >= 1) {
                m = ((value * c) - 1) * Math.pow(2, mLen);
                e = e + eBias;
            } else {
                m = value * Math.pow(2, eBias - 1) * Math.pow(2, mLen);
                e = 0;
            }
        }
        for (; mLen >= 8; buffer[offset + i] = (byte) ((byte) m & 0xff), i += d, m /= 256, mLen -= 8) {}
        e = ((int) e << mLen) | (int) m;
        eLen += mLen;
        for (; eLen > 0; buffer[offset + i] = (byte) ((byte) e & 0xff), i += d, e /= 256, eLen -= 8) {}
        buffer[offset + i - d] |= s * 128;
    }
    
    public void write(byte[] buffer, double value, int offset, boolean isLE, int mLen, int nBytes) {
        double e;
        double m;
        double c;
        int eLen = (nBytes * 8) - mLen - 1;
        int eMax = (1 << eLen) - 1;
        int eBias = eMax >> 1;
        int rt = mLen == 23 ? (int) (Math.pow(2, -24) - Math.pow(2, -77)) : 0;
        int i = isLE ? 0 : (nBytes - 1);
        int d = isLE ? 1 : -1;
        int s = value < 0 || (value == 0 && 1 / value < 0) ? 1 : 0;
        value = Math.abs(value);
        if (value == NaN || value == Float.POSITIVE_INFINITY) {
            m = Double.isNaN(value) ? 1 : 0;
            e = eMax;
        } else {
            e = Math.floor(Math.log(value) / Math.log(2)); // Math.LN2
            if (value * (c = Math.pow(2, -e)) < 1) {
                e--;
                c *= 2;
            }
            if (e + eBias >= 1) {
                value += rt / c;
            } else {
                value += rt * Math.pow(2, 1 - eBias);
            }
            if (value * c >= 2) {
                e++;
                c /= 2;
            }
            if (e + eBias >= eMax) {
                m = 0;
                e = eMax;
            } else if (e + eBias >= 1) {
                m = ((value * c) - 1) * Math.pow(2, mLen);
                e = e + eBias;
            } else {
                m = value * Math.pow(2, eBias - 1) * Math.pow(2, mLen);
                e = 0;
            }
        }
        for (; mLen >= 8; buffer[offset + i] = (byte) ((byte) m & 0xff), i += d, m /= 256, mLen -= 8) {}
        e = ((int) e << mLen) | (int) m;
        eLen += mLen;
        for (; eLen > 0; buffer[offset + i] = (byte) ((byte) e & 0xff), i += d, e /= 256, eLen -= 8) {}
        buffer[offset + i - d] |= s * 128;
    }
    
    public static void main(String[] args) {
        byte[] data = new byte[1024];
        IEEE754 test = new IEEE754();
        test.write(data, 56, 0, false, 23, 4);
        System.out.println(Arrays.toString(data));
    }
}