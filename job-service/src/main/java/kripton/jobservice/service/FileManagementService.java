package kripton.jobservice.service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.specialized.BlockBlobClient;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class FileManagementService {
	private Path fileStorageLocation;
	private final BlobServiceClient blobServiceClient;

	@Autowired
	public FileManagementService (@NonNull @Value ("${uploadDirectory:${java.io.tmpdir}/uploads}") String uploadDir,
	                              BlobServiceClient blobServiceClient) {
		this.blobServiceClient = blobServiceClient;
		Path configuredPath = Path.of(uploadDir);
		try {
			Files.createDirectories(configuredPath);
			fileStorageLocation = configuredPath;
		} catch (IOException e) {
			log.warn("Could not create the configured directory {}, falling back to system temp directory", uploadDir);
			Path tempPath = Path.of(System.getProperty("java.io.tmpdir"), "uploads");
			try {
				Files.createDirectories(tempPath);
				fileStorageLocation = tempPath;
			} catch (IOException ex) {
				log.error("Could not create fallback directory", ex);
				throw new RuntimeException("Could not create upload directory", ex);
			}
		}
	}

	public Path getFileStorageLocation() {
		return fileStorageLocation;
	}

	public String uploadToAzure(@NonNull MultipartFile file, String containerName) {
		String blobUrl ="";
		BlobContainerClient blobContainerClient = getBlobContainerClient(containerName);
		String filename = file.getOriginalFilename();
		BlockBlobClient blockBlobClient = blobContainerClient.getBlobClient(filename).getBlockBlobClient();
		try {
			if (blockBlobClient.exists()) {
				blockBlobClient.delete();
			}
			blockBlobClient.upload(new BufferedInputStream(file.getInputStream()), file.getSize(), true);
			blobUrl = blockBlobClient.getBlobUrl();
		} catch (IOException e) {
			blobUrl="";
			log.error("Error while processing file {}", e.getLocalizedMessage());
		}
		return blobUrl;
	}

	private @NonNull BlobContainerClient getBlobContainerClient(@NonNull String containerName) {
		// create container if not exists
		BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
		if (!blobContainerClient.exists()) {
			blobContainerClient.create();
		}
		return blobContainerClient;
	}
}
