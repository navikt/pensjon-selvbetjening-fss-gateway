SERVICEUSER_USERNAME=$(cat /secrets/serviceuser/username) || true
export SERVICEUSER_USERNAME
SERVICEUSER_PASSWORD=$(cat /secrets/serviceuser/password) || true
export SERVICEUSER_PASSWORD
SERVICEUSER2_USERNAME=$(cat /secrets/serviceuser2/username) || true
export SERVICEUSER2_USERNAME
SERVICEUSER2_PASSWORD=$(cat /secrets/serviceuser2/password) || true
export SERVICEUSER2_PASSWORD
SERVICEUSER3_USERNAME=$(cat /secrets/serviceuser3/username) || true
export SERVICEUSER3_USERNAME
SERVICEUSER3_PASSWORD=$(cat /secrets/serviceuser3/password) || true
export SERVICEUSER3_PASSWORD
