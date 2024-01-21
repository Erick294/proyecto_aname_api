package com.aname.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.aname.api.service.AzureBlobService;
import com.aname.api.service.to.DocResponseDTO;

@RestController
@RequestMapping("/files")
public class AzureController {

	@Autowired
	private AzureBlobService azureBlobAdapter;

	@PostMapping
	public ResponseEntity<?> upload(@RequestParam MultipartFile file, @RequestParam String contenedor,
			@RequestParam String email) {
		try {
			validateFileSize(file, contenedor);

			DocResponseDTO doc = azureBlobAdapter.upload(file, contenedor, email);
			return ResponseEntity.ok(doc);
		} catch (MaxUploadSizeExceededException e) {
			return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
					.body("El tamaño del archivo excede el límite permitido: " + formatSize(e.getMaxUploadSize()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al cargar documento: " + e.getMessage());
		}
	}

	private void validateFileSize(MultipartFile file, String contenedor) {
		long maxSizeBytes = (contenedor.equals("fotografia")) ? 1 * 1024 * 1024 : 2 * 1024 * 1024;
		if (file.getSize() > maxSizeBytes) {
			throw new MaxUploadSizeExceededException(maxSizeBytes);
		}
	}


	private String formatSize(long sizeInBytes) {
		double sizeInKb = (double) sizeInBytes / (1024 * 1024);
		return String.format("%.2f MB", sizeInKb);
	}


	@DeleteMapping
	public ResponseEntity<Boolean> delete(@RequestParam String fileName) {

		azureBlobAdapter.deleteBlob(fileName);
		return ResponseEntity.ok().build();
	}

//	@GetMapping("/download")
//	public ResponseEntity<Resource> getFile(@RequestParam String fileName) throws URISyntaxException {
//
//		ByteArrayResource resource = new ByteArrayResource(azureBlobAdapter.getFile(fileName));
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
//
//		return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).headers(headers).body(resource);
//	}
}
