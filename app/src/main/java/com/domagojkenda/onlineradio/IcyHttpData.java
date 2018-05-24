package com.domagojkenda.onlineradio;

import android.util.Log;

import saschpe.exoplayer2.ext.icy.IcyHttpDataSource;

public class IcyHttpData {
    private final MyConsumer<String> consumer;
    private IcyHttpDataSource.IcyHeaders icyHeaders;
    private IcyHttpDataSource.IcyMetadata icyMetadata;
    private String streamTitle = "";
    private final StringBuilder streamTitles;

    public IcyHttpData(MyConsumer<String> consumer) {
        this.consumer = consumer;
        streamTitles = new StringBuilder();
    }


    public void iceHeader(IcyHttpDataSource.IcyHeaders icyHeaders)
    {
        this.icyHeaders = icyHeaders;
        Log.d("XXX", String.format(icyHeaders.toString()));
        acceptStremTitle();
    }


    public void icyMetadata(IcyHttpDataSource.IcyMetadata icyMetadata)
    {
        this.icyMetadata = icyMetadata;
        Log.d("XXX", String.format(icyMetadata.toString()));
        acceptStremTitle();
    }


    private void acceptStremTitle()
    {
        setStreamTitle();
        if (consumer != null)
        {
            consumer.accept(streamTitle);
        }
    }

    private void setStreamTitle()
    {
        if (icyMetadata != null)
        {
            streamTitle = icyMetadata.getStreamTitle();
            insertStreamTitle(streamTitle);
            icyMetadata = null;
            return;
        }
        if (icyHeaders != null)
        {
            streamTitle = icyHeaders.getName();
        }
    }


    private void insertStreamTitle(String s)
    {
        if (streamTitles.length() > 0 ) streamTitles.insert(0, '\n');
        streamTitles.insert(0, s);
        Log.d("StreamTitles", String.format(streamTitles()));
    }


    public String streamTitles()
    {
        return streamTitles.toString();
    }
}
