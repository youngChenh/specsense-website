/**
 * Download existing images from database to local uploads folder
 * Run with: node scripts/download-images.js
 */

const fs = require('fs');
const path = require('path');
const https = require('https');
const http = require('http');

const UPLOAD_DIR = path.join(__dirname, '..', 'uploads');
const DB_HOST = '43.159.58.248';
const DB_USER = 'youthful';
const DB_PASS = 'Youthful@123456';
const DB_NAME = 'youthful_db';

// Create uploads directory if not exists
if (!fs.existsSync(UPLOAD_DIR)) {
    fs.mkdirSync(UPLOAD_DIR, { recursive: true });
    console.log('Created uploads directory');
}

// Sample image URLs from database (you can get these from your database)
// These are the initial data from specsense_db.sql
const imageUrls = [
    'https://images.unsplash.com/photo-1532187863486-abf9dbad1b69?w=600',
    'https://images.unsplash.com/photo-1581093450021-4a7360e9a6b5?w=600',
    'https://images.unsplash.com/photo-1576086213369-97a306d36557?w=600',
    'https://images.unsplash.com/photo-1518770660439-4636190af475?w=600',
    'https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=600',
    'https://images.unsplash.com/photo-1504868584819-f8e8b4b6d7e3?w=600',
    'https://images.unsplash.com/photo-1565361484688-a3d1ae0c31f7?w=600',
    'https://images.unsplash.com/photo-1578575437130-527eed3abbec?w=800',
    'https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800',
    'https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=800',
];

function downloadImage(url, dest) {
    return new Promise((resolve, reject) => {
        const file = fs.createWriteStream(dest);

        const protocol = url.startsWith('https') ? https : http;

        console.log(`Downloading: ${url}`);

        protocol.get(url, (response) => {
            // Handle redirects
            if (response.statusCode === 301 || response.statusCode === 302) {
                const redirectUrl = response.headers.location;
                console.log(`  Redirect to: ${redirectUrl}`);
                file.close();
                downloadImage(redirectUrl, dest).then(resolve).catch(reject);
                return;
            }

            if (response.statusCode !== 200) {
                file.close();
                reject(new Error(`HTTP ${response.statusCode}`));
                return;
            }

            response.pipe(file);

            file.on('finish', () => {
                file.close();
                console.log(`  Saved to: ${dest}`);
                resolve();
            });
        }).on('error', (err) => {
            fs.unlink(dest, () => {}); // Delete the file on error
            reject(err);
        });
    });
}

async function main() {
    console.log('Starting image download...\n');

    let downloaded = 0;
    let skipped = 0;

    for (const url of imageUrls) {
        const filename = path.basename(url.split('?')[0]);
        const dest = path.join(UPLOAD_DIR, filename);

        if (fs.existsSync(dest)) {
            console.log(`Skipping (already exists): ${filename}`);
            skipped++;
            continue;
        }

        try {
            await downloadImage(url, dest);
            downloaded++;
        } catch (err) {
            console.log(`  Error: ${err.message}`);
        }
    }

    console.log(`\nDownload complete!`);
    console.log(`  Downloaded: ${downloaded}`);
    console.log(`  Skipped: ${skipped}`);
    console.log(`\nNote: You need to update the image_url fields in the database to use local paths.`);
    console.log(`For example: UPDATE product SET image_url = '/uploads/${path.basename(imageUrls[0].split('?')[0])}' WHERE id = 1;`);
}

main().catch(console.error);
