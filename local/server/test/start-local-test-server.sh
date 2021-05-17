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
echo "[SETUP] Flask installed"
fi

echo "Starting server..."
export FLASK_APP=home-automation-gateway-test.py
flask run --host=0.0.0.0