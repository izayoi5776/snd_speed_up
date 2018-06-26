# snd_speed_up
play pcm 2x speed with correct pitch

### POINT HOW-TO
 1, read a pcm file
 2, FFT 1st 1024 bytes
 
    * now you got many 1024 Complex
 3, add 1024 blank Complex after it
 
    * so you got 2048 Complex
 4, IFFT them back
 
    * now it become 2048 byte PCM
 5, take 1st 1024 byte
 
    * drop 2nd 1024 bytes
 6, goto #2 for next 1024 bytes PCM until EOF
 
 7, concat all you got at step #5, play it use double sample rate.

### PCM file
 sample rate = 16000, 16bits signed, 1 channel, little endian

### FFT use following
 https://introcs.cs.princeton.edu/java/97data/FFT.java.html
