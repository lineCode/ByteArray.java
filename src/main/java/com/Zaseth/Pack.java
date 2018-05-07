package com.Zaseth;

import java.util.Arrays;

class Pack {
    private byte[] data;
    private int position;
    
    public Pack(byte[] data, int position) {
        this.data = data;
        this.position = position;
    }
    
    public void packInt(int value) {
        int shift = (value & ~0x7F);
        if (shift != 0) {
            shift = 31 - Integer.numberOfLeadingZeros(value);
            shift -= shift % 7;
            while (shift != 0) {
                this.data[this.position++] = (byte) ((value >>> shift) & 0x7F);
                shift -= 7;
            }
        }
        this.data[this.position++] = (byte) ((value & 0x7F)| 0x80);
    }
    
    public void packIntBigger(int value) {
        int shift = 31 - Integer.numberOfLeadingZeros(value);
        shift -= shift % 7;
        while(shift != 0) {
            this.data[this.position++] = (byte) ((value >>> shift) & 0x7F);
            shift -= 7;
        }
        this.data[this.position++] = (byte) ((value & 0x7F) | 0x80);
    }
    
    public void packLong(long value) {
        int shift = 63 - Long.numberOfLeadingZeros(value);
        shift -= shift % 7;
        while(shift != 0) {
            this.data[this.position++] = (byte) ((value >>> shift) & 0x7F);
            shift -= 7;
        }
        this.data[this.position++] = (byte) ((value & 0x7F) | 0x80);
    }
    
    public void packIntArray(int[] array, int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; i++) {
            int value = array[i];
            int shift = (value & ~0x7F);
            shift = 31 - Integer.numberOfLeadingZeros(value);
            shift -= shift % 7;
            while (shift != 0) {
                this.data[this.position++] = (byte) ((value >>> shift) & 0x7F);
                shift -= 7;
            }
            this.data[this.position++] = (byte) ((value & 0x7F)| 0x80);
        }
    }
    
    public void packIntBiggerArray(int[] array, int fromIndex, int toIndex) {
        for (int i = fromIndex; i < toIndex; i++) {
            int value = array[i];
            int shift = 31 - Integer.numberOfLeadingZeros(value);
            shift -= shift % 7;
            while(shift != 0) {
                this.data[this.position++] = (byte) ((value >>> shift) & 0x7F);
                shift -= 7;
            }
            this.data[this.position++] = (byte) ((value & 0x7F) | 0x80);
        }
    }
    
    public void packLongArray(long[] array, int fromIndex, int toIndex) {
        for(int i = fromIndex; i < toIndex; i++) {
            long value = array[i];
            int shift = 63 - Long.numberOfLeadingZeros(value);
            shift -= shift % 7;
            while(shift != 0) {
                this.data[this.position++] = (byte) ((value >>> shift) & 0x7F);
                shift-=7;
            }
            this.data[this.position++] = (byte) ((value & 0x7F) | 0x80);
        }
    }
    
    public static void main(String[] args) {
        Pack test = new Pack(new byte[1024], 0);
        test.packInt(55);
        System.out.println(Arrays.toString(test.data));
        System.out.println(test.position);
    }
}