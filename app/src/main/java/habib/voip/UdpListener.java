package habib.voip;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import habib.voip.sound.FrequencyBand;
import habib.voip.sound.SpeexDecoder;

public class UdpListener extends Thread {

	@Override
	public void run() {
		try {
            SpeexDecoder decoder = new SpeexDecoder(FrequencyBand.WIDE_BAND);
			int minBufferSize = AudioTrack.getMinBufferSize(
					MainActivity.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT);

			AudioTrack track = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
					MainActivity.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
					AudioFormat.ENCODING_PCM_16BIT, minBufferSize,
					AudioTrack.MODE_STREAM);
			track.play();
			DatagramSocket socket = Manager.getManager().getUdpSocket();
			while (Values.running) {
				byte[] capturedData = new byte[Values.BUFFER_SIZE];
				DatagramPacket packet = new DatagramPacket(capturedData, capturedData.length);
                socket.receive(packet);
                Log.i(Values.LogTag, "Length of packet :"+ packet.getLength());
                byte[] data = packet.getData();
                short[] decode = decoder.decode(data);
                track.write(decode, 0, decode.length);
			}
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
