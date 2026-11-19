package com.specsense.service;

import com.specsense.model.entity.Download;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DownloadFileMetadata {
    private final Path uploadRoot;

    public DownloadFileMetadata(@Value("${upload.path:./uploads}") String uploadPath) {
        this.uploadRoot = Paths.get(uploadPath).toAbsolutePath().normalize();
    }

    public void fillMissingSize(Download download) {
        if (download == null || (download.getFileSize() != null && !download.getFileSize().trim().isEmpty())) return;
        String url = download.getFileUrl();
        if (url == null || !url.startsWith("/uploads/")) return;
        try {
            URI uri = URI.create(url);
            String path = uri.getPath();
            if (uri.isAbsolute() || uri.getAuthority() != null || !path.startsWith("/uploads/")) return;
            Path root = uploadRoot.toRealPath();
            Path file = root.resolve(path.substring("/uploads/".length())).normalize();
            if (!file.startsWith(root)) return;
            file = file.toRealPath();
            if (!file.startsWith(root) || !Files.isRegularFile(file)) return;
            download.setFileSize(formatSize(Files.size(file)));
        } catch (IOException | IllegalArgumentException | SecurityException ignored) {
            // Missing/unreadable files must not prevent the download list from loading.
        }
    }

    static String formatSize(long bytes) {
        String[] units = { "B", "KB", "MB", "GB", "TB", "PB", "EB" };
        int unit = 0;
        long divisor = 1;
        while (unit < units.length - 1 && bytes / divisor >= 1024) {
            divisor *= 1024;
            unit++;
        }
        return BigDecimal.valueOf(bytes).divide(BigDecimal.valueOf(divisor), 2, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString() + " " + units[unit];
    }
}
