#!/bin/bash

# Download existing images from database to local uploads folder
# This script downloads all product images from unsplash URLs to the local uploads directory

UPLOAD_DIR="./uploads"
API_BASE="http://localhost:8080/spectsense"

# Create uploads directory if not exists
mkdir -p "$UPLOAD_DIR"

# Database connection parameters
DB_HOST="43.159.58.248"
DB_USER="youthful"
DB_PASS="Youthful@123456"
DB_NAME="youthful_db"

echo "Downloading existing product images..."

# Get all product image URLs from database
QUERY="SELECT DISTINCT image_url FROM product WHERE image_url IS NOT NULL AND image_url != '' AND image_url NOT LIKE 'http://localhost%' AND image_url NOT LIKE 'http://specsense-api%';"

# For MySQL, we need to run this query
mysql -h "$DB_HOST" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -N -e "$QUERY" 2>/dev/null | while read -r url; do
    if [ -n "$url" ]; then
        echo "Downloading: $url"
        filename=$(basename "$url" | sed 's/?.*//')
        filepath="$UPLOAD_DIR/$filename"

        if [ ! -f "$filepath" ]; then
            curl -s -o "$filepath" "$url"
            if [ $? -eq 0 ]; then
                echo "  -> Saved to: $filepath"
            else
                echo "  -> Failed to download"
            fi
        else
            echo "  -> Already exists: $filepath"
        fi
    fi
done

echo "Done!"
echo ""
echo "Note: You may need to update the image_url fields in the database to point to local paths."
echo "For example: UPDATE product SET image_url = '/uploads/image123.jpg' WHERE id = 1;"
