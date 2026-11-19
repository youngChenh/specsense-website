package com.specsense.service;

import com.specsense.mapper.DownloadMapper;
import com.specsense.model.entity.Download;
import com.specsense.service.impl.DownloadServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DownloadFileMetadataTest {
    @TempDir
    Path temp;

    private Download download(String url) {
        Download download = new Download();
        download.setFileUrl(url);
        return download;
    }

    @Test
    void readsLocalMetadataIncludingEncodedNamesAndQueryStrings() throws Exception {
        Files.write(temp.resolve("data sheet.pdf"), new byte[2560]);
        Download download = download("/uploads/data%20sheet.pdf?name=original.pdf");
        new DownloadFileMetadata(temp.toString()).fillMissingSize(download);
        assertEquals("2.5 KB", download.getFileSize());
    }

    @Test
    void formatsEmptySmallAndLargeFiles() throws Exception {
        assertEquals("0 B", DownloadFileMetadata.formatSize(0));
        assertEquals("1023 B", DownloadFileMetadata.formatSize(1023));
        assertEquals("1 KB", DownloadFileMetadata.formatSize(1024));
        assertEquals("100 MB", DownloadFileMetadata.formatSize(100L * 1024 * 1024));
        try (RandomAccessFile file = new RandomAccessFile(temp.resolve("large.pdf").toFile(), "rw")) {
            file.setLength(59L * 1024 * 1024);
        }
        Download download = download("/uploads/large.pdf");
        new DownloadFileMetadata(temp.toString()).fillMissingSize(download);
        assertEquals("59 MB", download.getFileSize());
    }

    @Test
    void preservesExistingSizesAndIgnoresUnavailableOrExternalFiles() throws Exception {
        Files.write(temp.resolve("local.pdf"), new byte[1024]);
        DownloadFileMetadata metadata = new DownloadFileMetadata(temp.toString());
        Download existing = download("/uploads/local.pdf");
        existing.setFileSize("Custom size");
        metadata.fillMissingSize(existing);
        assertEquals("Custom size", existing.getFileSize());
        for (String url : new String[] {null, "", "/uploads/missing.pdf", "https://example.com/uploads/local.pdf", "/uploads/bad%path", "/uploads/"}) {
            Download missing = download(url);
            assertDoesNotThrow(() -> metadata.fillMissingSize(missing));
            assertNull(missing.getFileSize());
        }
        assertDoesNotThrow(() -> metadata.fillMissingSize(null));
    }

    @Test
    void refusesPathsOutsideUploadDirectory() throws Exception {
        Path uploads = Files.createDirectory(temp.resolve("uploads"));
        Files.write(temp.resolve("outside.pdf"), new byte[123]);
        DownloadFileMetadata metadata = new DownloadFileMetadata(uploads.toString());
        for (String url : new String[] {"/uploads/../outside.pdf", "/uploads/%2e%2e/outside.pdf"}) {
            Download outside = download(url);
            metadata.fillMissingSize(outside);
            assertNull(outside.getFileSize());
        }
    }

    @Test
    void returnsSizesInSharedListAndCachesEnrichedRecords() throws Exception {
        Files.write(temp.resolve("old.pdf"), new byte[2048]);
        DownloadMapper mapper = mock(DownloadMapper.class);
        CacheService cache = mock(CacheService.class);
        DownloadServiceImpl service = service(mapper, cache);
        Download old = download("/uploads/old.pdf");
        old.setNameEn("Old datasheet");
        List<Download> records = new ArrayList<>();
        records.add(old);
        when(mapper.findAll(null)).thenReturn(records);
        List<Download> result = service.getAll(null, "en");
        assertEquals("2 KB", result.get(0).getFileSize());
        assertEquals("Old datasheet", result.get(0).getName());
        verify(cache).set("downloads:metadata-v1:en:null", records);
        when(cache.get(eq("downloads:metadata-v1:en:null"), any())).thenReturn(records);
        assertEquals("2 KB", service.getAll(null, "en").get(0).getFileSize());
        verify(mapper, times(1)).findAll(null);
    }

    @Test
    void populatesMissingSizesOnCreateUpdateAndDetail() throws Exception {
        Files.write(temp.resolve("saved.pdf"), new byte[1024]);
        DownloadMapper mapper = mock(DownloadMapper.class);
        CacheService cache = mock(CacheService.class);
        DownloadServiceImpl service = service(mapper, cache);
        when(mapper.insert(any())).thenReturn(1);
        when(mapper.update(any())).thenReturn(1);
        Download created = download("/uploads/saved.pdf");
        assertTrue(service.save(created));
        assertEquals("1 KB", created.getFileSize());
        Download updated = download("/uploads/saved.pdf");
        updated.setFileSize("  ");
        assertTrue(service.update(updated));
        assertEquals("1 KB", updated.getFileSize());
        Download detail = download("/uploads/saved.pdf");
        when(mapper.findById(1L)).thenReturn(detail);
        assertEquals("1 KB", service.getById(1L).getFileSize());
        verify(cache, times(2)).deleteByPattern("downloads:*");
    }

    private DownloadServiceImpl service(DownloadMapper mapper, CacheService cache) {
        DownloadServiceImpl service = new DownloadServiceImpl();
        ReflectionTestUtils.setField(service, "downloadMapper", mapper);
        ReflectionTestUtils.setField(service, "cacheService", cache);
        ReflectionTestUtils.setField(service, "fileMetadata", new DownloadFileMetadata(temp.toString()));
        return service;
    }
}
