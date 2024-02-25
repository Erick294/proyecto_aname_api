package com.aname.api.service;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

	//Token para conectar con el File Server en Azure
	private String tokenSAS = "?sv=2022-11-02&ss=bfqt&srt=sco&sp=rwdlacupiytfx&se=2024-03-29T22:19:13Z&st=2024-01-29T14:19:13Z&spr=https,http&sig=5n44N%2BrVDmWYMuwzu0fJDpNDg9knKZErKMN6uetY2gE%3D";

	/**
	* Sube un archivo al File Server. Este método es llamado por el servicio web para subir una imagen o un PDF
	* @param multipartFile - El archivo a subir
	* @param contenedor - Nombre del contenedor en Azure
	* @param email - El email del usuario que cargó el archivo
	* @return El DocResponseDTO con el resultado de la carga (nulo si se produjo un error)
	*/
	public DocResponseDTO upload(MultipartFile multipartFile, String contenedor, String email) throws Exception {
		String username = extractUsernameFromEmail(email);
		String containerName = contenedor;

		// Establecer límites de tamaño según el tipo de contenido

		String originalFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String fileExtension = StringUtils.getFilenameExtension(originalFileName);

		// Verificar el tipo de archivo permitido según el contenedor
		// Solo se permiten archivos de imagen con extensiones jpg jpeg png.
		if ((contenedor.equals("fotografia") && !isImageFileAllowed(fileExtension))) {
			throw new Exception(
					"Solo se permiten archivos de imagen con extensiones jpg, jpeg, png para el contenedor de fotografía.");
		// Solo se permiten archivos PDF para contenedor especificado.
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

	/**
	* Verifica si se permite subir la extensión de archivo. Esto se utiliza para evitar errores de carga de archivos en imágenes
	* @param fileExtension - la extensión del archivo a comprobar
	* @return verdad si la extensión de archivo está permitida falso de otro modo
	*/
	private boolean isImageFileAllowed(String fileExtension) {
		List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");
		return allowedExtensions.contains(fileExtension.toLowerCase());
	}

	/**
	* Verifica si la extensión de archivo es PDF. Se utiliza para detectar si estamos tratando con un archivo PDF
	* @param fileExtension - la extensión del archivo a comprobar
	* @return verdad si la extensión de archivo es PDF falso si no lo es
	*/
	private boolean isPdfFile(String fileExtension) {
		return fileExtension.toLowerCase().equals("pdf");
	}

	/**
	* Método que permite realizar una lista de documentos de competidores, se envía el token de acceso para poder descargar los archivos
	* @param docs - La lista de documentos de los competidores
	* @return Enviar una lista de documentos de respuesta con los datos correspondientes a la información específicada
	*/
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

	/**
	* Descarga un archivo desde el File Server a partir del nombre del archivo
	* @param fileName - el nombre del archivo
	*/
	public byte[] getFile(String fileName) throws URISyntaxException {

		BlobClient blob = blobContainerClient.getBlobClient(fileName);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		blob.download(outputStream);
		final byte[] bytes = outputStream.toByteArray();
		return bytes;

	}

	/**
	* Enumera todos los puntos de guasrdado en el File Server
	* @return Una lista con los nombres de los puntos guardado del File Server
	*/
	public List<String> listBlobs() {

		PagedIterable<BlobItem> items = blobContainerClient.listBlobs();
		List<String> names = new ArrayList<String>();
		for (BlobItem item : items) {
			names.add(item.getName());
		}
		return names;

	}

	/**
	* Elimina un BLOB con el nombre dado
	* @param blobName - el nombre de l BLOB
	* @return verdad si la eliminación fue exitosa falsa si hubo un error en la eliminación
	*/
	public Boolean deleteBlob(String blobName) {

		BlobClient blob = blobContainerClient.getBlobClient(blobName);
		blob.delete();
		return true;
	}

	/**
	* Metodo para obtener la URL de un BLOB del File Server
	* @param containerName - Nombre del contenedor que contiene la BLOB
	* @param blobName - Nombre de BLOB 
	* @return String con la URL de la BLOB
	*/
	public String getBlobUrl(String containerName, String blobName) {
		// Construir y devolver la URL del blob
		return String.format("https://%s.blob.core.windows.net/%s/%s", blobServiceClient.getAccountName(),
				containerName, blobName);
	}

	/**
	* Extrae el nombre de usuario de un email. Esto se utiliza para comprobar si el email es válido o no
	* @param email - El email del usuario
	* @return El nombre de usuario si es válido
	*/
	private String extractUsernameFromEmail(String email) {
		// Utilizar una expresión regular para extraer cualquier carácter antes del '@'
		Pattern pattern = Pattern.compile("^(.+)@.*$");
		Matcher matcher = pattern.matcher(email);

		// Retorna el primer elemento del matcher.
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
