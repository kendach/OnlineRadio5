package com.domagojkenda.onlineradio;

import java.util.Objects;

public interface MyConsumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t);

}

