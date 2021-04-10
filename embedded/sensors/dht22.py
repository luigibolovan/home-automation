import serial
import time
from firebase import firebase


fire = firebase.FirebaseApplication("https://something.firebaseio.com", None);

count = 0

ser = serial.Serial('/dev/cu.usbserial-143240', 9600)
time.sleep(1)

while True:
    ser_bytes = ser.readline()
    decoded_bytes = ser_bytes.decode()
    string = decoded_bytes.rstrip()

    if not 'Ro' in string:
        string = float(string)
        if count  == 1:
            fire.put('/air', 'humidity', string)
        if count == 2 :
            fire.put('/air', 'temperature',  string)
        if count == 3:
        #     fire.put('/air', 'smoke', string)
            count = 0
    print(decoded_bytes)

    time.sleep(5)
    count += 1

ser.close()
