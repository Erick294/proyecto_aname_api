package com.aname.api.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.aname.api.service.to.DocResponseDTO;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;

@Component
public class AzureBlobService {

	@Autowired
	BlobServiceClient blobServiceClient;

	@Autowired
	BlobContainerClient blobContainerClient;

	public DocResponseDTO upload(MultipartFile multipartFile, String contenedor, String email) throws IOException {
		String username = extractUsernameFromEmail(email);

		// nombre del contenedor al que se subirá el documento o foto
		// String containerName = "fotos";
		String containerName = contenedor;
		// String containerName = "pagos";
		// String containerName = "consentimientos";

		// Obtener el nombre original del archivo
		String originalFileName = multipartFile.getOriginalFilename();

		// Obtener la extensión del archivo
		String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));

		// Generar un nombre único que incluya la extensión del archivo
		String uniqueFileName = containerName + "_" + username + "_" + UUID.randomUUID() + fileExtension;

		BlobClient blob = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(uniqueFileName);

		blob.upload(multipartFile.getInputStream(), multipartFile.getSize(), true);

		DocResponseDTO doc = new DocResponseDTO();
		doc.setLink(getBlobUrl(containerName, uniqueFileName));
		doc.setExtension(multipartFile.getContentType());
		doc.setNombre(uniqueFileName);
		doc.setUsername(email);

		return doc;
	}

	public byte[] getFile(String fileName) throws URISyntaxException {

		BlobClient blob = blobContainerClient.getBlobClient(fileName);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		blob.download(outputStream);
		final byte[] bytes = outputStream.toByteArray();
		return bytes;

	}

	public List<String> listBlobs() {

		PagedIterable<BlobItem> items = blobContainerClient.listBlobs();
		List<String> names = new ArrayList<String>();
		for (BlobItem item : items) {
			names.add(item.getName());
		}
		return names;

	}

	public Boolean deleteBlob(String blobName) {

		BlobClient blob = blobContainerClient.getBlobClient(blobName);
		blob.delete();
		return true;
	}

	public String getBlobUrl(String containerName, String blobName) {
		// Construir y devolver la URL del blob
		return String.format("https://%s.blob.core.windows.net/%s/%s", blobServiceClient.getAccountName(),
				containerName, blobName);
	}

	private String extractUsernameFromEmail(String email) {
		// Utilizar una expresión regular para extraer cualquier carácter antes del '@'
		Pattern pattern = Pattern.compile("^(.+)@.*$");
		Matcher matcher = pattern.matcher(email);

		if (matcher.find()) {
			return matcher.group(1);
		}

		// Si no se encuentra ninguna coincidencia, devolver un valor predeterminado o
		// lanzar una excepción
		// En este caso, devolvemos el correo electrónico completo si no se encuentra
		// ningún carácter antes del '@'
		return email;
	}

}
