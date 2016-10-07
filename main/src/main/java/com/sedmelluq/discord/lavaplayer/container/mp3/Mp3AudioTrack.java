package com.sedmelluq.discord.lavaplayer.container.mp3;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.BaseAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioTrackExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Audio track that handles an MP3 stream
 */
public class Mp3AudioTrack extends BaseAudioTrack {
  private static final Logger log = LoggerFactory.getLogger(Mp3AudioTrack.class);

  private final SeekableInputStream inputStream;

  /**
   * @param manager Audio player manager which created the track
   * @param executor Track executor
   * @param trackInfo Track info
   * @param inputStream Input stream for the MP3 file
   */
  public Mp3AudioTrack(AudioPlayerManager manager, AudioTrackExecutor executor, AudioTrackInfo trackInfo, SeekableInputStream inputStream) {
    super(manager, executor, trackInfo);

    this.inputStream = inputStream;
  }

  @Override
  public void process(AtomicInteger volumeLevel) throws Exception {
    Mp3StreamingFile file = new Mp3StreamingFile(manager, inputStream, executor.getFrameConsumer(), volumeLevel);

    try {
      file.parseHeaders();

      log.debug("Starting to play MP3 track {}", getIdentifier());
      executor.executeProcessingLoop(file::provideFrames, file::seekToTimecode);
    } finally {
      file.close();
    }
  }
}