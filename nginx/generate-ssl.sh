#!/bin/bash
# ============================================================
# Script tạo SSL self-signed certificate cho development/testing
# Cho production: thay bằng Let's Encrypt hoặc cert thật
# ============================================================

SSL_DIR="./nginx/ssl"
mkdir -p "$SSL_DIR"

echo "📦 Đang tạo SSL self-signed certificate..."

openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout "$SSL_DIR/key.pem" \
    -out "$SSL_DIR/cert.pem" \
    -subj "/C=VN/ST=HoChiMinh/L=HoChiMinh/O=EmployeeSystem/OU=Dev/CN=localhost" \
    -addext "subjectAltName=DNS:localhost,IP:127.0.0.1"

echo "✅ Đã tạo SSL certificate tại $SSL_DIR/"
echo "   - cert.pem (certificate)"
echo "   - key.pem  (private key)"
echo ""
echo "⚠️  Đây là self-signed cert, dùng cho local/dev."
echo "   Cho production, dùng Let's Encrypt hoặc cert thật."
