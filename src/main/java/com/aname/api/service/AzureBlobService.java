package com.aname.api.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.aname.api.model.DocumentoCompetidores;
import com.aname.api.repository.ICompetidorRepo;
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

	@Autowired
	private ICompetidorRepo competidorRepo;

	private String tokenSAS = "?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2024-03-29T22:19:13Z&st=2024-01-29T14:19:13Z&spr=https,http&sig=5n44N%2BrVDmWYMuwzu0fJDpNDg9knKZErKMN6uetY2gE%3D";

	public DocResponseDTO upload(MultipartFile multipartFile, String contenedor, String email) throws Exception {
		String username = extractUsernameFromEmail(email);
		String containerName = contenedor;

		// Establecer límites de tamaño según el tipo de contenido

		String originalFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String fileExtension = StringUtils.getFilenameExtension(originalFileName);

		// Verificar el tipo de archivo permitido según el contenedor
		if ((contenedor.equals("fotografia") && !isImageFileAllowed(fileExtension))) {
			throw new Exception(
					"Solo se permiten archivos de imagen con extensiones jpg, jpeg, png para el contenedor de fotografía.");
		} else if (!contenedor.equals("fotografia") && !isPdfFile(fileExtension)) {
			throw new Exception("Solo se permiten archivos PDF para el contenedor especificado.");
		}

		String uniqueFileName = containerName + "_" + username + "_" + LocalDateTime.now() + "." + fileExtension;

		BlobClient blob = blobServiceClient.getBlobContainerClient(containerName).getBlobClient(uniqueFileName);

		blob.upload(multipartFile.getInputStream(), multipartFile.getSize(), true);

		DocResponseDTO doc = new DocResponseDTO();
		doc.setLink(getBlobUrl(containerName, uniqueFileName));
		doc.setExtension(multipartFile.getContentType());
		doc.setNombre(uniqueFileName);
		doc.setUsername(email);

		return doc;
	}

	private boolean isImageFileAllowed(String fileExtension) {
		List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");
		return allowedExtensions.contains(fileExtension.toLowerCase());
	}

	private boolean isPdfFile(String fileExtension) {
		return fileExtension.toLowerCase().equals("pdf");
	}

	public List<DocResponseDTO> listarDocumentosCompetidor(List<DocumentoCompetidores> docs) {
		List<DocResponseDTO> listaDocs = new ArrayList<DocResponseDTO>();
		for (DocumentoCompetidores d : docs) {
			DocResponseDTO doc = new DocResponseDTO();
			doc.setExtension(d.getExtension());
			doc.setLink(d.getLink() + tokenSAS);
			doc.setNombre(d.getNombre());
			doc.setUsername(d.getCompetidor().getUsuario().getEmail());
			listaDocs.add(doc);

		}

		return listaDocs;
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
