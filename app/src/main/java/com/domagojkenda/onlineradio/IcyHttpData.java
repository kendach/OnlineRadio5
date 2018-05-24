package com.domagojkenda.onlineradio;

import android.util.Log;
import android.widget.TextView;

import saschpe.exoplayer2.ext.icy.IcyHttpDataSource;
import saschpe.exoplayer2.ext.icy.IcyHttpDataSourceFactory;

public class IcyHttpData {
    private IcyHttpDataSource.IcyHeaders icyHeaders;
    private IcyHttpDataSource.IcyMetadata icyMetadata;
    private TextView textView;

    public IcyHttpData(TextView textView) {
        this.textView = textView;
    }

    public void iceHeader(IcyHttpDataSource.IcyHeaders icyHeaders)
    {
        this.icyHeaders = icyHeaders;
        Log.d("XXX", "onIcyHeaders: %s".format(icyHeaders.toString()));
        System.out.println("onIcyHeaders: %s".format(icyHeaders.toString()));
        setTextViewText();
    }

    public void icyMetadata(IcyHttpDataSource.IcyMetadata icyMetadata)
    {
        this.icyMetadata = icyMetadata;;
        System.out.println("onIcyMetaData: %s".format(icyMetadata.toString()));
        setTextViewText();
    }

    private void setTextViewText()
    {
        if (textView == null) return;
        textView.setText("");
        if (icyMetadata != null)
        {
            textView.setText(icyMetadata.getStreamTitle());
            return;
        }
        if (icyHeaders != null)
        {
            textView.setText(icyHeaders.getName());
            return;
        }
    }
}
