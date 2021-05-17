#!/bin/bash
if [ ! -d virtualenv/ ]; then
echo "[SETUP] Virtual environment does not exist"
echo "[SETUP] Creating virtual environment"
python3 -m venv virtualenv/
echo "[SETUP] Virtual environment created"

echo "[SETUP] Activate virtual environment"
source virtualenv/bin/activate

echo "[SETUP] Fetching Flask..."
pip install flask > /dev/null
echo "[SETUP] Fetching requests..."
pip install requests > /dev/null
echo "[SETUP] Fetching pycryptodome..."
pip install pycryptodome > /dev/null
echo "[SETUP] Flask installed"
fi

echo "Starting server..."
source virtualenv/bin/activate
export FLASK_APP=home-automation-sensors-gateway.py
flask run --host=0.0.0.0 --port=1310