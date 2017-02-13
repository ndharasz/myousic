package com.myousic.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.spotify.sdk.android.player.AudioController;

/**
 * Created by ndharasz on 2/13/2017.
 */

public class CustomAudioController implements AudioController {
    private AudioTrack track = null;

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public int onAudioDataDelivered(short[] samples, int numSamples, int rate, int channels) {
        int written;
        if (track == null) {
            int channel = (channels == 0) ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
            int buffSize = AudioTrack.getMinBufferSize(rate, channel,
                    AudioFormat.ENCODING_PCM_16BIT);
            track = new AudioTrack(AudioManager.STREAM_MUSIC, rate, channel,
                    AudioFormat.ENCODING_PCM_16BIT, buffSize, AudioTrack.MODE_STREAM);
            written = track.write(samples, 0, numSamples);
            track.play();
        } else {
            written = track.write(samples, 0, numSamples);
        }
        return written;
    }

    @Override
    public void onAudioFlush() {
        if(track != null)
            track.flush();
    }

    @Override
    public void onAudioPaused() {
        if(track != null)
            track.pause();
    }

    @Override
    public void onAudioResumed(){
        if(track != null)
            track.play();
    }
}
