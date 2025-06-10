#!/bin/bash
# Exit immediately if a command exits with a non-zero status.
set -e

echo "--- VNC STARTUP SCRIPT ---"
echo "--- Printing Environment Variables for Debugging ---"
printenv
echo "----------------------------------------------------"

# Use environment variables with sensible defaults as a fallback
VNC_RES=${VNC_RESOLUTION:-1920x1080}
VNC_DEPTH=${VNC_COL_DEPTH:-24}
VNC_PORT_NUM=${VNC_PORT:-5901}
NOVNC_PORT_NUM=${NOVNC_PORT:-6901}

# Ensure the .vnc directory exists before trying to write to it.
mkdir -p /root/.vnc

# Create a custom xstartup file
cat <<EOF > /root/.vnc/xstartup
#!/bin/sh
unset SESSION_MANAGER
unset DBUS_SESSION_BUS_ADDRESS
exec fluxbox
EOF

chmod +x /root/.vnc/xstartup

# Start the VNC server in the background with no security.
echo "Starting VNC server on :1 (Port: ${VNC_PORT_NUM}, Geometry: ${VNC_RES}, Depth: ${VNC_DEPTH})"
vncserver :1 -geometry ${VNC_RES} -depth ${VNC_DEPTH} -rfbport ${VNC_PORT_NUM} -localhost no -SecurityTypes None --I-KNOW-THIS-IS-INSECURE

# Start the noVNC web proxy in the background
echo "Starting noVNC proxy on port ${NOVNC_PORT_NUM}"
echo ">>> To connect automatically, open your browser to: http://localhost:${NOVNC_PORT_NUM}/vnc.html?autoconnect=true"
websockify --web /usr/share/novnc/ ${NOVNC_PORT_NUM} localhost:${VNC_PORT_NUM} &

# Give services a moment to initialize
sleep 2

# Tail the VNC log to the main Docker log for easier debugging
tail -f /root/.vnc/*.log &

# Execute the main process (your Java application)
echo "Starting Spring Boot application..."
java -jar app.jar 2>&1
