-- blank pwd for user travis (could not set it via env vars in docker compose)
SET PASSWORD FOR 'root' = '';
FLUSH PRIVILEGES;
