package com.xzhiwei.demo.base.coder.marshalling;

import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;
import org.jboss.marshalling.serial.SerialMarshallerFactory;

import java.io.IOException;

public class MarshallingCodecFactory {

    private final static SerialMarshallerFactory factory = new SerialMarshallerFactory();

    private static MarshallingConfiguration configuration = new MarshallingConfiguration();

    static {
        configuration.setVersion(5);
    }

    private static Marshaller marshaller;

    private static Unmarshaller unmarshaller;

    /*
    此处是否能用单例
     */
    public static Marshaller buildMarshalling() throws IOException {
        return factory.createMarshaller(configuration);
    }

    /*
    此处是否能用单例
     */
    public static Unmarshaller buildUnmarshalling() throws IOException {
        return factory.createUnmarshaller(configuration);
    }
}
