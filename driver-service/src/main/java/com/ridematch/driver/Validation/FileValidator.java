package com.ridematch.driver.Validation;

import com.ridematch.driver.configuration.UploadValidationConfig;
import com.ridematch.driver.enums.ErrorCode;
import com.ridematch.driver.excpetion.UploadException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileValidator {

    private final UploadValidationConfig uploadValidationConfig;
    private final Tika tika = new Tika();

    public void validate(
            boolean isRequired,
            MultipartFile licenseDocument,
            MultipartFile registrationDocument,
            List<MultipartFile> vehicleImages) {

        validateFileOrImage(isRequired, licenseDocument, ErrorCode.LICENSE_MISSING);
        validateFileOrImage(isRequired, registrationDocument, ErrorCode.REGISTRATION_MISSING);

        if (vehicleImages != null) {
            for (MultipartFile img : vehicleImages) {
                validateImageFile(isRequired, img);
            }
        } else if (isRequired) {
            throw new UploadException(ErrorCode.VEHICLE_IMAGE_MISSING);
        }
    }

    private void validateFileOrImage(
            boolean isRequired, MultipartFile file, ErrorCode missingCode) {
        if (provided(file)) {
            if (isRequired) throw new UploadException(missingCode);
            return;
        }
        if (exceedsMaxBytes(file)) throw new UploadException(ErrorCode.SIZE_LIMIT_EXCEEDED);

        Path tmp = null;
        try {
            tmp = toTemp(file);
            if (exceedsMaxBytes(tmp)) throw new UploadException(ErrorCode.SIZE_LIMIT_EXCEEDED);

            String mime = tika.detect(tmp);
            if ("application/pdf".equals(mime)) {
                validatePdf(tmp);
            } else if (mime != null && mime.startsWith("image/")) {
                validateImage(tmp);
            } else {
                throw new UploadException(ErrorCode.FILE_TYPE_BLOCKED);
            }
        } catch (IOException e) {
            throw new UploadException(ErrorCode.INTERNAL_SERVER);
        } finally {
            deleteQuietly(tmp);
        }
    }

    private void validateImageFile(boolean isRequired, MultipartFile file) {
        if (provided(file)) {
            if (isRequired) throw new UploadException(ErrorCode.VEHICLE_IMAGE_MISSING);
            return;
        }
        if (exceedsMaxBytes(file)) throw new UploadException(ErrorCode.SIZE_LIMIT_EXCEEDED);

        Path tmp = null;
        try {
            tmp = toTemp(file);
            if (exceedsMaxBytes(tmp)) throw new UploadException(ErrorCode.SIZE_LIMIT_EXCEEDED);

            String mime = tika.detect(tmp);
            if (mime == null || !mime.startsWith("image/")) {
                throw new UploadException(ErrorCode.FILE_TYPE_BLOCKED);
            }
            validateImage(tmp);
        } catch (IOException e) {
            throw new UploadException(ErrorCode.INTERNAL_SERVER);
        } finally {
            deleteQuietly(tmp);
        }
    }

    private void validatePdf(Path file) {
        try (PDDocument doc = Loader.loadPDF(file.toFile())) {
            if (doc.isEncrypted()) {
                throw new UploadException(ErrorCode.PDF_ENCRYPTED);
            }
            if (doc.getNumberOfPages() <= 0) {
                throw new UploadException(ErrorCode.PDF_EMPTY);
            }
        } catch (IOException e) {
            throw new UploadException(ErrorCode.FILE_TYPE_BLOCKED);
        }
    }

    private void validateImage(Path file) {
        try (ImageInputStream iis = ImageIO.createImageInputStream(file.toFile())) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                throw new UploadException(ErrorCode.IMAGE_INVALID);
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(iis, true, true);
                int w = reader.getWidth(0);
                int h = reader.getHeight(0);
                if (w <= 0 || h <= 0) {
                    throw new UploadException(ErrorCode.IMAGE_INVALID);
                }
                if (w > uploadValidationConfig.getMaxImageWidth()
                        || h > uploadValidationConfig.getMaxImageHeight()) {
                    throw new UploadException(ErrorCode.DIMENSIONS_TOO_LARGE);
                }
            } finally {
                reader.dispose();
            }
        } catch (IOException e) {
            throw new UploadException(ErrorCode.IMAGE_INVALID);
        }
    }

    private static boolean provided(MultipartFile f) {
        return f == null || f.isEmpty();
    }

    private boolean exceedsMaxBytes(MultipartFile f) {
        long size = f.getSize();
        return size > 0 && size > uploadValidationConfig.getMaxBytes();
    }

    private boolean exceedsMaxBytes(Path p) throws IOException {
        return Files.size(p) > uploadValidationConfig.getMaxBytes();
    }

    private Path toTemp(MultipartFile mf) throws IOException {
        Path tmp = Files.createTempFile("upload-", ".bin");
        try (InputStream in = mf.getInputStream()) {
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
        }
        return tmp;
    }

    private void deleteQuietly(Path tmp) {
        if (tmp == null) return;
        try {
            Files.deleteIfExists(tmp);
        } catch (IOException ignored) {
        }
    }
}
