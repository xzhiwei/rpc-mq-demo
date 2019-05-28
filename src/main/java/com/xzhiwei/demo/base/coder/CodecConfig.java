package com.xzhiwei.demo.base.coder;

public class CodecConfig {

    private String encoderClassName;

    private String decoderClassName;

    public Encoder getEncoder() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Class clz = Class.forName(this.encoderClassName);
        return (Encoder) clz.newInstance();
    }

    public Decoder getDecoder() throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Class clz = Class.forName(this.decoderClassName);
        return (Decoder) clz.newInstance();
    }

    public static Encoder getEncoder(String encoderClassName) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Class clz = Class.forName(encoderClassName);
        return (Encoder) clz.newInstance();
    }

    public static Decoder getDecoder(String decoderClassName) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Class clz = Class.forName(decoderClassName);
        return (Decoder) clz.newInstance();
    }

    public static Encoder getEncoder(Class<? extends Encoder> clz) throws IllegalAccessException, InstantiationException {
        return clz.newInstance();
    }

    public static Decoder getDecoder(Class<? extends Decoder> clz) throws IllegalAccessException, InstantiationException {
        return clz.newInstance();
    }

    public String getEncoderClassName() {
        return encoderClassName;
    }

    public void setEncoderClassName(String encoderClassName) {
        this.encoderClassName = encoderClassName;
    }

    public String getDecoderClassName() {
        return decoderClassName;
    }

    public void setDecoderClassName(String decoderClassName) {
        this.decoderClassName = decoderClassName;
    }
}
