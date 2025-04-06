package kripton.candidateservice.service;

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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileManagementService {
	private final Path fileStorageLocation;
	private final BlobServiceClient blobServiceClient;

	@Autowired
	public FileManagementService(@NonNull @Value ("${uploadDirectory}") String uploadDir,
	                             BlobServiceClient blobServiceClient) {
		fileStorageLocation = Path.of(uploadDir);
		this.blobServiceClient = blobServiceClient;
		try {
			Files.createDirectories(fileStorageLocation);
		} catch (IOException e) {
			log.error("Could not create the directory where the uploaded files will be stored.", e);
		}
	}

	public Path getFileStorageLocation() {
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (IOException e) {
			log.error("Could not create the directory where the uploaded file will be stored.", e);
		}
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
			 blobUrl = blockBlobClient.getBlobUrl ();
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
