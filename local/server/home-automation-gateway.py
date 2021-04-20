from flask import Flask
from flask import request
import json

app = Flask(__name__)

plm = 0

@app.route('/')
def hello_world():
    return "use route '/sensors' to post temperature, humidity rate and methane gase ppm \n \
            use route '/door to get door status from the remote server \n \
            use route '/lights to get ligths status from the remote server \n"

# used to send to remote server the data from the sensors
@app.route('/sensors', methods=['POST'])
def print_json():
    print(request.json)
    return {}

if __name__ == '__main__':
    app.run()