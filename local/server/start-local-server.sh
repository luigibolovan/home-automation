#!/bin/bash

if [ ! -d virtualenv/ ]; then
echo "virtual env does not exist"
echo "creating virtual environment"
python3 -m venv virtualenv/

echo "virtual environment created"
echo "activate virtual environment"
source virtualenv/bin/activate

echo "fetching flask..."
pip install flask > /dev/null
echo "flask installed"

fi

export FLASK_APP=home-automation-gateway.py
flask run --host=0.0.0.0