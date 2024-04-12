package net.xiaoyu233.classkit.tools;

import net.xiaoyu233.classkit.av.AudioProvider;
import net.xiaoyu233.classkit.av.FrameStreamer;
import net.xiaoyu233.classkit.av.VideoProvider;
import net.xiaoyu233.classkit.config.StreamingConfig;

import java.net.URL;

public class StreamingTool extends Tool<StreamingConfig>{
    private FrameStreamer streamer;
    @Override
    public String getName() {
        return "Streaming Tool";
    }

    @Override
    public void init(StreamingConfig config) {
        streamer = new FrameStreamer("rtmp:/localhost:" + config.getPort() + "/live",1366,768);
    }

    public <T extends VideoProvider & AudioProvider> void  registerThis(T provider){
        provider.addVideoCallback(streamer);
        provider.addAudioCallback(streamer);
    }

    @Override
    public void reloadConfig(StreamingConfig config) {

    }

    @Override
    public void tick() {

    }
}
