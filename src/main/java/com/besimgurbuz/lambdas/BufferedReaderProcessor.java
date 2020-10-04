package com.besimgurbuz.lambdas;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Besim Gurbuz
 */

@FunctionalInterface
public interface BufferedReaderProcessor {

    String process(BufferedReader b) throws IOException;
}
