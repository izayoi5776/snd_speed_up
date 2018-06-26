package wavwspeed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class WaveSpeed {

	public WaveSpeed() {
	}

	public static void main(String[] args) throws InterruptedException {
		playSound();
		Thread.sleep(5000);
	}
	
	public static void playSound() {
	    try {
	    	float sampleRate = 32000;	// base 16000 -> 32000 for 2x speed
	    	int sampleSizeInBits = 16;
	    	int channels = 1;
	    	boolean signed = true;
	    	boolean bigEndian = false;
	    	AudioFormat audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

	        Clip clip = AudioSystem.getClip();
	    	byte[] data = readFile();
	    	byte[] data2 = conv(data);
	    	int offset = 0;
	    	int bufferSize = data2.length;
	    	clip.open(audioFormat, data2, offset, bufferSize);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
	private static byte[] readFile() {
		File file = new File("c:/tmp/chiputao.pcm").getAbsoluteFile();
		byte ret[] = new byte[(int) file.length()];
		try(FileInputStream st = new FileInputStream(file)){
			st.read(ret);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	// bytes -> short
	private static Short bytes2short(byte hi, byte lo) {
		return (short)((hi & 0xff) << 8 | (lo & 0xff));
	}
	// byte[] to short[]
	private static Short[] byte2short(byte buf[]) {
		Short[] ret = new Short[buf.length/2];
		for(int i=0;i<ret.length;i++) {
			byte lo = buf[i*2  ];
			byte hi = buf[i*2+1];
			short st = bytes2short(hi, lo);
			ret[i] = st;
		}
		return ret;
	}
	// short -> bytes
	private static byte[] short2byte(Short st) {
		byte[] ret = new byte[2];
		ret[0] = (byte) (st & 0x00ff);
		ret[1] = (byte) (st >> 8); 
		return ret;
	}
	// short[] -> byte[]
	private static byte[] short2byte(Short buf[]) {
		byte[] ret = new byte[buf.length * 2];
		for(int i=0;i<buf.length;i+=2) {
			//ret[i*2]   = (byte) (buf[i] & 0x00ff);
			//ret[i*2+1] = (byte) (buf[i] >> 8); 
			ret[i*2]   = short2byte(buf[i])[0];
			ret[i*2+1] = short2byte(buf[i])[1]; 
		}
		return ret;
	}
	// Complex[] -> List<>
	private static List<Byte> array2List(byte[] buf){
		List<Byte> ret = new ArrayList<>();
		for(int i=0;i<buf.length;i++) {
			ret.add(buf[i]);
		}
		return ret;
	}
	// byte[] -> byte[]
	private static byte[] conv(byte[] buf) {
		int n = 1024;
		List<Byte> bufb = new ArrayList<>();
		Complex[] buff = new Complex[buf.length/2];
		for(int i=0;i<buff.length;i++) {
			Short st = bytes2short(buf[i*2+1], buf[i*2]);
			buff[i] = new Complex(st, 0);
		}
		
		for(int i=0;i<buff.length-n;i+=n) {
			Complex[] buff2 = Arrays.copyOfRange(buff, i, i+n);
			Complex[] buff3 = FFT.fft(buff2);
			Complex[] buff4 = half(buff3);
			byte[] buf5 = ifft(buff4);
			bufb.addAll(array2List(buf5));
		}
		byte[] ret = new byte[bufb.size()];
		for(int i=0;i<bufb.size();i++) {
			ret[i] = bufb.get(i);
		}

		/* DEBUG
		byte[] ret = new byte[buff.length*2];
		for(int i=0;i<buff.length;i++) {
			ret[i*2  ] = short2byte((short) buff[i].re())[0];
			ret[i*2+1] = short2byte((short) buff[i].re())[1];
		}
		*/
		return ret;
	}
	// 同じ長さの空白を追加、２倍長さにする、時間倍に、頻率を半分にする
	private static Complex[] half(Complex[] buf) {
		Complex[] ret = new Complex[buf.length*2];
		for(int i=0;i<ret.length;i++) {
			if(i<buf.length) {
				ret[i] = buf[i];
			}else {
				ret[i] = new Complex(0,0);
			}
		}
		return ret;
	}
	// ifft実施後、時域で間引きし長さを半分にする、時間が半分、頻率は倍になるが、half()と相殺。
	private static byte[] ifft(Complex[] buf) {
		Complex[] ift= FFT.ifft(buf);
		Short[] sa = new Short[ift.length/2];
		for(int i=0;i<sa.length;i++) {
			sa[i] = (short)(ift[i].re());	// only 1st half
		}
		return short2byte(sa);
	}
}
