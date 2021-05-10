from datetime import datetime
from Crypto.Cipher import AES
import base64

current_date = datetime.now()
BLOCK_SIZE = 16

pad = lambda s: s + (BLOCK_SIZE - len(s) % BLOCK_SIZE) * chr(BLOCK_SIZE - len(s) % BLOCK_SIZE)

key = bytearray.fromhex('A0A94477CA492BF4EA54C8B2A0617449')
iv = bytearray.fromhex('C196C6DB0B0EDC093E4C5F513C75B75A')

temperature = str(24) + '_' + str(current_date.date()) + '_' + str(current_date.time())
humidity = str(46)  + '_' + str(current_date.date()) + '_' + str(current_date.time())
methane_gas_concentration = str(335) + '_' + str(current_date.date()) + '_' + str(current_date.time())

door_lock = str(1) + '_' + str(current_date.date()) + '_' + str(current_date.time())
lights = str(1) + '_' + str(current_date.date()) + '_' + str(current_date.time())

cipher = AES.new(key, AES.MODE_CBC, iv)
print(base64.b64encode(cipher.encrypt(pad(temperature).encode())))
print(base64.b64encode(cipher.encrypt(pad(humidity).encode())))
print(base64.b64encode(cipher.encrypt(pad(methane_gas_concentration).encode())))
print('------------------------------------------------------------------------')
print(base64.b64encode(cipher.encrypt(pad(door_lock).encode())))
print(base64.b64encode(cipher.encrypt(pad(lights).encode())))