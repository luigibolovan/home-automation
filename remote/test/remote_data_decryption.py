from datetime import datetime
from Crypto.Cipher import AES
import base64

current_date = datetime.now()
BLOCK_SIZE = 16

pad = lambda s: s + (BLOCK_SIZE - len(s) % BLOCK_SIZE) * chr(BLOCK_SIZE - len(s) % BLOCK_SIZE)

unpad = lambda s: s[:-ord(s[len(s)-1:])]

key = bytearray.fromhex('A0A94477CA492BF4EA54C8B2A0617449')
iv = bytearray.fromhex('C196C6DB0B0EDC093E4C5F513C75B75A')

ciphertext = "fph0e8xwUIb6/s9iWeWAg69VMN+C2S/XP3zt02lfgdE="

# renew the cipher in order to be able to decrypt easily...
cipher = AES.new(key, AES.MODE_CBC, iv)
print(unpad(cipher.decrypt(base64.b64decode(ciphertext))))
