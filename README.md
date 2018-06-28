# snd_speed_up
play pcm 2x speed with correct pitch

### POINT HOW-TO
 1, read a pcm file

 2, FFT 1st 1024 bytes

    * now you got many 1024 Complex

 3, remove 512 number one skips one

    * so you remain 512 Complex

 4, IFFT them back to time-domain
 
    * now it become 512 byte PCM

 5, goto #2 for next 1024 bytes PCM until EOF
 
 6, concat all you got at step #5, play it use same sample rate.

### PCM file
 sample rate = 16000, 16bits signed, 1 channel, little endian

### FFT use following
 https://introcs.cs.princeton.edu/java/97data/FFT.java.html
