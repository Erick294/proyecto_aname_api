package com.aname.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

	/**
    * Método para cargar archivos al FileServer de Azure
	* @param file - El archivo
	* @param contenedor - El nombre del contenedor en el que se guardará el archivo
	* @param email - Clave primaria que se liga con el archivo y del que recibirá el nombre
	* @return Respuesta ResponseEntity que comprueba si el archivo se puedo subir
	*/
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

	/**
	* Método que valida el tamaño del archivo.
    * Arroja MaxUploadSizeExceededException si el archivo es mayor que maxSizeBytes
	* @param file - MultipartFile para ser validado.
	* @param contenedor - Nombre del contenedor de archivos
	*/
	private void validateFileSize(MultipartFile file, String contenedor) {
		long maxSizeBytes = (contenedor.equals("fotografia")) ? 1 * 1024 * 1024 : 2 * 1024 * 1024;
		// si el tamaño del archivo es mayor que maxSizeBytes
		if (file.getSize() > maxSizeBytes) {
			throw new MaxUploadSizeExceededException(maxSizeBytes);
		}
	}


	/**
	* Método para mostrar darle formato al tamaño del archivo
	* @param sizeInBytes - el tamaño del archivo en bytes para formatear
	* @return el tamaño en MB de archivo en formato legible con 2 decimales
	*/
	private String formatSize(long sizeInBytes) {
		double sizeInKb = (double) sizeInBytes / (1024 * 1024);
		return String.format("%.2f MB", sizeInKb);
	}


	/**
	* Elimina un archivo del almacenamiento.
	* @param fileName - Nombre del archivo a eliminar.
	* @return Una entidad de respuesta con estado 200 (OK) y cuerpo que indique éxito o fracaso (con cuerpo).
	*/
	@DeleteMapping
	public ResponseEntity<Boolean> delete(@RequestParam String fileName) {
		azureBlobAdapter.deleteBlob(fileName);
		return ResponseEntity.ok().build();
	}
}
