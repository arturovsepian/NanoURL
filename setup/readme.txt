To generate self-signed certificate:
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout /etc/ssl/private/nginx-selfsigned.key -out /etc/ssl/certs/nginx-selfsigned.crt
sudo openssl dhparam -out /etc/nginx/dhparam.pem 4096

To start/stop/restart:
sudo systemctl start nginx 
sudo systemctl stop nginx 
sudo systemctl restart nginx
