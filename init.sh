SERVICEUSER_USERNAME=$(cat /secrets/serviceuser/username) || true
export SERVICEUSER_USERNAME
SERVICEUSER_PASSWORD=$(cat /secrets/serviceuser/password) || true
export SERVICEUSER_PASSWORD
SERVICEUSER2_USERNAME=$(cat /secrets/serviceuser2/username) || true
export SERVICEUSER2_USERNAME
SERVICEUSER2_PASSWORD=$(cat /secrets/serviceuser2/password) || true
export SERVICEUSER2_PASSWORD
