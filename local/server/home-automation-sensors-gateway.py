from flask import Flask
from flask import request
import json
import requests
from datetime import datetime
from Crypto.Cipher import AES
import base64

app = Flask(__name__)

remote_server = "http://35.158.221.217:1310/"

TEMP = "Temperature"
HUM  = "Humidity"
GAS  = "MethaneGasConcentration"

BLOCK_SIZE = 16

pad = lambda s: s + (BLOCK_SIZE - len(s) % BLOCK_SIZE) * chr(BLOCK_SIZE - len(s) % BLOCK_SIZE)

unpad = lambda s: s[:-ord(s[len(s)-1:])]

def encrypt_sensors_data(plain_sensors_json):
    key = bytearray.fromhex('A0A94477CA492BF4EA54C8B2A0617449')
    iv = bytearray.fromhex('C196C6DB0B0EDC093E4C5F513C75B75A')

    temperature_value = str(plain_sensors_json[TEMP])
    humidity_value    = str(plain_sensors_json[HUM])
    gas_value         = str(plain_sensors_json[GAS])

    current_date = datetime.now()
    date = current_date.date()
    time = current_date.time()

    temperature_data = temperature_value + '_' + str(date) + '_' + str(time)
    humidity_data    = humidity_value + '_' + str(date) + '_' + str(time)
    gas_data         = gas_value + '_' + str(date) + '_' + str(time)

    cipher = AES.new(key, AES.MODE_CBC, iv)
    encrypted_temperature = "".join(map(chr, base64.b64encode(cipher.encrypt(pad(temperature_data).encode()))))
    cipher = AES.new(key, AES.MODE_CBC, iv)
    encrpyted_humidity = "".join(map(chr, base64.b64encode(cipher.encrypt(pad(humidity_data).encode()))))
    cipher = AES.new(key, AES.MODE_CBC, iv)
    encrypted_gas_concentration = "".join(map(chr, base64.b64encode(cipher.encrypt(pad(gas_data).encode()))))

    encrypted_sensors_data = dict()
    encrypted_sensors_data[TEMP] = encrypted_temperature
    encrypted_sensors_data[HUM]  = encrpyted_humidity
    encrypted_sensors_data[GAS]  = encrypted_gas_concentration
    
    return encrypted_sensors_data



@app.route('/')
def hello_world():
    return "use route '/sensors' to post temperature, humidity rate and methane gase ppm"

# used to send to remote server the data from the sensors
@app.route('/sensors', methods=['POST'])
def post_sensors_data():
    sensors_json = request.json
    encrypted_json = encrypt_sensors_data(sensors_json)
    url = remote_server + "atmosphere/"
    requests.post(url, json = encrypted_json)
    # print(encrypted_json)
    return ('', 204)

if __name__ == '__main__':
    app.run()