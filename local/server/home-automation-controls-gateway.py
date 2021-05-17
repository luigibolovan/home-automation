from flask import Flask
from flask import request
import json
import requests
from datetime import datetime
from Crypto.Cipher import AES
import base64

app = Flask(__name__)

remote_server = "http://35.158.221.217:1310/"

DOOR    = "DoorLock"
LIGHTS  = "Lights"

BLOCK_SIZE = 16

pad = lambda s: s + (BLOCK_SIZE - len(s) % BLOCK_SIZE) * chr(BLOCK_SIZE - len(s) % BLOCK_SIZE)

unpad = lambda s: s[:-ord(s[len(s)-1:])]

def decrypt_controls_data(encrypted_controls_json):
    key = bytearray.fromhex('A0A94477CA492BF4EA54C8B2A0617449')
    iv = bytearray.fromhex('C196C6DB0B0EDC093E4C5F513C75B75A')

    doorlock_cipher = encrypted_controls_json[DOOR]
    lights_cipher   = encrypted_controls_json[LIGHTS]

    cipher = AES.new(key, AES.MODE_CBC, iv)
    doorlock_data = "".join(map(chr, unpad(cipher.decrypt(base64.b64decode(doorlock_cipher)))))
    cipher = AES.new(key, AES.MODE_CBC, iv)
    lights_data   = "".join(map(chr, unpad(cipher.decrypt(base64.b64decode(lights_cipher)))))

    doorlock_tokens = doorlock_data.split("_")
    lights_tokens   = lights_data.split("_")

    decrypted_controls_data          = dict()
    decrypted_controls_data[DOOR]    = int(doorlock_tokens[0])
    decrypted_controls_data[LIGHTS]  = int(lights_tokens[0])
    
    return decrypted_controls_data



@app.route('/')
def hello_world():
    return "use route '/controls' to get door status from the remote server"

# used to send to remote server the data from the sensors
@app.route('/controls', methods=['GET'])
def fetch_controls_states():
    url = remote_server + "control/latest"
    handler = requests.get(url)
    decrypted_json = decrypt_controls_data(json.loads(handler.text))
    
    return decrypted_json

if __name__ == '__main__':
    app.run()